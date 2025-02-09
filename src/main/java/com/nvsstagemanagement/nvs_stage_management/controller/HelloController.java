package com.nvsstagemanagement.nvs_stage_management.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    String sayHello(){
        return "hello this is state management project ~";
    }
}
