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
            userPostt.setPostDeleteFlag(0);
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
        UpPostResponse upPostResponse = new UpPostResponse();
        UserPost userPost = userPostRepository.findUserPostByPostIdAndAndPostDeleteFlag(upPostRequest.getPostId(),
                0);

        if (userPost != null) {
            upPostRequest.setPostCreateDate(userPost.getPostCreateDate());
            Optional<UserInfo> userInfo = userInfoRepository.findByUserId(userPost.getPostUserId());
            if (userInfo.get().getUserId() == upPostRequest.getPostUserId()) {
                List<String> imagesEdit = upPostRequest.getPostUrlImages();
                List<Image> imageList = imageRepository.findImageByImagePostIdAndImageFlagDelete(upPostRequest.getPostId(),
                        0);
                List<Image> imagesEdited = new ArrayList<>();
                if (imagesEdit != null) {
                    if (!imageList.isEmpty()) {
                        for (Image image : imageList) {
                            System.out.println(image.getImageUrl());
                            image.setImageFlagDelete(1);
                            imageRepository.save(image);
                        }
                    }
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
                } else {
                    imagesEdited = imageRepository.findImageByImagePostIdAndImageFlagDelete(upPostRequest.getPostId(),
                            0);
                }
                UserPost userPost1 = postMapper.toEntity(upPostRequest);
                userPost1.setPostDeleteFlag(0);
                UserPost userPostEdited = userPostRepository.save(userPost1);
                PostDto postDto = postMapper.toDto(userPostEdited);
                List<Comment> commentList = commentRepository.findCommentByCommentPostId(postDto.getPostId());
                postDto.setPostComments(commentList);
                int likePost = likeRepository.countLikeByLikePostIdAndLikeFlag(postDto.getPostId(),
                        1);
                postDto.setLike(likePost);
                postDto.setPostImages(imagesEdited);
                upPostResponse.setPostDto(postDto);
                upPostResponse.setMessage("Edit this post successfully!");
                upPostResponse.setStatus("200");

            } else {
                upPostResponse.setMessage("You cannot edit this post!");
                upPostResponse.setStatus("400");
            }
        } else {
            upPostResponse.setMessage("Not found this post!");
            upPostResponse.setStatus("400");
        }
        return upPostResponse;
    }

    public PostResponse findPostById(int postId) {
        PostResponse postResponse = new PostResponse();
//        check friend
        String currentUserName = friendService.getListFriends().getCurrentUserName();
        List<String> listUserNameCanSeePost = friendService.getListFriends().getUserNameFriends();
        listUserNameCanSeePost.add(currentUserName);
        UserPost userPost = userPostRepository.findUserPostByPostIdAndAndPostDeleteFlag(postId,
                0);
        UserInfo userPostedThePost = userInfoRepository.findByUserId(userPost.getPostUserId()).get();
        if(!listUserNameCanSeePost.contains(userPostedThePost.getUserName())) {
            postResponse.setMessage("You can not see the post because you and "+ userPostedThePost.getUserName() +" are not friend!");
            postResponse.setStatus("400");
            return postResponse;
        }
//        check post ton tai hay ko
        if (userPost != null) {
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
            postDto.setLike(likeRepository.countLikeByLikePostIdAndLikeFlag(userPost.getPostId(),
                    1));
            postResponse.setMessage("This is post!");
            postResponse.setStatus("200");
            postResponse.setPostDto(postDto);

        } else {
            postResponse.setMessage("Not found this post!");
            postResponse.setStatus("400");
        }
        return postResponse;
    }

    public DeletePostResponse deletePost(int idPost) {
        DeletePostResponse deletePostResponse = new DeletePostResponse();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            UserInfo userInfo = userInfoRepository.findByUserName(currentUserName).get();
            UserPost userPost = userPostRepository.findById(idPost).get();
            if (userPost != null) {
                if (userPost.getPostUserId() == userInfo.getUserId()) {
                    userPost.setPostDeleteFlag(1);
                    userPostRepository.save(userPost);
                    deletePostResponse.setMessage("Delete post successful!");
                    deletePostResponse.setStatus("200");
                    deletePostResponse.setIdPostDelete(userPost.getPostId());
                } else {
                    deletePostResponse.setMessage("You can not delete this post!");
                    deletePostResponse.setStatus("400");
                    deletePostResponse.setIdPostDelete(0);
                }
            } else {
                deletePostResponse.setMessage("Not found post!");
                deletePostResponse.setStatus("400");
                deletePostResponse.setIdPostDelete(0);
            }
        } else {
            deletePostResponse.setMessage("Token is invalid!");
            deletePostResponse.setStatus("403");
            deletePostResponse.setIdPostDelete(0);
        }
        return deletePostResponse;
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
            postDto.setLike(likeRepository.countLikeByLikePostIdAndLikeFlag(userPost.getPostId(),
                    1));
            postDtos.add(postDto);
        }
        getAllPostResponse.setPosts(postDtos);
        return getAllPostResponse;
    }

    public LikeResponse likePost(int postId) {
        LikeRequest likeRequest = new LikeRequest();
        LikeResponse likeResponse = new LikeResponse();
        likeRequest.setPostId(postId);
        UserPost userPost = userPostRepository.findUserPostByPostIdAndAndPostDeleteFlag(postId,
                0);
        if (userPost == null) {
            likeResponse.setMessage("Not found this post!");
            likeResponse.setStatus("400");
            return likeResponse;
        }
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
            likeResponse.setStatus("200");
            likeResponse.setLiked(true);
            likeRepository.save(like);
        }
        return likeResponse;
    }

    public GetNewFeedResponse getNewFeed() {
//      hiển thị các bài viết của bản thân và bạn bè ắp xếp theo thời gian đăng gần nhất
        GetNewFeedResponse getNewFeedResponse = new GetNewFeedResponse();
        GetListFriendResponse getListFriendResponse = friendService.getListFriends();
        List<String> userNameFriends = getListFriendResponse.getUserNameFriends();
        List<PostDto> posts = new ArrayList<>();
        for (String userNameFriend : userNameFriends) {
            UserInfo userInfo = userInfoRepository.findByUserName(userNameFriend).get();
            List<PostDto> postsByIdUser = getAllPostsByUserId(userInfo.getUserId());
            posts.addAll(postsByIdUser);
        }
        List<PostDto> myPosts = getAllPostsByUserId(userInfoRepository.findByUserName(getListFriendResponse.getCurrentUserName()).get().getUserId());
        posts.addAll(myPosts);
        Collections.sort(posts,
                Comparator.comparing(PostDto::getPostCreateDate).reversed());
        getNewFeedResponse.setStatus("200");
        getNewFeedResponse.setMessage("Get new feed successful!");
        getNewFeedResponse.setPostDtos(posts);
        System.out.println(posts.size());
        return getNewFeedResponse;
    }

    public List<PostDto> getAllPostsByUserId(int userId) {
        List<UserPost> userPostList = userPostRepository.findUserPostsByPostUserIdAndAndPostDeleteFlag(userId,
                0);
        List<PostDto> postDtos = new ArrayList<>();
        if (!userPostList.isEmpty()) {
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
                postDto.setLike(likeRepository.countLikeByLikePostIdAndLikeFlag(userPost.getPostId(),
                        1));
                postDtos.add(postDto);
            }
        }
        return postDtos;
    }

    //    lay ra danh sach nguoi like bai post
    public UserLikePostResponse getUserLikePost(int postId) {
        UserLikePostResponse userLikePostResponse = new UserLikePostResponse();
        UserPost userPost = userPostRepository.findUserPostByPostIdAndAndPostDeleteFlag(postId,
                0);
        if (userPost != null) {
            List<Like> listLikeByPost = likeRepository.findLikeByLikePostId(postId);
            List<String> listUsernameLikePost = new ArrayList<>();
            for (Like like : listLikeByPost) {
                listUsernameLikePost.add(userInfoRepository.findById(like.getLikeUserId()).get().getUserName());
            }
            userLikePostResponse.setStatus("200");
            userLikePostResponse.setMessage("Get list userName like post successfully!");
            userLikePostResponse.setUserNamesLikePost(listUsernameLikePost);
        } else {
            userLikePostResponse.setStatus("400");
            userLikePostResponse.setMessage("Not found this post!");
            userLikePostResponse.setUserNamesLikePost(null);
        }

        return userLikePostResponse;
    }

}
