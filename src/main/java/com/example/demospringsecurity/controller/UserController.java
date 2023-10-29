package com.example.demospringsecurity.controller;

import com.example.demospringsecurity.dto.request.*;
import com.example.demospringsecurity.model.UserInfo;
import com.example.demospringsecurity.response.uploadfile.FileUploadResponse;
import com.example.demospringsecurity.response.user.ChangeInfoUserResponse;
import com.example.demospringsecurity.service.FileService;
import com.example.demospringsecurity.service.UserService;
import com.example.demospringsecurity.util.FileDownloadUtil;
import com.example.demospringsecurity.validator.ValidFile;
import com.example.demospringsecurity.validator.ValidSizeFile;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

//  chinh sua thong tin ca nhan
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

//  xem ava cua cac user khac
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
