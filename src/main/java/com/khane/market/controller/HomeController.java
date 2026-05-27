package com.khane.market.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public ResponseEntity<Map<String, String>> home() {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("application", "market");
        response.put("status", "running");
        response.put("version", "1.0");
        return ResponseEntity.ok(response);
    }
}