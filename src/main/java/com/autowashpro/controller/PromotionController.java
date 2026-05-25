package com.autowashpro.controller;

import com.autowashpro.dto.request.PromotionRequest;
import com.autowashpro.dto.response.PromotionResponse;
import com.autowashpro.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    /** GET /api/promotions — Admin/Manager xem tất cả */
    @GetMapping
    public ResponseEntity<List<PromotionResponse>> getAll() {
        return ResponseEntity.ok(promotionService.getAll());
    }

    /** GET /api/promotions/active — Customer xem promotion đang active */
    @GetMapping("/active")
    public ResponseEntity<List<PromotionResponse>> getActive() {
        return ResponseEntity.ok(promotionService.getActive());
    }

    /** GET /api/promotions/check/{code} — Customer kiểm tra mã */
    @GetMapping("/check/{code}")
    public ResponseEntity<PromotionResponse> checkCode(@PathVariable String code) {
        return ResponseEntity.ok(promotionService.checkCode(code));
    }

    /** POST /api/promotions — Admin/Manager tạo mới */
    @PostMapping
    public ResponseEntity<PromotionResponse> create(
            @Valid @RequestBody PromotionRequest req) {
        return ResponseEntity.status(201).body(promotionService.create(req));
    }

    /** PUT /api/promotions/{id} — Admin/Manager cập nhật */
    @PutMapping("/{id}")
    public ResponseEntity<PromotionResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody PromotionRequest req) {
        return ResponseEntity.ok(promotionService.update(id, req));
    }

    /** DELETE /api/promotions/{id} — Admin/Manager vô hiệu hóa */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Integer id) {
        promotionService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}