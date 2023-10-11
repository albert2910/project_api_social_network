package com.example.demospringsecurity.controller;

import com.example.demospringsecurity.dto.request.UpPostRequest;
import com.example.demospringsecurity.response.FileUploadResponse;
import com.example.demospringsecurity.response.UpPostResponse;
import com.example.demospringsecurity.service.FileService;
import com.example.demospringsecurity.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PostController {
    @Autowired
    FileService fileService;

    @Autowired
    PostService postService;


    @PostMapping("/up-post")
    public UpPostResponse upPost(@RequestPart("files")MultipartFile[] multipartFiles, UpPostRequest upPostRequest) throws IOException {
        List<String> listImages = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            FileUploadResponse fileUploadResponse = fileService.uploadFile(multipartFile);
            listImages.add(fileUploadResponse.getFileName());
        }
        upPostRequest.setPostUrlImages(listImages);
        return postService.upPost(upPostRequest);
    }
}
