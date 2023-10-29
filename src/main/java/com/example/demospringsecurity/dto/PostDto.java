package com.example.demospringsecurity.dto;

import com.example.demospringsecurity.model.Comment;
import com.example.demospringsecurity.model.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private int postId;
    private String postContent;
    private List<Image> postImages;
    private Page<Comment> postComments;
    private int like;
    private int postUserId;
    private LocalDateTime postCreateDate;
    private int postDeleteFlag;

}

