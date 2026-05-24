package com.autowashpro.controller;

import com.autowashpro.dto.request.LoginRequest;
import com.autowashpro.dto.request.RegisterRequest;
import com.autowashpro.dto.response.UserDTO;
import com.autowashpro.entity.User;
import com.autowashpro.repository.UserRepository;
import com.autowashpro.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // ── REGISTER ──────────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email đã được sử dụng!"));
        }
        if (userRepository.existsByPhone(req.getPhone())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Số điện thoại đã được sử dụng!"));
        }

        User user = new User();
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setRole(User.Role.CUSTOMER);
        user.setStatus(User.Status.ACTIVE);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "Đăng ký thành công!",
                "user", UserDTO.fromEntity(user)
        ));
    }

    // ── LOGIN ─────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            User user = userRepository.findByEmail(req.getEmail()).orElse(null);

            if (user == null) {
                return ResponseEntity.status(401)
                        .body(Map.of("message", "Email hoặc mật khẩu không đúng!"));
            }

            if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
                return ResponseEntity.status(401)
                        .body(Map.of("message", "Email hoặc mật khẩu không đúng!"));
            }

            if (user.getStatus() == User.Status.BANNED) {
                return ResponseEntity.status(403)
                        .body(Map.of("message", "Tài khoản đã bị khóa!"));
            }

            String token = jwtUtil.generateToken(
                    user.getEmail(),
                    user.getRole().name()
            );

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "user", UserDTO.fromEntity(user)
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Lỗi server: " + e.getMessage()));
        }
    }

    // ── GET ME ────────────────────────────────────────────────
    @GetMapping("/me")
    public ResponseEntity<?> getMe(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Chưa đăng nhập!"));
        }
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow();
        return ResponseEntity.ok(UserDTO.fromEntity(user));
    }
}