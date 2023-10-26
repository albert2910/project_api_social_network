package com.example.demospringsecurity.controller;

import com.example.demospringsecurity.dto.request.*;
import com.example.demospringsecurity.model.UserInfo;
import com.example.demospringsecurity.response.*;
import com.example.demospringsecurity.service.FileService;
import com.example.demospringsecurity.service.JwtService;
import com.example.demospringsecurity.service.OtpService;
import com.example.demospringsecurity.service.UserService;
import com.example.demospringsecurity.util.FileDownloadUtil;
import com.example.demospringsecurity.validator.ValidFile;
import com.example.demospringsecurity.validator.ValidSizeFile;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    FileService fileService;


    @PostMapping(value = "/info/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ChangeInfoUserResponse> changeInfo(@RequestPart(value = "file", required = false) @Valid @ValidFile @ValidSizeFile MultipartFile multipartFile , ChangeInfoUserRequest changeInfoUserRequest ) throws IOException {
        if(multipartFile != null) {
            FileUploadResponse fileUploadResponse = fileService.uploadFile(multipartFile);
            changeInfoUserRequest.setUserAvatar(fileUploadResponse.getFileName());
        } else {
            changeInfoUserRequest.setUserAvatar(null);
        }
        return new ResponseEntity<>(userService.updateInfoUser(changeInfoUserRequest),
                HttpStatus.OK);
    }

    @GetMapping("/avatar/{idUser}")
    public ResponseEntity<?> getAvatarUser(@PathVariable(name = "idUser") int id) {
        UserInfo userInfo = userService.findUserById(id);
        FileDownloadUtil downloadUtil = new FileDownloadUtil();
        Resource resource;

        try {
            resource = downloadUtil.getFileAsResource(userInfo.getUserAvatar());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

        if (resource == null) {
            return new ResponseEntity<>("File not found",
                    HttpStatus.NOT_FOUND);
        }
//        String contentType = "application/octet-stream";
//        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
//                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }


}
