package com.autowashpro.controller;

import com.autowashpro.dto.response.MemberTierDTO;
import com.autowashpro.entity.MemberTier;
import com.autowashpro.repository.MemberTierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tiers")
@RequiredArgsConstructor
public class MemberTierController {

    private final MemberTierRepository memberTierRepository;

    // GET tất cả hạng thành viên — public, không cần token
    @GetMapping
    public ResponseEntity<List<MemberTierDTO>> getAllTiers() {
        return ResponseEntity.ok(
            memberTierRepository.findAll()
                .stream()
                .map(MemberTierDTO::fromEntity)
                .collect(Collectors.toList())
        );
    }

    // GET 1 hạng theo ID
    @GetMapping("/{id}")
    public ResponseEntity<MemberTierDTO> getTierById(@PathVariable Integer id) {
        return memberTierRepository.findById(id)
                .map(MemberTierDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}