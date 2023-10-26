package com.example.demospringsecurity.controller;

import com.example.demospringsecurity.response.GetNewFeedResponse;
import com.example.demospringsecurity.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/news")
@Validated
public class HomeController {
    @Autowired
    PostService postService;
    @GetMapping()
    public GetNewFeedResponse getNewFeed() {
        return postService.getNewFeed();
    }
}
