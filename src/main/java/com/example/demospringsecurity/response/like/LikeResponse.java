package com.example.demospringsecurity.response.like;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeResponse {
    private String status;
    private String message;
    private boolean liked;
}
