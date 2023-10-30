package com.example.demospringsecurity.service;

import com.example.demospringsecurity.dto.PostDto;
import com.example.demospringsecurity.dto.PostViewDto;
import com.example.demospringsecurity.dto.request.LikeRequest;
import com.example.demospringsecurity.dto.request.UpPostRequest;
import com.example.demospringsecurity.exceptions.PostNotFoundException;
import com.example.demospringsecurity.exceptions.UserNotFoundException;
import com.example.demospringsecurity.mapper.PostMapper;
import com.example.demospringsecurity.mapper.PostViewMapper;
import com.example.demospringsecurity.model.*;
import com.example.demospringsecurity.repository.*;
import com.example.demospringsecurity.response.friend.GetListFriendResponse;
import com.example.demospringsecurity.response.like.LikeResponse;
import com.example.demospringsecurity.response.post.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    PostViewMapper postViewMapper;

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
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            UserInfo userInfo = userInfoRepository.findByUserName(currentUserName)
                    .orElseThrow(() -> new UserNotFoundException("Not found user"));
            upPostRequest.setPostUserId(userInfo.getUserId());
        } else return upPostResponse;
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
            PostViewDto postViewDto = postViewMapper.toDto(userPostSaved);
            postViewDto.setPostImages(imageList);

            upPostResponse.setPostViewDto(postViewDto);
            upPostResponse.setStatus("200");
            upPostResponse.setMessage("Up post success!");
        }
        return upPostResponse;
    }


    public UpPostResponse editPost(UpPostRequest upPostRequest) {
        UpPostResponse upPostResponse = new UpPostResponse();
        UserPost userPost = userPostRepository.findUserPostByPostIdAndAndPostDeleteFlag(upPostRequest.getPostId(),
                        0)
                .orElseThrow(() -> new PostNotFoundException("Post not exist!"));

        upPostRequest.setPostCreateDate(userPost.getPostCreateDate());
        UserInfo userInfo = userInfoRepository.findByUserId(userPost.getPostUserId())
                .orElseThrow(() -> new UserNotFoundException("User this post not found"));
        if (userInfo.getUserId() == upPostRequest.getPostUserId()) {
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
            } else if (upPostRequest.isDeleteImages()) {
                if (!imageList.isEmpty()) {
                    for (Image image : imageList) {
                        image.setImageFlagDelete(1);
                        imageRepository.save(image);
                    }
                }
            } else {
                imagesEdited = imageRepository.findImageByImagePostIdAndImageFlagDelete(upPostRequest.getPostId(),
                        0);
            }
            UserPost userPost1 = postMapper.toEntity(upPostRequest);
            userPost1.setPostDeleteFlag(0);
            UserPost userPostEdited = userPostRepository.save(userPost1);
            PostViewDto postViewDto = postViewMapper.toDto(userPostEdited);
            int comments = commentRepository.countCommentsByCommentPostId(postViewDto.getPostId());
            postViewDto.setComments(comments);
            int likePost = likeRepository.countLikeByLikePostIdAndLikeFlag(postViewDto.getPostId(),
                    1);
            postViewDto.setLike(likePost);
            postViewDto.setPostImages(imagesEdited);
            upPostResponse.setPostViewDto(postViewDto);
            upPostResponse.setMessage("Edit this post successfully!");
            upPostResponse.setStatus("200");

        } else {
            upPostResponse.setMessage("You cannot edit this post!");
            upPostResponse.setStatus("400");
        }
        return upPostResponse;
    }

    public PostResponse findPostById(int postId, int page, int size) {
        PostResponse postResponse = new PostResponse();
//        check friend
        String currentUserName = friendService.getListFriends()
                .getCurrentUserName();
        List<String> listUserNameCanSeePost = friendService.getListFriends()
                .getUserNameFriends();
        listUserNameCanSeePost.add(currentUserName);
        UserPost userPost = userPostRepository.findUserPostByPostIdAndAndPostDeleteFlag(postId,
                        0)
                .orElseThrow(() -> new PostNotFoundException("Post not exist!"));
        UserInfo userPostedThePost = userInfoRepository.findByUserId(userPost.getPostUserId())
                .orElseThrow(() -> new UserNotFoundException("Not found user!"));
        if (!listUserNameCanSeePost.contains(userPostedThePost.getUserName())) {
            postResponse.setMessage("You can not see the post because you and " + userPostedThePost.getUserName() + " are not friend!");
            postResponse.setStatus("400");
            return postResponse;
        }

        PostDto postDto = postMapper.toDto(userPost);
        List<Image> imageList = imageRepository.findImageByImagePostIdAndImageFlagDelete(userPost.getPostId(),
                0);

        Pageable pageable = PageRequest.of(page,
                size,
                Sort.by("commentCreateDate")
                        .descending());
        Page<Comment> commentList = commentRepository.findCommentByCommentPostId(userPost.getPostId(),
                pageable);
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
        return postResponse;
    }

    public DeletePostResponse deletePost(int idPost) {
        DeletePostResponse deletePostResponse = new DeletePostResponse();
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            UserInfo userInfo = userInfoRepository.findByUserName(currentUserName)
                    .orElseThrow(() -> new UserNotFoundException("Not found user!"));
            UserPost userPost = userPostRepository.findById(idPost)
                    .orElseThrow(() -> new PostNotFoundException("Not found post!"));
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
        }
        return deletePostResponse;
    }

