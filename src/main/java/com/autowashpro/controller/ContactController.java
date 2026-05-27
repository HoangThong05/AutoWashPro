package com.autowashpro.controller;

import com.autowashpro.dto.request.ContactRequest;
import com.autowashpro.dto.response.ContactMessageResponse;
import com.autowashpro.dto.response.ContactResponse;
import com.autowashpro.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    /** POST /api/contacts — Khách gửi tin (public) */
    @PostMapping
    public ResponseEntity<ContactResponse> send(
            @Valid @RequestBody ContactRequest req) {
        return ResponseEntity.status(201)
                .body(contactService.sendMessage(req));
    }

    /** GET /api/contacts/my — Khách xem tin + reply */
    @GetMapping("/my")
    public ResponseEntity<List<ContactResponse>> getMy(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                contactService.getMyContacts(userDetails.getUsername()));
    }

    /** POST /api/contacts/{id}/reply-customer — Khách gửi thêm tin */
    @PostMapping("/{id}/reply-customer")
    public ResponseEntity<ContactMessageResponse> customerReply(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                contactService.customerReply(
                        id, body.get("message"), userDetails.getUsername()));
    }

    /** GET /api/contacts — Staff/Manager xem tất cả */
    @GetMapping
    public ResponseEntity<List<ContactResponse>> getAll(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(contactService.getAll(status));
    }

    /** GET /api/contacts/{id} — Xem chi tiết + messages */
    @GetMapping("/{id}")
    public ResponseEntity<ContactResponse> getById(
            @PathVariable Integer id) {
        return ResponseEntity.ok(contactService.getById(id));
    }

    /** POST /api/contacts/{id}/reply — Staff reply */
    @PostMapping("/{id}/reply")
    public ResponseEntity<ContactMessageResponse> staffReply(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                contactService.staffReply(
                        id, body.get("message"), userDetails.getUsername()));
    }
}