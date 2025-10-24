package com.abhi.practice.workwithgcpterraform.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/")
    public String hello() {
        return "Hello from Cloud Run!";
    }

    @GetMapping("/hi")
    public String hi() {
        return "Hi from Cloud Run!";
    }
}