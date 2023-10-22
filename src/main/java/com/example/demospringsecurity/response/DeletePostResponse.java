package com.example.demospringsecurity.response;

import com.example.demospringsecurity.dto.PostDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeletePostResponse {
    private String status;
    private String message;
    private int idPostDelete;
}
