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
public class RegisterRequest {
    @NotBlank(message = "Invalid userEmail: Empty userEmail")
    @NotNull(message = "Invalid userEmail: userEmail is NULL")
    @Pattern(regexp = "^([a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6})*$", message = "Incorrect email format!")
    private String userEmail;

    @NotBlank(message = "Invalid userName: Empty userName")
    @NotNull(message = "Invalid userName: userName is NULL")
    @Size(min = 3, max = 30, message = "Invalid userName: Exceeds 30 characters")
    private String userName;

    @NotBlank(message = "Invalid password: Empty password")
    @NotNull(message = "Invalid password: Password is NULL")
    // Minimum eight and maximum 10 characters, at least one uppercase letter, one lowercase letter, one number and one special character
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,10}$", message = "Minimum eight and maximum 10 characters, at least one uppercase letter, one lowercase letter, one number and one special character")
    private String userPassword;
}
