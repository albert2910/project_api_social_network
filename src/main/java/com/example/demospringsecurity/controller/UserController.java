package com.example.demospringsecurity.controller;

import com.example.demospringsecurity.dto.request.*;
import com.example.demospringsecurity.model.UserInfo;
import com.example.demospringsecurity.response.*;
import com.example.demospringsecurity.service.FileService;
import com.example.demospringsecurity.service.JwtService;
import com.example.demospringsecurity.service.OtpService;
import com.example.demospringsecurity.service.UserService;
import com.example.demospringsecurity.util.FileDownloadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    JwtService jwtService;

    @Autowired
    OtpService otpService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserService userService;

    @Autowired
    FileService fileService;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to Demo Security Springboot";
    }

    @PostMapping("/login")
    public LoginResponse authenticateAndGetOtp(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUserName(),
                    authRequest.getPassword()));
            LoginResponse loginResponse = new LoginResponse();
            if (authentication.isAuthenticated()) {
                loginResponse.setStatus("200");
                loginResponse.setUserName(authRequest.getUserName());
                loginResponse.setOtp(otpService.sendOtp(authRequest.getUserName()));
                loginResponse.setMessage("OTP: " + loginResponse.getOtp());
            }
            return loginResponse;
        } catch (AuthenticationException e) {
            e.printStackTrace();
            throw new BadCredentialsException("adu");
        }

    }

    @PostMapping("/verify")
    public OtpResponse authenticateAndGetToken(@RequestBody AuthOtpRequest authOtpRequest) {
        return otpService.verifyOtp(authOtpRequest.getUserName(),
                authOtpRequest.getOtp());
    }

    @PostMapping("/register")
    public RegisterResponse registerUser(@RequestBody RegisterRequest registerRequest) {
        return userService.registerUser(registerRequest);
    }

    @PostMapping("/forgot-password")
    public PasswordResetTokenResponse forgotPassword(@RequestParam String username) {
        return userService.forgotPassword(username);
    }

    @PostMapping("/change-password")
    public PasswordChangeResponse changePassword(@RequestBody AuthChangePassword authChangePassword) {
        return userService.changePassword(authChangePassword);
    }

    @PostMapping(value = "/change-info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ChangeInfoUserResponse> changeInfo(@RequestPart("file") MultipartFile multipartFile, ChangeInfoUserRequest changeInfoUserRequest) throws IOException {
        FileUploadResponse fileUploadResponse = fileService.uploadFile(multipartFile);
        changeInfoUserRequest.setUserAvatar(fileUploadResponse.getFileName());
        return new ResponseEntity<>(userService.updateInfoUser(changeInfoUserRequest),
                HttpStatus.OK);
    }

    @GetMapping("/get-avatar-user/{id}")
    public ResponseEntity<?> getAvatarUser(@PathVariable int id) {
        UserInfo userInfo = userService.findUserById(id);
        FileDownloadUtil downloadUtil = new FileDownloadUtil();

        Resource resource;

        try {
            resource = downloadUtil.getFileAsResource(userInfo.getUserAvatar());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

        if (resource == null) {
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        }
//        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
//                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }

}
