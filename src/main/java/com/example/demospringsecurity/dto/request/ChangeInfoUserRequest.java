package com.example.demospringsecurity.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(type = "object", example = "{\n" +
        "  \"username\": \"string\",\n" +
        "  \"userFullName\": \"string\",\n" +
        "  \"userAvatar\": \"string\",\n" +
        "  \"userEmail\": \"string\",\n" +
        "  \"userAddress\": \"string\"\n" +
        "}")
public class ChangeInfoUserRequest {
    private int userId;
    private String userName;
    private String userFullName;
    private String userAvatar;
    private String userEmail;
    private Date userBirthDate;
    private String userAddress;

}
