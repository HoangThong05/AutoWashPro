package com.autowashpro.controller;

import com.autowashpro.dto.response.CustomerDTO;
import com.autowashpro.entity.User;
import com.autowashpro.repository.CustomerRepository;
import com.autowashpro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    // GET thông tin customer của user đang đăng nhập
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow();

        return customerRepository.findByUser_UserId(user.getUserId())
                .map(CustomerDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET tất cả customers (Admin/Manager)
    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return ResponseEntity.ok(
            customerRepository.findAll()
                .stream()
                .map(CustomerDTO::fromEntity)
                .collect(Collectors.toList())
        );
    }

    // GET customer theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Integer id) {
        return customerRepository.findById(id)
                .map(CustomerDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}