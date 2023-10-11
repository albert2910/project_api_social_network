package com.example.demospringsecurity.service;

import com.example.demospringsecurity.dto.PostDto;
import com.example.demospringsecurity.dto.request.UpPostRequest;
import com.example.demospringsecurity.mapperImpl.PostMapper;
import com.example.demospringsecurity.model.Image;
import com.example.demospringsecurity.model.UserPost;
import com.example.demospringsecurity.repository.ImageRepository;
import com.example.demospringsecurity.repository.UserPostRepository;
import com.example.demospringsecurity.response.UpPostResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {
    @Autowired
    PostMapper postMapper;

    @Autowired
    UserPostRepository userPostRepository;

    @Autowired
    ImageRepository imageRepository;

    public UpPostResponse upPost(UpPostRequest upPostRequest) {
        UserPost userPost = postMapper.toEntity(upPostRequest);
        UserPost userPostSaved = userPostRepository.save(userPost);
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
        UpPostResponse upPostResponse = new UpPostResponse();
        upPostResponse.setPostDto(postDto);
        upPostResponse.setStatus("200");
        upPostResponse.setMessage("Up post success!");
        return upPostResponse;

    }
}
