package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.service.impl.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/email")
@RequiredArgsConstructor
@Slf4j
public class EmailController {
    @Autowired
    private EmailService emailService;
     @PostMapping("")
     public ResponseEntity<?> sendEmail(@Valid  @RequestParam String email) {
         emailService.sendEmail2(email);

         return ResponseEntity.ok().build();
     }
}
