package com.example.demospringsecurity.service;

import com.example.demospringsecurity.dto.ReportUserDto;
import com.example.demospringsecurity.dto.UserDto;
import com.example.demospringsecurity.dto.request.AuthChangePassword;
import com.example.demospringsecurity.dto.request.AuthRequest;
import com.example.demospringsecurity.dto.request.ChangeInfoUserRequest;
import com.example.demospringsecurity.dto.request.RegisterRequest;
import com.example.demospringsecurity.exceptions.TokenNotFoundException;
import com.example.demospringsecurity.exceptions.UserNotFoundException;
import com.example.demospringsecurity.mapper.UserChangeInfoMapper;
import com.example.demospringsecurity.mapper.UserMapper;
import com.example.demospringsecurity.model.ExcelGenerator;
import com.example.demospringsecurity.model.PasswordResetToken;
import com.example.demospringsecurity.model.UserInfo;
import com.example.demospringsecurity.repository.*;
import com.example.demospringsecurity.response.auth.LoginResponse;
import com.example.demospringsecurity.response.auth.PasswordChangeResponse;
import com.example.demospringsecurity.response.auth.PasswordResetTokenResponse;
import com.example.demospringsecurity.response.auth.RegisterResponse;
import com.example.demospringsecurity.response.user.ChangeInfoUserResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {
    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    UserPostRepository userPostRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    FriendRepository friendRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserChangeInfoMapper userChangeInfoMapper;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    FileService fileService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    OtpService otpService;

    /**
     * đăng ký tài khoản
     *
     * @param registerRequest
     * @return
     */
    public RegisterResponse registerUser(RegisterRequest registerRequest) {
        RegisterResponse registerResponse = new RegisterResponse();
//            check trùng email
        if (userInfoRepository.existsUserInfoByUserEmail(registerRequest.getUserEmail())) {
            registerResponse.setMessage("Email already exists!");
            registerResponse.setSuccess(false);
            registerResponse.setUserInfo(null);
        } else if (userInfoRepository.existsUserInfosByUserName(registerRequest.getUserName())) {
            registerResponse.setMessage("Username already exists!");
            registerResponse.setSuccess(false);
            registerResponse.setUserInfo(null);
        } else {
            UserInfo userInfo = userMapper.toEntity(registerRequest);
            userInfo.setUserPassword(passwordEncoder.encode(registerRequest.getUserPassword()));
            userInfo.setRoles("ROLE_USER");
            userInfo.setUserAvatar("avatardefault.jpg");
            UserInfo userInfoSaved = userInfoRepository.save(userInfo);
            UserDto userDto = new UserDto();
            userDto.setUserId(userInfoSaved.getUserId());
            userDto.setUserEmail(userInfoSaved.getUserEmail());
            userDto.setUserName(userInfoSaved.getUserName());
            userDto.setUserAvatar(userInfoSaved.getUserAvatar());
            registerResponse.setMessage("Register success!");
            registerResponse.setSuccess(true);
            registerResponse.setUserInfo(userDto);
        }
        return registerResponse;
    }

    public LoginResponse authenticateAndGetOtp(AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUserName(),
                    authRequest.getPassword()));
            LoginResponse loginResponse = new LoginResponse();
            if (authentication.isAuthenticated()) {
                loginResponse.setStatus("200");
                loginResponse.setUserName(authRequest.getUserName());
                loginResponse.setOtp(otpService.sendOtp(authRequest.getUserName()));
            }
            return loginResponse;
        } catch (AuthenticationException e) {
            e.printStackTrace();
            throw new BadCredentialsException("adu");
        }
    }

    /**
     * quên mật khẩu trả về token
     *
     * @param username
     * @return
     */
    public PasswordResetTokenResponse forgotPassword(String username) {
        PasswordResetTokenResponse passwordResetTokenResponse = new PasswordResetTokenResponse();
        //        check tài khoản tồn tại bởi username
        UserInfo userInfo = userInfoRepository.findByUserName(username)
                .orElseThrow(() -> new UserNotFoundException("Not found user has username: " + username));

        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findPasswordResetTokenByUserId(userInfo
                .getUserId());
        String tokenReset = UUID.randomUUID()
                .toString();
        PasswordResetToken passwordResetTokenAdd = new PasswordResetToken();
        passwordResetTokenAdd.setTokenReset(tokenReset);
        LocalDateTime dateResetToken = java.time.LocalDateTime.now();
        passwordResetTokenAdd.setPasswordResetToken_datetime(dateResetToken);
        passwordResetTokenAdd.setUserId(userInfo.getUserId());
        passwordResetToken.ifPresent(resetToken -> passwordResetTokenAdd.setPasswordResetTokenId(resetToken.getPasswordResetTokenId()));
        passwordResetTokenRepository.save(passwordResetTokenAdd);
        passwordResetTokenResponse.setStatus("200");
        passwordResetTokenResponse.setResetToken(tokenReset);
        passwordResetTokenResponse.setMessage("New token: " + passwordResetTokenResponse.getResetToken());

        return passwordResetTokenResponse;
    }

    public PasswordChangeResponse changePassword(AuthChangePassword authChangePassword) {
        PasswordChangeResponse passwordChangeResponse = new PasswordChangeResponse();
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findPasswordResetTokenByTokenReset(
                        authChangePassword.getTokenReset())
                .orElseThrow(() -> new TokenNotFoundException("Token is invalid!"));

        if (!checkTimeTokenReset(passwordResetToken)) {
            passwordChangeResponse.setStatus("406");
            passwordChangeResponse.setMessage("Token has expired");
        } else {
            UserInfo userInfo = userInfoRepository.findByUserId(passwordResetToken
                            .getUserId())
                    .orElseThrow(() -> new UserNotFoundException("Not found user!"));
            userInfo
                    .setUserPassword(passwordEncoder.encode(authChangePassword.getNewPassword()));
            userInfoRepository.save(userInfo);
            passwordChangeResponse.setStatus("200");
            passwordChangeResponse.setMessage("Reset password success!");
        }

        return passwordChangeResponse;
    }


    public boolean checkTimeTokenReset(PasswordResetToken passwordResetToken) {
        LocalDateTime currentDateTime = java.time.LocalDateTime.now();
        System.out.println(currentDateTime);
        long millisToken = passwordResetToken.getPasswordResetToken_datetime()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        long millisCurrent = currentDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        long millis = millisCurrent - millisToken;
        if (millis < 120000) {
            return true;
        } else {
            passwordResetTokenRepository.deleteById(passwordResetToken.getPasswordResetTokenId());
            return false;
        }
    }

    public ChangeInfoUserResponse updateInfoUser(ChangeInfoUserRequest changeInfoUserRequest) {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        ChangeInfoUserResponse changeInfoUserResponse = new ChangeInfoUserResponse();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            Optional<UserInfo> userInfo = Optional.ofNullable(userInfoRepository.findByUserName(currentUserName)
                    .orElseThrow(() -> new UserNotFoundException("Not found User username: " + currentUserName)));
            changeInfoUserRequest.setUserId(userInfo.get()
                    .getUserId());
            UserInfo userInfoUpdate = userChangeInfoMapper.toEntity(changeInfoUserRequest);
            if (changeInfoUserRequest.getUserName() == null) {
                userInfoUpdate.setUserName(userInfo.get()
                        .getUserName());
            }
            if (changeInfoUserRequest.getUserFullName() == null) {
                userInfoUpdate.setUserFullName(userInfo.get()
                        .getUserFullName());
            }
            if (changeInfoUserRequest.getUserAvatar() == null) {
                userInfoUpdate.setUserAvatar(userInfo.get()
                        .getUserAvatar());
            }
            if (changeInfoUserRequest.getUserEmail() == null) {
                userInfoUpdate.setUserEmail(userInfo.get()
                        .getUserEmail());
            }
            if (changeInfoUserRequest.getUserBirthDate() == null) {
                userInfoUpdate.setUserBirthDate(userInfo.get()
                        .getUserBirthDate());
            }
            if (changeInfoUserRequest.getUserAddress() == null) {
                userInfoUpdate.setUserAddress(userInfo.get()
                        .getUserAddress());
            }
            userInfoUpdate.setUserPassword(userInfo.get()
                    .getUserPassword());
            userInfoUpdate.setRoles("ROLE_USER");
            userInfoRepository.save(userInfoUpdate);
            changeInfoUserResponse.setStatus("200");
            changeInfoUserResponse.setMessage("Update success");
            changeInfoUserResponse.setUserInfoUpdate(userInfoUpdate);
        }
        return changeInfoUserResponse;
    }

    public UserInfo findUserById(int id) {
        return userInfoRepository.findByUserId(id)
                .orElseThrow(() -> new UserNotFoundException("Not found user!"));
    }

    //  export file exel bao cao 1 tuan cua user dang nhap
    public void exportReport(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=userActive" + currentDateTime + ".xlsx";
        response.setHeader(headerKey,
                headerValue);

        ReportUserDto reportUserDto = synthesizeReport();
        ExcelGenerator generator = new ExcelGenerator(reportUserDto);
        generator.generateExcelFile(response);
    }

    //   tổng hợp báo cáo 1 tuần của user đăng nhập
    public ReportUserDto synthesizeReport() {
        ReportUserDto reportUserDto = new ReportUserDto();
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            UserInfo userInfo = userInfoRepository.findByUserName(currentUserName)
                    .get();
            reportUserDto.setUserName(currentUserName);
//            lay ra so bai viet user dang nhap da tao trong vong 1 tuan
            LocalDateTime currentDate = LocalDateTime.now();
            int countPostsLastWeek = userPostRepository.countPostLastWeek(userInfo.getUserId(),
                    currentDate);
            reportUserDto.setPostsLastWeek(countPostsLastWeek);
//            lay ra so comment user dang nhap da comment trong vong 1 tuan
            int countCommentsLastWeek = commentRepository.countCommentsLastWeekByMe(userInfo.getUserId(),
                    currentDate);
            reportUserDto.setNewCommentsLastWeek(countCommentsLastWeek);
//            lay ra danh sach ban be user da ket ban trong vong 1 tuan
            int countNewFriendsLastWeek = friendRepository.countNewFriendsLastWeek(userInfo.getUserId(),
                    currentDate);
            reportUserDto.setNewFriendLastWeek(countNewFriendsLastWeek);
//            lay ra so like user dang nhap da like trong vong 1 tuan
            int countLikesLastWeek = likeRepository.countLikesLastWeekByMe(userInfo.getUserId(),
                    currentDate);
            reportUserDto.setNewLikesLastWeek(countLikesLastWeek);
        }
        return reportUserDto;
    }

}