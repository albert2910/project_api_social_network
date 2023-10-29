package com.example.demospringsecurity.controller;

import com.example.demospringsecurity.dto.request.UpPostRequest;
import com.example.demospringsecurity.response.post.*;
import com.example.demospringsecurity.response.uploadfile.FileUploadResponse;
import com.example.demospringsecurity.service.FileService;
import com.example.demospringsecurity.service.PostService;
import com.example.demospringsecurity.validator.ValidMultipleFile;
import com.example.demospringsecurity.validator.ValidSizeMultipleFile;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@Validated
public class PostController {
    @Autowired
    FileService fileService;

    @Autowired
    PostService postService;

//  up bai post
    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UpPostResponse> upPost(@RequestPart(value = "files", required = false) @Valid @ValidMultipleFile @ValidSizeMultipleFile MultipartFile[] multipartFiles, UpPostRequest upPostRequest) throws IOException {
        List<String> listImages = new ArrayList<>();
        if (multipartFiles != null) {
            for (MultipartFile multipartFile : multipartFiles) {
                FileUploadResponse fileUploadResponse = fileService.uploadFile(multipartFile);
                listImages.add(fileUploadResponse.getFileName());
            }
        }
        upPostRequest.setPostUrlImages(listImages);
        return new ResponseEntity<>(postService.upPost(upPostRequest), HttpStatus.OK);
    }

//  chinh sua bai post
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

//    xoa bai post
    @DeleteMapping("/{postId}")
    public DeletePostResponse deletePost(@PathVariable int postId) {
        return postService.deletePost(postId);
    }

//    @GetMapping("/")
//    public GetAllPostResponse getAllPosts() {
//        return postService.getAllPosts();
//    }

//    like va unlike bai post
    @PostMapping("/{postId}/react")
    public ResponseEntity<?> likePost(@PathVariable int postId) {
        return new ResponseEntity<>(postService.likePost(postId),
                HttpStatus.OK);
    }

//  lay ra danh sach nguoi like bai post
    @GetMapping("/{postId}/users-liked")
    public UserLikePostResponse getListUserNameLikePost(@PathVariable int postId) {
        return postService.getUserLikePost(postId);
    }

//    tim kiem bai post theo id
    @GetMapping("/{postId}")
    public PostResponse getPostById(@PathVariable int postId) {
        return postService.findPostById(postId);
    }

//    xem cac bai viet da dang cua ban than
    @GetMapping("/me")
    public ResponseEntity<GetMyPostsResponse> getMyPosts() {
        return new ResponseEntity<>(postService.getMyPosts(),HttpStatus.OK);
    }


}
