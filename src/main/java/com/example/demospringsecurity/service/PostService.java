package com.example.demospringsecurity.service;

import com.example.demospringsecurity.dto.PostDto;
import com.example.demospringsecurity.dto.request.UpPostRequest;
import com.example.demospringsecurity.mapperImpl.PostMapper;
import com.example.demospringsecurity.model.Image;
import com.example.demospringsecurity.model.UserInfo;
import com.example.demospringsecurity.model.UserPost;
import com.example.demospringsecurity.repository.ImageRepository;
import com.example.demospringsecurity.repository.UserInfoRepository;
import com.example.demospringsecurity.repository.UserPostRepository;
import com.example.demospringsecurity.response.UpPostResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    PostMapper postMapper;

    @Autowired
    UserPostRepository userPostRepository;

    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    ImageRepository imageRepository;

    public UpPostResponse upPost(UpPostRequest upPostRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UpPostResponse upPostResponse = new UpPostResponse();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            UserInfo userInfo = userInfoRepository.findByUserName(currentUserName).get();
            upPostRequest.setPostUserId(userInfo.getUserId());
        }
        if (upPostRequest.getPostId() != 0) {
            upPostResponse = editPost(upPostRequest);
        } else {
            LocalDateTime dateCreate = LocalDateTime.now();
            upPostRequest.setPostCreateDate(dateCreate);
            UserPost userPostt = postMapper.toEntity(upPostRequest);
            UserPost userPostSaved = userPostRepository.save(userPostt);
            List<String> listImages = upPostRequest.getPostUrlImages();
            List<Image> imageList = new ArrayList<>();
            for (String listImage : listImages) {
                Image image = new Image();
                image.setImagePostId(userPostSaved.getPostId());
                image.setImageUrl(listImage);
                Image imageSaved = imageRepository.save(image);
                imageList.add(imageSaved);
            }
            PostDto postDto = postMapper.toDto(userPostSaved);
            postDto.setPostImages(imageList);

            upPostResponse.setPostDto(postDto);
            upPostResponse.setStatus("200");
            upPostResponse.setMessage("Up post success!");
        }
        return upPostResponse;
    }


    public UpPostResponse editPost(UpPostRequest upPostRequest) {
        Optional<UserPost> userPost = userPostRepository.findById(upPostRequest.getPostId());
        upPostRequest.setPostCreateDate(userPost.get().getPostCreateDate());
        UpPostResponse upPostResponse = new UpPostResponse();
        if (userPost.isPresent()) {
            Optional<UserInfo> userInfo = userInfoRepository.findByUserId(userPost.get().getPostUserId());
            if (userInfo.get().getUserId() == upPostRequest.getPostUserId()) {
                List<Image> imageList = imageRepository.findImageByImagePostId(upPostRequest.getPostId());
                if (!imageList.isEmpty()) {
                    for (Image image : imageList) {
                        imageRepository.delete(image);
                    }
                }
                List<String> imagesEdit = upPostRequest.getPostUrlImages();
                List<Image> imagesEdited = new ArrayList<>();
                if (!imagesEdit.isEmpty()) {
                    for (String s : imagesEdit) {
                        Image image = new Image();
                        image.setImageUrl(s);
                        image.setImagePostId(upPostRequest.getPostId());
                        Image imageSaved = imageRepository.save(image);
                        imagesEdited.add(imageSaved);
                    }
                }
                UserPost userPost1 = postMapper.toEntity(upPostRequest);
                UserPost userPostEdited = userPostRepository.save(userPost1);
                PostDto postDto = postMapper.toDto(userPostEdited);
                postDto.setPostImages(imagesEdited);
                upPostResponse.setPostDto(postDto);
                upPostResponse.setMessage("Edit this post successfully!");
                upPostResponse.setStatus("200");

            } else {
                upPostResponse.setMessage("You cannot edit this post!");
                upPostResponse.setStatus("400");
            }

        }
        return upPostResponse;
    }
}
