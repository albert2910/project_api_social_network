package com.example.demospringsecurity.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(type = "object", example = "{\n" +
        "  \"email\": \"string\",\n"+
        "  \"newPassword\": \"string\",\n" +
        "  \"tokenReset\": \"string\"\n" +
        "}")
public class AuthChangePassword {
    @NotBlank(message = "Invalid userEmail: Empty userEmail")
    @NotNull(message = "Invalid userEmail: userEmail is NULL")
    @Email(message = "Incorrect email format!")
    private String email;

    @NotBlank(message = "Invalid newPassword: Empty newPassword")
    @NotNull(message = "Invalid newPassword: newPassword is NULL")
    // Minimum eight and maximum 10 characters, at least one uppercase letter, one lowercase letter, one number and one special character
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,100}$", message = "Minimum eight and maximum 10 characters, at least one uppercase letter, one lowercase letter, one number and one special character")
    private String newPassword;

    @NotBlank(message = "Invalid tokenReset: Empty tokenReset")
    @NotNull(message = "Invalid tokenReset: tokenReset is NULL")
    private String tokenReset;
}
