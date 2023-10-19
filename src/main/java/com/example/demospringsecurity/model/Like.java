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
@Table(name = "TBPost_like")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private int likeId;

    @Column(name = "like_post_id")
    private int likePostId;

    @Column(name = "like_user_id")
    private int likeUserId;

//    likeflag = 0 => dislike
//    likeflag = 1 => like
    @Column(name = "like_flag")
    private int likeFlag;

    @Column(name = "like_time_create")
    private int likeCreateDate;
}
