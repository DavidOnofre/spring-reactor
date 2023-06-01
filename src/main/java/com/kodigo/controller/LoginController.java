package com.kodigo.controller;

import com.kodigo.security.AuthRequest;
import com.kodigo.security.AuthResponse;
import com.kodigo.security.ErrorLogin;
import com.kodigo.security.JwtUtil;
import com.kodigo.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.bcrypt.BCrypt;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Date;

// Class s9

@RestController
@RequiredArgsConstructor
public class LoginController {
    private final JwtUtil jwtUtil;
    private final IUserService service;

    @PostMapping("/login")
    public Mono<ResponseEntity<?>> login(@RequestBody AuthRequest authRequest) {
        return service.searchByUser(authRequest.getUsername())
                .map(userDetails -> {
                    if (BCrypt.checkpw(authRequest.getPassword(), userDetails.getPassword())) {
                        String token = jwtUtil.generateToken(userDetails);
                        Date expiration = jwtUtil.getExpirationDateFromToken(token);
                        return ResponseEntity.ok(new AuthResponse(token, expiration));
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(new ErrorLogin("Bad Credentials", new Date()));
                    }
                })
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