//    public GetAllPostResponse getAllPosts() {
//        GetAllPostResponse getAllPostResponse = new GetAllPostResponse();
//        getAllPostResponse.setStatus("200");
//        getAllPostResponse.setMessage("Get all!");
//        List<UserPost> userPostList = userPostRepository.findAll();
//        List<PostDto> postDtos = new ArrayList<>();
//        for (UserPost userPost : userPostList) {
//            PostDto postDto = postMapper.toDto(userPost);
//            List<Image> imageList = imageRepository.findImageByImagePostIdAndImageFlagDelete(userPost.getPostId(),
//                    0);
//            List<Comment> commentList = commentRepository.findCommentByCommentPostId(userPost.getPostId());
//            if (!imageList.isEmpty()) {
//                postDto.setPostImages(imageList);
//            }
//            if (!commentList.isEmpty()) {
//                postDto.setPostComments(commentList);
//            }
//            postDto.setLike(likeRepository.countLikeByLikePostIdAndLikeFlag(userPost.getPostId(),
//                    1));
//            postDtos.add(postDto);
//        }
//        getAllPostResponse.setPosts(postDtos);
//        return getAllPostResponse;
//    }

    public LikeResponse likePost(int postId) {
        LikeRequest likeRequest = new LikeRequest();
        LikeResponse likeResponse = new LikeResponse();
        UserPost userPost = userPostRepository.findUserPostByPostIdAndAndPostDeleteFlag(postId,
                        0)
                .orElseThrow(() -> new PostNotFoundException("Post not exist!"));
        likeRequest.setPostId(userPost.getPostId());
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            UserInfo userInfo = userInfoRepository.findByUserName(currentUserName)
                    .orElseThrow(() -> new UserNotFoundException("Not found user!"));
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
        List<PostViewDto> posts = new ArrayList<>();
        for (String userNameFriend : userNameFriends) {
            UserInfo userInfo = userInfoRepository.findByUserName(userNameFriend)
                    .get();
            List<PostViewDto> postsByIdUser = getAllPostsByUserId(userInfo.getUserId());
            posts.addAll(postsByIdUser);
        }
        List<PostViewDto> myPosts = getAllPostsByUserId(userInfoRepository.findByUserName(getListFriendResponse.getCurrentUserName())
                .get()
                .getUserId());
        posts.addAll(myPosts);
        Collections.sort(posts,
                Comparator.comparing(PostViewDto::getPostCreateDate)
                        .reversed());
        getNewFeedResponse.setStatus("200");
        getNewFeedResponse.setMessage("Get new feed successful!");
        getNewFeedResponse.setPostViewDtos(posts);
        System.out.println(posts.size());
        return getNewFeedResponse;
    }

    public List<PostViewDto> getAllPostsByUserId(int userId) {
        List<UserPost> userPostList = userPostRepository.findUserPostsByPostUserIdAndAndPostDeleteFlag(userId,
                0);
        List<PostViewDto> postViewDtos = new ArrayList<>();
        if (!userPostList.isEmpty()) {
            for (UserPost userPost : userPostList) {
                PostViewDto postViewDto = postViewMapper.toDto(userPost);
                List<Image> imageList = imageRepository.findImageByImagePostIdAndImageFlagDelete(userPost.getPostId(),
                        0);
                int comments = commentRepository.countCommentsByCommentPostId(userPost.getPostId());
                if (!imageList.isEmpty()) {
                    postViewDto.setPostImages(imageList);
                }
                postViewDto.setComments(comments);
                postViewDto.setLike(likeRepository.countLikeByLikePostIdAndLikeFlag(userPost.getPostId(),
                        1));
                postViewDtos.add(postViewDto);
            }
        }
        return postViewDtos;
    }

    //  xem cac bai viet cua ban than dang
    public GetMyPostsResponse getMyPosts() {
        GetMyPostsResponse getMyPostsResponse = new GetMyPostsResponse();
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Optional<UserInfo> userInfo = Optional.ofNullable(userInfoRepository.findByUserName(authentication.getName())
                    .orElseThrow(() -> new UserNotFoundException("Not found user has userName: " + authentication.getName())));
            getMyPostsResponse.setUserId(userInfo.get()
                    .getUserId());
        }
        List<PostViewDto> myPosts = getAllPostsByUserId(getMyPostsResponse.getUserId());
        Collections.sort(myPosts,
                Comparator.comparing(PostViewDto::getPostCreateDate)
                        .reversed());
        getMyPostsResponse.setMyPosts(myPosts);
        getMyPostsResponse.setMessage("Success!");
        getMyPostsResponse.setStatus(200);
        return getMyPostsResponse;
    }


    //    lay ra danh sach nguoi like bai post
    public UserLikePostResponse getUserLikePost(int postId) {
        UserLikePostResponse userLikePostResponse = new UserLikePostResponse();
        UserPost userPost = userPostRepository.findUserPostByPostIdAndAndPostDeleteFlag(postId,
                        0)
                .orElseThrow(() -> new PostNotFoundException("Post not exist!"));
//            tìm user là người đăng bài viết
        UserInfo userPostPost = userInfoRepository.findByUserId(userPost.getPostUserId())
                .orElseThrow(() -> new UserNotFoundException("User not exist!"));

//            check quyền curentUser có được xem bài viết hay không
        List<String> listCanSeePostsUserName = friendService.getListFriends()
                .getUserNameFriends();
        listCanSeePostsUserName.add(friendService.getListFriends()
                .getCurrentUserName());
        if (listCanSeePostsUserName.contains(userPostPost.getUserName())) {
            List<Like> listLikeByPost = likeRepository.findLikeByLikePostId(postId);
            List<String> listUsernameLikePost = new ArrayList<>();
            for (Like like : listLikeByPost) {
                UserInfo userInfo = userInfoRepository.findById(like.getLikeUserId())
                        .orElseThrow(() -> new UserNotFoundException("User not exist!"));
                listUsernameLikePost.add(userInfo.getUserName());
            }
            userLikePostResponse.setStatus("200");
            userLikePostResponse.setMessage("Get list userName like post successfully!");
            userLikePostResponse.setUserNamesLikePost(listUsernameLikePost);
        } else {
            userLikePostResponse.setStatus("400");
            userLikePostResponse.setMessage("You can not see this post!");
            userLikePostResponse.setUserNamesLikePost(null);
        }
        return userLikePostResponse;
    }

}
