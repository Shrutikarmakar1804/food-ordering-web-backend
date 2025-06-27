package com.foodsphere.controller;

import com.foodsphere.model.User;
import com.foodsphere.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<User> findUserByJwt(@RequestHeader("Authorization") String jwt) throws Exception {
        return new ResponseEntity<>(userService.findUserByJwt(jwt), HttpStatus.OK);
    }

    @GetMapping("/profile/{email}")
    public ResponseEntity<User> findUserByEmail(@PathVariable String email) throws Exception {
        return new ResponseEntity<>(userService.findUserByEmail(email), HttpStatus.OK);
    }
}
