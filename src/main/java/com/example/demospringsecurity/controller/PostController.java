package com.example.demospringsecurity.controller;

import com.example.demospringsecurity.dto.request.LikeRequest;
import com.example.demospringsecurity.dto.request.UpPostRequest;
import com.example.demospringsecurity.model.Image;
import com.example.demospringsecurity.response.*;
import com.example.demospringsecurity.service.FileService;
import com.example.demospringsecurity.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @DeleteMapping("/delete-post/{postId}")
    public DeletePostResponse deletePost(@PathVariable int postId) {
        return postService.deletePost(postId);
    }

    @GetMapping("/all-posts")
    public GetAllPostResponse getAllPosts() {
        return postService.getAllPosts();
    }

    @PostMapping("/like-post/{postId}")
    public ResponseEntity<?> likePost(@PathVariable int postId) {
        return new ResponseEntity<>(postService.likePost(postId),
                HttpStatus.OK);
    }

    @GetMapping("/post/users-liked/{postId}")
    public UserLikePostResponse getListUserNameLikePost(@PathVariable int postId) {
        return postService.getUserLikePost(postId);
    }

    @GetMapping("/post/{postId}")
    public PostResponse getPostById(@PathVariable int postId) {
        return postService.findPostById(postId);
    }

    @GetMapping("/new-feed")
    public GetNewFeedResponse getNewFeed() {
        return postService.getNewFeed();
    }



}
