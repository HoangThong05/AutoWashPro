package com.autowashpro.service;

import com.autowashpro.dto.response.CustomerDTO;
import com.autowashpro.repository.CustomerRepository;
import com.autowashpro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public CustomerDTO getMyProfile(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return customerRepository.findByUser_UserId(user.getUserId())
                .map(CustomerDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomerById(Integer id) {
        return customerRepository.findById(id)
                .map(CustomerDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }
}