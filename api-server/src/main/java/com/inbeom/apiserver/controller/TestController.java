package com.inbeom.apiserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/bcrypt/{password}")
    public Map<String, Object> generateBCrypt(@PathVariable String password) {
        String hash = passwordEncoder.encode(password);
        String dbHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

        Map<String, Object> result = new HashMap<>();
        result.put("rawPassword", password);
        result.put("newHash", hash);
        result.put("dbHash", dbHash);
        result.put("dbHashMatches", passwordEncoder.matches(password, dbHash));
        result.put("newHashMatches", passwordEncoder.matches(password, hash));

        return result;
    }
}
