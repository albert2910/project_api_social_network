package com.example.demospringsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportUserDto {
    private String userName;
    private int postsLastWeek;
    private int newFriendLastWeek;
    private int newLikesLastWeek;
    private int newCommentsLastWeek;
}
