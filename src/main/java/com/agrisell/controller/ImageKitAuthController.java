package com.agrisell.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class ImageKitAuthController {

    @Value("${imagekit.privateKey}")
    private String privateKey;

    @GetMapping("/imagekit")
    public ResponseEntity<Map<String, String>> getImageKitAuth() {
        try {
            // 1️⃣ Generate random token and expiry
            String token = UUID.randomUUID().toString();
            long expire = (System.currentTimeMillis() / 1000L) + 2400; // 40 mins validity

            // 2️⃣ Create signature using HMAC-SHA1
            String data = token + expire;
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secretKey = new SecretKeySpec(privateKey.getBytes(), "HmacSHA1");
            mac.init(secretKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : rawHmac) sb.append(String.format("%02x", b));
            String signature = sb.toString();

            // 3️⃣ Prepare response
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("expire", String.valueOf(expire));
            response.put("signature", signature);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            throw new RuntimeException("Error generating ImageKit auth parameters", e);
        }
    }
}
