package com.example.demospringsecurity.service;

import com.example.demospringsecurity.dto.PostDto;
import com.example.demospringsecurity.dto.PostViewDto;
import com.example.demospringsecurity.dto.request.UpPostRequest;
import com.example.demospringsecurity.exceptions.PostNotFoundException;
import com.example.demospringsecurity.mapper.PostMapper;
import com.example.demospringsecurity.mapper.PostViewMapper;
import com.example.demospringsecurity.model.Comment;
import com.example.demospringsecurity.model.Image;
import com.example.demospringsecurity.model.UserInfo;
import com.example.demospringsecurity.model.UserPost;
import com.example.demospringsecurity.repository.*;
import com.example.demospringsecurity.response.friend.GetListFriendResponse;
import com.example.demospringsecurity.response.post.PostResponse;
import com.example.demospringsecurity.response.post.UpPostResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @InjectMocks
    PostService postService;

    @Mock
    PostMapper postMapper;

    @Mock
    PostViewMapper postViewMapper;

    @Mock
    UserPostRepository userPostRepository;

    @Mock
    UserInfoRepository userInfoRepository;

    @Mock
    ImageRepository imageRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    LikeRepository likeRepository;

    @Mock
    FriendService friendService;

    @Test
    void upPost_accessDenied() {
        UpPostRequest upPostRequest = new UpPostRequest();
        Authentication authentication = Mockito.mock(AnonymousAuthenticationToken.class);
// Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication())
                .thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        UpPostResponse upPostResponse = postService.upPost(upPostRequest);
        Assertions.assertNull(upPostResponse.getMessage());
    }

    @Test
    void upPost_success() {
        UpPostRequest upPostRequest = new UpPostRequest();
        List<String> imageList = new ArrayList<>();
        imageList.add("lalalalala.jpg");
        upPostRequest.setPostUrlImages(imageList);
        Authentication authentication = Mockito.mock(Authentication.class);
// Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(authentication.getName())
                .thenReturn("oasjdioashid");
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(securityContext.getAuthentication())
                .thenReturn(authentication);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(1);
        Mockito.when(userInfoRepository.findByUserName(Mockito.anyString()))
                .thenReturn(Optional.of(userInfo));
        UserPost userPostt = new UserPost();
        Mockito.when(postMapper.toEntity(Mockito.any()))
                .thenReturn(userPostt);
        UserPost userPostSaved = new UserPost();
        userPostSaved.setPostId(1);
        Mockito.when(userPostRepository.save(Mockito.any()))
                .thenReturn(userPostSaved);
        PostViewDto postViewDto = new PostViewDto();
        Mockito.when(postViewMapper.toDto(Mockito.any()))
                .thenReturn(postViewDto);
        Image imageSaved = new Image(1,
                "lalalala.jpg",
                1,
                0);
        Mockito.when(imageRepository.save(Mockito.any()))
                .thenReturn(imageSaved);
        UpPostResponse upPostResponse = postService.upPost(upPostRequest);
        Assertions.assertEquals("Up post success!",
                upPostResponse.getMessage());
        Assertions.assertNotNull(upPostResponse.getPostViewDto());
    }

    @Test
    void editPost_postNotFound() {
        UpPostRequest upPostRequest = new UpPostRequest();
        upPostRequest.setPostId(1);
        List<String> imageList = new ArrayList<>();
        imageList.add("lalalalala.jpg");
        upPostRequest.setPostUrlImages(imageList);
        Authentication authentication = Mockito.mock(Authentication.class);
// Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(authentication.getName())
                .thenReturn("oasjdioashid");
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(securityContext.getAuthentication())
                .thenReturn(authentication);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(1);
        Mockito.when(userInfoRepository.findByUserName(Mockito.anyString())).thenReturn(Optional.of(userInfo));
        Mockito.when(userPostRepository.findUserPostByPostIdAndAndPostDeleteFlag(Mockito.anyInt(),
                        Mockito.anyInt()))
                .thenThrow(PostNotFoundException.class);
        Assertions.assertThrows(PostNotFoundException.class, () -> postService.upPost(upPostRequest));
    }

    @Test
    void editPost_cannotEdit() {
        UpPostRequest upPostRequest = new UpPostRequest();
        upPostRequest.setPostId(1);
        upPostRequest.setPostUserId(1);
        List<String> imageList = new ArrayList<>();
        imageList.add("lalalalala.jpg");
        upPostRequest.setPostUrlImages(imageList);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(2);
        Authentication authentication = Mockito.mock(Authentication.class);
// Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        SecurityContextHolder.setContext(securityContext);
        Mockito.when(securityContext.getAuthentication())
                .thenReturn(authentication);
        Mockito.when(authentication.getName())
                .thenReturn("oasjdioashid");
        Mockito.when(userInfoRepository.findByUserName(Mockito.anyString())).thenReturn(Optional.of(userInfo));
        UserPost userPost = new UserPost();
        userPost.setPostUserId(1);
        userPost.setPostCreateDate(LocalDateTime.of(2023,10,29,12,12,12,12));
        Mockito.when(userPostRepository.findUserPostByPostIdAndAndPostDeleteFlag(Mockito.anyInt(),
                        Mockito.anyInt()))
                .thenReturn(Optional.of(userPost));

        Mockito.when(userInfoRepository.findByUserId(Mockito.anyInt())).thenReturn(Optional.of(userInfo));

        UpPostResponse upPostResponse = postService.upPost(upPostRequest);
        Assertions.assertEquals("You cannot edit this post!",
                upPostResponse.getMessage());
        Assertions.assertNull(upPostResponse.getPostViewDto());
    }

    @Test
    void editPost_success1() {
        UpPostRequest upPostRequest = new UpPostRequest();
        upPostRequest.setDeleteImages(false);
        upPostRequest.setPostId(1);
        upPostRequest.setPostUserId(1);

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(1);
        Authentication authentication = Mockito.mock(Authentication.class);
// Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        SecurityContextHolder.setContext(securityContext);
        Mockito.when(securityContext.getAuthentication())
                .thenReturn(authentication);
        Mockito.when(authentication.getName())
                .thenReturn("oasjdioashid");
        Mockito.when(userInfoRepository.findByUserName(Mockito.anyString())).thenReturn(Optional.of(userInfo));
        UserPost userPost = new UserPost();
        userPost.setPostUserId(1);
        userPost.setPostCreateDate(LocalDateTime.of(2023,10,29,12,12,12,12));
        Mockito.when(userPostRepository.findUserPostByPostIdAndAndPostDeleteFlag(Mockito.anyInt(),
                        Mockito.anyInt()))
                .thenReturn(Optional.of(userPost));

        Mockito.when(userInfoRepository.findByUserId(Mockito.anyInt())).thenReturn(Optional.of(userInfo));

        List<Image> list = new ArrayList<>();
        Image image = new Image(1,"asjkdhask.jpg",1,0);
        list.add(image);
        Mockito.when(imageRepository.findImageByImagePostIdAndImageFlagDelete(Mockito.anyInt(),
                Mockito.anyInt())).thenReturn(list);

        Mockito.when(postMapper.toEntity(Mockito.any())).thenReturn(userPost);
        Mockito.when(userPostRepository.save(Mockito.any())).thenReturn(userPost);
        PostViewDto postViewDto = new PostViewDto();
        Mockito.when(postViewMapper.toDto(Mockito.any())).thenReturn(postViewDto);
        Mockito.when(commentRepository.countCommentsByCommentPostId(Mockito.anyInt())).thenReturn(3);
        Mockito.when(likeRepository.countLikeByLikePostIdAndLikeFlag(Mockito.anyInt(), Mockito.anyInt())).thenReturn(3);

        UpPostResponse upPostResponse = postService.upPost(upPostRequest);
        Assertions.assertEquals("Edit this post successfully!",
                upPostResponse.getMessage());
        Assertions.assertNotNull(upPostResponse.getPostViewDto());
    }

    @Test
    void editPost_success2() {
        UpPostRequest upPostRequest = new UpPostRequest();
        upPostRequest.setDeleteImages(true);
        upPostRequest.setPostId(1);
        upPostRequest.setPostUserId(1);
        List<String> imageList = new ArrayList<>();
        imageList.add("lalalalala.jpg");
        upPostRequest.setPostUrlImages(imageList);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(1);
        Authentication authentication = Mockito.mock(Authentication.class);
// Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        SecurityContextHolder.setContext(securityContext);
        Mockito.when(securityContext.getAuthentication())
                .thenReturn(authentication);
        Mockito.when(authentication.getName())
                .thenReturn("oasjdioashid");
        Mockito.when(userInfoRepository.findByUserName(Mockito.anyString())).thenReturn(Optional.of(userInfo));
        UserPost userPost = new UserPost();
        userPost.setPostUserId(1);
        userPost.setPostCreateDate(LocalDateTime.of(2023,10,29,12,12,12,12));
        Mockito.when(userPostRepository.findUserPostByPostIdAndAndPostDeleteFlag(Mockito.anyInt(),
                        Mockito.anyInt()))
                .thenReturn(Optional.of(userPost));

        Mockito.when(userInfoRepository.findByUserId(Mockito.anyInt())).thenReturn(Optional.of(userInfo));

        List<Image> list = new ArrayList<>();
        Image image = new Image(1,"asjkdhask.jpg",1,0);
        list.add(image);
        Mockito.when(imageRepository.findImageByImagePostIdAndImageFlagDelete(Mockito.anyInt(),
                Mockito.anyInt())).thenReturn(list);

        Mockito.when(postMapper.toEntity(Mockito.any())).thenReturn(userPost);
        Mockito.when(userPostRepository.save(Mockito.any())).thenReturn(userPost);
        PostViewDto postViewDto = new PostViewDto();
        Mockito.when(postViewMapper.toDto(Mockito.any())).thenReturn(postViewDto);
        Mockito.when(commentRepository.countCommentsByCommentPostId(Mockito.anyInt())).thenReturn(3);
        Mockito.when(likeRepository.countLikeByLikePostIdAndLikeFlag(Mockito.anyInt(), Mockito.anyInt())).thenReturn(3);

        UpPostResponse upPostResponse = postService.upPost(upPostRequest);
        Assertions.assertEquals("Edit this post successfully!",
                upPostResponse.getMessage());
        Assertions.assertNotNull(upPostResponse.getPostViewDto());
    }

    @Test
    void editPost_success3() {
        UpPostRequest upPostRequest = new UpPostRequest();
        upPostRequest.setDeleteImages(true);
        upPostRequest.setPostId(1);
        upPostRequest.setPostUserId(1);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(1);
        Authentication authentication = Mockito.mock(Authentication.class);
// Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        SecurityContextHolder.setContext(securityContext);
        Mockito.when(securityContext.getAuthentication())
                .thenReturn(authentication);
        Mockito.when(authentication.getName())
                .thenReturn("oasjdioashid");
        Mockito.when(userInfoRepository.findByUserName(Mockito.anyString())).thenReturn(Optional.of(userInfo));
        UserPost userPost = new UserPost();
        userPost.setPostUserId(1);
        userPost.setPostCreateDate(LocalDateTime.of(2023,10,29,12,12,12,12));
        Mockito.when(userPostRepository.findUserPostByPostIdAndAndPostDeleteFlag(Mockito.anyInt(),
                        Mockito.anyInt()))
                .thenReturn(Optional.of(userPost));

        Mockito.when(userInfoRepository.findByUserId(Mockito.anyInt())).thenReturn(Optional.of(userInfo));

        List<Image> list = new ArrayList<>();
        Image image = new Image(1,"asjkdhask.jpg",1,0);
        list.add(image);
        Mockito.when(imageRepository.findImageByImagePostIdAndImageFlagDelete(Mockito.anyInt(),
                Mockito.anyInt())).thenReturn(list);

        Mockito.when(postMapper.toEntity(Mockito.any())).thenReturn(userPost);
        Mockito.when(userPostRepository.save(Mockito.any())).thenReturn(userPost);
        PostViewDto postViewDto = new PostViewDto();
        Mockito.when(postViewMapper.toDto(Mockito.any())).thenReturn(postViewDto);
        Mockito.when(commentRepository.countCommentsByCommentPostId(Mockito.anyInt())).thenReturn(3);
        Mockito.when(likeRepository.countLikeByLikePostIdAndLikeFlag(Mockito.anyInt(), Mockito.anyInt())).thenReturn(3);

        UpPostResponse upPostResponse = postService.upPost(upPostRequest);
        Assertions.assertEquals("Edit this post successfully!",
                upPostResponse.getMessage());
        Assertions.assertNotNull(upPostResponse.getPostViewDto());
    }

    @Test
    void findPostById_success1() {
        List<String> userNames = new ArrayList<>();
        userNames.add("abc");
        userNames.add("doremi");
        GetListFriendResponse getListFriendResponse = new GetListFriendResponse("200","Ok","nambeoi",userNames);
        Mockito.when(friendService.getListFriends()).thenReturn(getListFriendResponse);
        UserPost userPost = new UserPost();
        userPost.setPostUserId(1);
        Mockito.when(userPostRepository.findUserPostByPostIdAndAndPostDeleteFlag(Mockito.anyInt(),Mockito.anyInt())).thenReturn(Optional.of(userPost));
        UserInfo userPostedThePost = new UserInfo();
        userPostedThePost.setUserId(1);
        userPostedThePost.setUserName("doremi");
        Mockito.when(userInfoRepository.findByUserId(Mockito.anyInt())).thenReturn(Optional.of(userPostedThePost));
        PostDto postDto = new PostDto();
        postDto.setPostId(1);
        Mockito.when(postMapper.toDto(Mockito.any())).thenReturn(postDto);
        List<Image> list = new ArrayList<>();
        Image image = new Image(1,"asjkdhask.jpg",1,0);
        list.add(image);
        Mockito.when(imageRepository.findImageByImagePostIdAndImageFlagDelete(Mockito.anyInt(),Mockito.anyInt())).thenReturn(list);
        Page<Comment> commentList = new PageImpl<>(new ArrayList<>());
        Mockito.when(commentRepository.findCommentByCommentPostId(Mockito.anyInt(),Mockito.any())).thenReturn(commentList);
        Mockito.when(likeRepository.countLikeByLikePostIdAndLikeFlag(Mockito.anyInt(),Mockito.anyInt())).thenReturn(5);
        PostResponse postResponse = postService.findPostById(1,0,3);
        Assertions.assertEquals("This is post!", postResponse.getMessage());
        Assertions.assertNotNull(postResponse.getPostDto());
    }

    @Test
    void findPostById_success2() {
        List<String> userNames = new ArrayList<>();
        userNames.add("abc");
        userNames.add("doremi");
        GetListFriendResponse getListFriendResponse = new GetListFriendResponse("200","Ok","nambeoi",userNames);
        Mockito.when(friendService.getListFriends()).thenReturn(getListFriendResponse);
        UserPost userPost = new UserPost();
        userPost.setPostUserId(1);
        Mockito.when(userPostRepository.findUserPostByPostIdAndAndPostDeleteFlag(Mockito.anyInt(),Mockito.anyInt())).thenReturn(Optional.of(userPost));
        UserInfo userPostedThePost = new UserInfo();
        userPostedThePost.setUserId(1);
        userPostedThePost.setUserName("doremi");
        Mockito.when(userInfoRepository.findByUserId(Mockito.anyInt())).thenReturn(Optional.of(userPostedThePost));
        PostDto postDto = new PostDto();
        postDto.setPostId(1);
        Mockito.when(postMapper.toDto(Mockito.any())).thenReturn(postDto);
        List<Image> list = new ArrayList<>();
        Image image = new Image(1,"asjkdhask.jpg",1,0);
        list.add(image);
        Mockito.when(imageRepository.findImageByImagePostIdAndImageFlagDelete(Mockito.anyInt(),Mockito.anyInt())).thenReturn(list);
        Comment comment = new Comment(1,"lua han thu dot chay ki uc hai ta",1,1,LocalDateTime.now());
        List<Comment> commentList = new ArrayList<>();
        commentList.add(comment);
        Page<Comment> commentPage = new PageImpl<>(commentList);
        Mockito.when(commentRepository.findCommentByCommentPostId(Mockito.anyInt(),Mockito.any())).thenReturn(commentPage);
        Mockito.when(likeRepository.countLikeByLikePostIdAndLikeFlag(Mockito.anyInt(),Mockito.anyInt())).thenReturn(5);
        PostResponse postResponse = postService.findPostById(1,0,3);
        Assertions.assertEquals("This is post!", postResponse.getMessage());
        Assertions.assertNotNull(postResponse.getPostDto());
    }

    @Test
    void findPostById_cantSeePost() {
        List<String> userNames = new ArrayList<>();
        userNames.add("abc");
        userNames.add("doremi");
        GetListFriendResponse getListFriendResponse = new GetListFriendResponse("200","Ok","nambeoi",userNames);
        Mockito.when(friendService.getListFriends()).thenReturn(getListFriendResponse);
        UserPost userPost = new UserPost();
        userPost.setPostUserId(1);
        Mockito.when(userPostRepository.findUserPostByPostIdAndAndPostDeleteFlag(Mockito.anyInt(),Mockito.anyInt())).thenReturn(Optional.of(userPost));
        UserInfo userPostedThePost = new UserInfo();
        userPostedThePost.setUserId(1);
        userPostedThePost.setUserName("lalala");
        Mockito.when(userInfoRepository.findByUserId(Mockito.anyInt())).thenReturn(Optional.of(userPostedThePost));
        PostResponse postResponse = postService.findPostById(1,0,3);
        Assertions.assertNull(postResponse.getPostDto());
    }




}