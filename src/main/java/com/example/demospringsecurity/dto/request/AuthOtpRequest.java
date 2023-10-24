package com.example.demospringsecurity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthOtpRequest {
    @NotBlank(message = "Invalid userName: Empty userName")
    @NotNull(message = "Invalid userName: userName is NULL")
    @Size(min = 3, max = 30, message = "Invalid userName: Exceeds 30 characters")
    private String userName;

    @NotBlank(message = "Invalid otp: Empty otp")
    @NotNull(message = "Invalid otp: Otp is NULL")
    @Size(min = 6, max = 6, message = "Invalid otp: Must = 6 characters")
    @Pattern(regexp = "^\\d+$", message = "Invalid otp: Otp is a 6-digit number sequence")
    private String otp;
}
