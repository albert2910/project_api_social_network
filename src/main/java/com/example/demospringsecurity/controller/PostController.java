package com.example.demospringsecurity.controller;

import com.example.demospringsecurity.dto.request.LikeRequest;
import com.example.demospringsecurity.dto.request.UpPostRequest;
import com.example.demospringsecurity.model.Image;
import com.example.demospringsecurity.response.*;
import com.example.demospringsecurity.service.FileService;
import com.example.demospringsecurity.service.PostService;
import com.example.demospringsecurity.validator.ValidMultipleFile;
import com.example.demospringsecurity.validator.ValidSizeMultipleFile;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/posts")
@Validated
public class PostController {
    @Autowired
    FileService fileService;

    @Autowired
    PostService postService;


    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UpPostResponse upPost(@RequestPart(value = "files", required = false) @Valid @ValidMultipleFile @ValidSizeMultipleFile MultipartFile[] multipartFiles, UpPostRequest upPostRequest) throws IOException {
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

    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UpPostResponse editPost(@RequestPart(value = "files", required = false) @Valid @ValidMultipleFile @ValidSizeMultipleFile MultipartFile[] multipartFiles, @PathVariable int postId, UpPostRequest upPostRequest) throws IOException {
        upPostRequest.setPostId(postId);
        if (multipartFiles != null) {
            List<String> listImages = new ArrayList<>();
            for (MultipartFile multipartFile : multipartFiles) {
                FileUploadResponse fileUploadResponse = fileService.uploadFile(multipartFile);
                listImages.add(fileUploadResponse.getFileName());
            }
            upPostRequest.setPostUrlImages(listImages);
        } else {
            upPostRequest.setPostUrlImages(null);
        }
        return postService.upPost(upPostRequest);
    }

    @DeleteMapping("/{postId}")
    public DeletePostResponse deletePost(@PathVariable int postId) {
        return postService.deletePost(postId);
    }

//    @GetMapping("/")
//    public GetAllPostResponse getAllPosts() {
//        return postService.getAllPosts();
//    }

    @PostMapping("/{postId}/react")
    public ResponseEntity<?> likePost(@PathVariable int postId) {
        return new ResponseEntity<>(postService.likePost(postId),
                HttpStatus.OK);
    }

    @GetMapping("/{postId}/users-liked")
    public UserLikePostResponse getListUserNameLikePost(@PathVariable int postId) {
        return postService.getUserLikePost(postId);
    }

    @GetMapping("/{postId}")
    public PostResponse getPostById(@PathVariable int postId) {
        return postService.findPostById(postId);
    }


}
