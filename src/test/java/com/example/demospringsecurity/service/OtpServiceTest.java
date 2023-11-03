//package com.example.demospringsecurity.service;
//
//import com.example.demospringsecurity.exceptions.UserNotFoundException;
//import com.example.demospringsecurity.model.UserInfo;
//import com.example.demospringsecurity.repository.UserInfoRepository;
//import com.example.demospringsecurity.response.auth.OtpResponse;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(MockitoExtension.class)
//class OtpServiceTest {
//    @InjectMocks
//    OtpService otpService;
//
//    @Mock
//    UserInfoRepository userInfoRepository;
//
//    @Mock
//    JwtService jwtService;
//
//    @Test
//    void sendOtp_notFoundUser() {
//        Mockito.when(userInfoRepository.findByUserName(Mockito.anyString())).thenThrow(UserNotFoundException.class);
//        Assertions.assertThrows(UserNotFoundException.class, () -> otpService.sendOtp("abc"));
//    }
//
//    @Test
//    void sendOtp_success() {
//        UserInfo userInfo = new UserInfo();
//        Mockito.when(userInfoRepository.findByUserName(Mockito.anyString())).thenReturn(Optional.of(userInfo));
//        String otpActual = otpService.sendOtp("anaskjd");
//        Assertions.assertNotNull(otpActual);
//    }
//
//    @Test
//    void verifyOtp_notFoundUser() {
//        Mockito.when(userInfoRepository.findByUserName(Mockito.anyString())).thenThrow(UserNotFoundException.class);
//        Assertions.assertThrows(UserNotFoundException.class, () -> otpService.verifyOtp("abc","090909"));
//    }
//
//    @Test
//    void verifyOtp_otpExpired() {
//        UserInfo userInfo = new UserInfo();
//        Mockito.when(userInfoRepository.findByUserName(Mockito.anyString())).thenReturn(Optional.of(userInfo));
//        OtpResponse otpResponse = otpService.verifyOtp("abc","989898");
//        Assertions.assertEquals("OTP otp has expired!",otpResponse.getMessage());
//    }
//
//    @Test
//    void verifyOtp_otpTimeOut() {
//        UserInfo userInfo = new UserInfo();
//        userInfo.setUserOtp("989898");
//        LocalDateTime localDateTime = LocalDateTime.of(1999,12,12,12,12,12,12);
//        userInfo.setUserTimeCreateOtp(localDateTime);
//        Mockito.when(userInfoRepository.findByUserName(Mockito.anyString())).thenReturn(Optional.of(userInfo));
//        OtpResponse otpResponse = otpService.verifyOtp("abc","989898");
//        Assertions.assertEquals("OTP otp has expired!",otpResponse.getMessage());
//    }
//
//    @Test
//    void verifyOtp_success() {
//        UserInfo userInfo = new UserInfo();
//        userInfo.setUserOtp("989898");
//        LocalDateTime localDateTime = LocalDateTime.now();
//        userInfo.setUserTimeCreateOtp(localDateTime);
//        Mockito.when(userInfoRepository.findByUserName(Mockito.anyString())).thenReturn(Optional.of(userInfo));
//        OtpResponse otpResponse = otpService.verifyOtp("abc","989898");
//        Assertions.assertEquals("OTP confirmed!",otpResponse.getMessage());
//    }
//
//    @Test
//    void verifyOtp_fail() {
//        UserInfo userInfo = new UserInfo();
//        userInfo.setUserOtp("101010");
//        LocalDateTime localDateTime = LocalDateTime.now();
//        userInfo.setUserTimeCreateOtp(localDateTime);
//        Mockito.when(userInfoRepository.findByUserName(Mockito.anyString())).thenReturn(Optional.of(userInfo));
//        OtpResponse otpResponse = otpService.verifyOtp("abc","989898");
//        Assertions.assertEquals("OTP does not exist!",otpResponse.getMessage());
//    }
//
//}