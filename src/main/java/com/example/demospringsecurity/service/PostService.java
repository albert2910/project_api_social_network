package com.example.demospringsecurity.service;

import com.example.demospringsecurity.dto.PostDto;
import com.example.demospringsecurity.dto.request.LikeRequest;
import com.example.demospringsecurity.dto.request.UpPostRequest;
import com.example.demospringsecurity.mapperImpl.PostMapper;
import com.example.demospringsecurity.model.*;
import com.example.demospringsecurity.repository.*;
import com.example.demospringsecurity.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class PostService {
    @Autowired
    PostMapper postMapper;

    @Autowired
    UserPostRepository userPostRepository;

    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    FriendService friendService;


    public UpPostResponse upPost(UpPostRequest upPostRequest) {
        UpPostResponse upPostResponse = new UpPostResponse();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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
                image.setImageFlagDelete(0);
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
                List<Image> imageList = imageRepository.findImageByImagePostIdAndImageFlagDelete(upPostRequest.getPostId(),
                        0);
                if (!imageList.isEmpty()) {
                    for (Image image : imageList) {
                        System.out.println(image.getImageUrl());
                        image.setImageFlagDelete(1);
                        imageRepository.save(image);
                    }
                }
                List<String> imagesEdit = upPostRequest.getPostUrlImages();
                List<Image> imagesEdited = new ArrayList<>();
                if (!imagesEdit.isEmpty()) {
                    for (String s : imagesEdit) {
                        Image image = new Image();
                        image.setImageUrl(s);
                        image.setImagePostId(upPostRequest.getPostId());
                        image.setImageFlagDelete(0);
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

    public GetAllPostResponse getAllPosts() {
        GetAllPostResponse getAllPostResponse = new GetAllPostResponse();
        getAllPostResponse.setStatus("200");
        getAllPostResponse.setMessage("Get all!");
        List<UserPost> userPostList = userPostRepository.findAll();
        List<PostDto> postDtos = new ArrayList<>();
        for (UserPost userPost : userPostList) {
            PostDto postDto = postMapper.toDto(userPost);
            List<Image> imageList = imageRepository.findImageByImagePostIdAndImageFlagDelete(userPost.getPostId(),
                    0);
            List<Comment> commentList = commentRepository.findCommentByCommentPostId(userPost.getPostId());
            if (!imageList.isEmpty()) {
                postDto.setPostImages(imageList);
            }
            if (!commentList.isEmpty()) {
                postDto.setPostComments(commentList);
            }
            postDto.setLike(likeRepository.countLikeByLikePostIdAndLikeFlag(userPost.getPostId(), 1));
            postDtos.add(postDto);
        }
        getAllPostResponse.setPosts(postDtos);
        return getAllPostResponse;
    }

    public LikeResponse likePost(LikeRequest likeRequest) {
        LikeResponse likeResponse = new LikeResponse();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            UserInfo userInfo = userInfoRepository.findByUserName(currentUserName).get();
            likeRequest.setUserId(userInfo.getUserId());
        }
        boolean checkLikePostIdUserId = likeRepository.existsByLikePostIdAndLikeUserId(likeRequest.getPostId(),
                likeRequest.getUserId());
        if (checkLikePostIdUserId) {
            Like like = likeRepository.findLikeByLikePostIdAndLikeUserId(likeRequest.getPostId(),
                    likeRequest.getUserId());
            if (like.getLikeFlag() == 0) {
                like.setLikeFlag(1);
                likeRepository.save(like);
                likeResponse.setMessage("Liked!");
                likeResponse.setLiked(true);

            } else {
                like.setLikeFlag(0);
                likeResponse.setMessage("Disliked");
                likeResponse.setLiked(false);
                likeRepository.save(like);
            }
            likeResponse.setStatus("200");

        } else {
            Like like = new Like();
            like.setLikePostId(likeRequest.getPostId());
            like.setLikeUserId(likeRequest.getUserId());
            like.setLikeFlag(1);
            likeResponse.setMessage("Liked!");
            likeResponse.setLiked(true);
            likeRepository.save(like);
        }
        return likeResponse;
    }

    public GetNewFeedResponse getNewFeed() {
        GetNewFeedResponse getNewFeedResponse = new GetNewFeedResponse();
        GetListFriendResponse getListFriendResponse = friendService.getListFriends();
        List<String> userNameFriends = getListFriendResponse.getUserNameFriends();
        List<PostDto> posts = new ArrayList<>();
        for (String userNameFriend : userNameFriends) {
            UserInfo userInfo = userInfoRepository.findByUserName(userNameFriend).get();
            List<PostDto> postsByIdUser = getAllPostsByUserId(userInfo.getUserId());
            posts.addAll(postsByIdUser);
        }
        Collections.sort(posts, Comparator.comparing(PostDto::getPostCreateDate).reversed());
        getNewFeedResponse.setStatus("200");
        getNewFeedResponse.setMessage("Get new feed successful!");
        getNewFeedResponse.setPostDtos(posts);
        System.out.println(posts.size());
        return getNewFeedResponse;
    }

    public List<PostDto> getAllPostsByUserId(int userId) {
        List<UserPost> userPostList = userPostRepository.findUserPostByPostUserId(userId);
        List<PostDto> postDtos = new ArrayList<>();
        for (UserPost userPost : userPostList) {
            PostDto postDto = postMapper.toDto(userPost);
            List<Image> imageList = imageRepository.findImageByImagePostIdAndImageFlagDelete(userPost.getPostId(),
                    0);
            List<Comment> commentList = commentRepository.findCommentByCommentPostId(userPost.getPostId());
            if (!imageList.isEmpty()) {
                postDto.setPostImages(imageList);
            }
            if (!commentList.isEmpty()) {
                postDto.setPostComments(commentList);
            }
            postDto.setLike(likeRepository.countLikeByLikePostIdAndLikeFlag(userPost.getPostId(), 1));
            postDtos.add(postDto);
        }

        return postDtos;
    }

}
