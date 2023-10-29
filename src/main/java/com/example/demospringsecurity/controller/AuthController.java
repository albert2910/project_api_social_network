package com.example.demospringsecurity.controller;

import com.example.demospringsecurity.dto.request.AuthChangePassword;
import com.example.demospringsecurity.dto.request.AuthOtpRequest;
import com.example.demospringsecurity.dto.request.AuthRequest;
import com.example.demospringsecurity.dto.request.RegisterRequest;
import com.example.demospringsecurity.response.auth.*;
import com.example.demospringsecurity.service.OtpService;
import com.example.demospringsecurity.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
public class AuthController {
    @Autowired
    UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    OtpService otpService;
    @PostMapping("/signup")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody @Valid RegisterRequest registerRequest) {
        return new ResponseEntity<>(userService.registerUser(registerRequest), HttpStatus.OK);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<LoginResponse> authenticateAndGetOtp(@RequestBody @Valid AuthRequest authRequest) {
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
            return new ResponseEntity<>(loginResponse,
                    HttpStatus.OK);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            throw new BadCredentialsException("adu");
        }

    }

    @PostMapping("/verify-otp")
    public ResponseEntity<OtpResponse> authenticateAndGetToken(@RequestBody @Valid AuthOtpRequest authOtpRequest) {
        return new ResponseEntity<>(otpService.verifyOtp(authOtpRequest.getUserName(),
                authOtpRequest.getOtp()), HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public PasswordResetTokenResponse forgotPassword(@RequestParam String username) {
        return userService.forgotPassword(username);
    }

    @PatchMapping("/change-password")
    public PasswordChangeResponse changePassword(@RequestBody @Valid AuthChangePassword authChangePassword) {
        return userService.changePassword(authChangePassword);
    }
}
