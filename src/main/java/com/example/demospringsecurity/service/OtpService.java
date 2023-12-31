package com.example.demospringsecurity.service;

import com.example.demospringsecurity.exceptions.BadRequestException;
import com.example.demospringsecurity.exceptions.ExpiredException;
import com.example.demospringsecurity.exceptions.UserNotFoundException;
import com.example.demospringsecurity.model.UserInfo;
import com.example.demospringsecurity.repository.UserInfoRepository;
import com.example.demospringsecurity.response.auth.OtpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;

@Service
@Transactional
public class OtpService {

    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    JwtService jwtService;

    public String sendOtp(String username) {
        UserInfo userInfo = userInfoRepository.findByUserName(username)
                .orElseThrow(() -> new UserNotFoundException("Not found user userName: " + username));
        userInfo.setUserOtp(generateOTP());
        LocalDateTime dateCreateOtp = java.time.LocalDateTime.now();
        userInfo.setUserTimeCreateOtp(dateCreateOtp);
        System.out.println(userInfo.getUserTimeCreateOtp());
        userInfoRepository.save(userInfo);
        return userInfo.getUserOtp();
    }

    public String generateOTP() {
        // Using numeric values
        String numbers = "0123456789";

        // Using random method
        Random rdm_method = new Random();

        // to store the OTP
        char[] OTP = new char[6];
        String otp = "";
        for (int i = 0; i < 6; i++) {
            // Use of charAt() method to get character value
            // Use of nextInt() as it is scanning the value as int
            OTP[i] = numbers.charAt(rdm_method.nextInt(numbers.length()));
            otp += OTP[i];
        }
        return otp;
    }


    public OtpResponse verifyOtp(String username, String otp) {
        UserInfo userInfo = userInfoRepository.findByUserName(username)
                .orElseThrow(() -> new UserNotFoundException("Not found user username: " + username));
        OtpResponse otpResponse = new OtpResponse();
        if (userInfo.getUserOtp() == null) {
            throw new ExpiredException("OTP otp has expired!");
        } else if (checkTimeOtp(userInfo)) {
            if (otp.equals(userInfo.getUserOtp())) {
                userInfo.setUserOtp(null);
                userInfo.setUserTimeCreateOtp(null);
                userInfoRepository.save(userInfo);
                otpResponse.setMessage("OTP confirmed!");
                otpResponse.setStatus("200");
                otpResponse.setToken(jwtService.generateToken(username));
            } else throw new BadRequestException("OTP does not exist!");
        } else {
            throw new ExpiredException("OTP otp has expired!");
        }
        return otpResponse;
    }

    public boolean checkTimeOtp(UserInfo userInfo) {
        LocalDateTime currentDateTime = java.time.LocalDateTime.now();
        long millisOtp = userInfo.getUserTimeCreateOtp()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        long millisCurrent = currentDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        long millis = millisCurrent - millisOtp;
        if (millis < 60000) {
            return true;
        } else {
            userInfo.setUserOtp(null);
            userInfo.setUserTimeCreateOtp(null);
            userInfoRepository.save(userInfo);
            return false;
        }
    }

}



