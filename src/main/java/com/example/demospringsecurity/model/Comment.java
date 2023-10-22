package com.example.demospringsecurity.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "TBPost_comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private int commentId;

    @Column(name = "comment_content")
    private String commentContent;

    @Column(name = "comment_post_id")
    private int commentPostId;

    @Column(name = "comment_user_id")
    private int commentUserId;

    @Column(name = "comment_date_create")
    private LocalDateTime commentCreateDate;

}
