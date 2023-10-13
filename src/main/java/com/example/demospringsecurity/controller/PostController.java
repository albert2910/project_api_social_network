package com.example.demospringsecurity.controller;

import com.example.demospringsecurity.dto.request.UpPostRequest;
import com.example.demospringsecurity.response.FileUploadResponse;
import com.example.demospringsecurity.response.GetAllPostResponse;
import com.example.demospringsecurity.response.UpPostResponse;
import com.example.demospringsecurity.service.FileService;
import com.example.demospringsecurity.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
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


    @PostMapping(value = "/up-post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UpPostResponse upPost(@RequestPart(value = "files", required = false) MultipartFile[] multipartFiles, UpPostRequest upPostRequest) throws IOException {
        List<String> listImages = new ArrayList<>();
        if (multipartFiles != null) {
            for (MultipartFile multipartFile : multipartFiles) {
                FileUploadResponse fileUploadResponse = fileService.uploadFile(multipartFile);
                listImages.add(fileUploadResponse.getFileName());
            }
        }
        upPostRequest.setPostUrlImages(listImages);
        return postService.upPost(upPostRequest);
    }

    @PutMapping(value = "/edit-post/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UpPostResponse editPost(@RequestPart(value = "files", required = false) MultipartFile[] multipartFiles, @PathVariable int postId, UpPostRequest upPostRequest) throws IOException {
        upPostRequest.setPostId(postId);
        List<String> listImages = new ArrayList<>();
        if (multipartFiles != null) {
            for (MultipartFile multipartFile : multipartFiles) {
                FileUploadResponse fileUploadResponse = fileService.uploadFile(multipartFile);
                listImages.add(fileUploadResponse.getFileName());
            }
        }
        upPostRequest.setPostUrlImages(listImages);
        return postService.upPost(upPostRequest);
    }

    @GetMapping("/all-posts")
    public GetAllPostResponse getAllPosts() {
        return postService.getAllPosts();
    }
}
