package com.chatapp.user.controller;

import com.chatapp.shared.jwt.JWTUtil;
import com.chatapp.user.dto.ChatUserDTO;
import com.chatapp.user.dto.ChatUserRegistrationRequestDTO;
import com.chatapp.user.service.ChatUserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class ChatUserController {
    private final ChatUserService chatUserService;
    private final JWTUtil jwtUtil;

    public ChatUserController(ChatUserService chatUserService, JWTUtil jwtUtil) {

        this.chatUserService = chatUserService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    ResponseEntity<?> registerUser(@RequestBody ChatUserRegistrationRequestDTO request){
        chatUserService.addCustomer(request);
        String jwtToken = jwtUtil.issueToken(request.email(), "USER");
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .build();
    }

    @GetMapping("")
    public List<ChatUserDTO> getAllUsers() {
        return chatUserService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ChatUserDTO getUserById(@PathVariable("id") Long id) {
        return chatUserService.getUserById(id);
    }
}
