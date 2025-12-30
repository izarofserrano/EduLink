package com.edulink.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/speedtest")
public class SpeedTestController {

    private static final Random RANDOM = new Random();

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("server", "EduLink Speed Test Server");
        info.put("version", "1.0");
        info.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(info);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam(defaultValue = "1024") int size) {
        // Size in KB → convert to bytes
        int byteSize = size * 1024;
        byte[] data = new byte[byteSize];
        RANDOM.nextBytes(data); // Fill with random data

        return ResponseEntity.ok()
                .header("Content-Type", "application/octet-stream")
                .header("Content-Length", String.valueOf(byteSize))
                .body(data);
    }
}