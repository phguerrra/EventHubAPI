package com.grupo1.backGrupo1.controller;

import org.springframework.web.bind.annotation.*;
import com.grupo1.backGrupo1.service.UserService;
import com.grupo1.backGrupo1.dto.UserDTO;
import com.grupo1.backGrupo1.model.User;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public User register(@RequestBody UserDTO dto) {
        return service.register(dto);
    }
}