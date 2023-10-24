package com.example.demospringsecurity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    @NotBlank(message = "Invalid userName: Empty userName")
    @NotNull(message = "Invalid userName: userName is NULL")
    @Size(min = 3, max = 30, message = "Invalid userName: Exceeds 30 characters")
    private String userName;

    @NotBlank(message = "Invalid password: Empty password")
    @NotNull(message = "Invalid password: Password is NULL")
    @Size(min = 8, max = 10, message = "Invalid password: Minimum eight and maximum 10 characters")
    private String password;
}
