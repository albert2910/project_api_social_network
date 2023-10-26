package com.example.demospringsecurity.controller;

import com.example.demospringsecurity.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/report")
public class ReportController {
    @Autowired
    UserService userService;

//   xuat file bao cao
    @GetMapping("/me")
    public void exportIntoExcelFile(HttpServletResponse response) throws IOException {
        userService.exportReport(response);
    }

}
