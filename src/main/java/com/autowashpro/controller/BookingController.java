package com.autowashpro.controller;

import com.autowashpro.dto.request.BookingRequest;
import com.autowashpro.dto.request.BookingStatusRequest;
import com.autowashpro.dto.response.BookingResponse;
import com.autowashpro.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /** GET /api/bookings/slots?date=2026-06-01 — public */
    @GetMapping("/slots")
    public ResponseEntity<List<Map<String, Object>>> getSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(bookingService.getAvailableSlots(date));
    }

    /** POST /api/bookings — CUSTOMER tạo booking */
    @PostMapping
    public ResponseEntity<BookingResponse> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody BookingRequest req) {
        return ResponseEntity.status(201).body(
                bookingService.createBooking(userDetails.getUsername(), req));
    }

    /** GET /api/bookings/my — CUSTOMER xem booking của mình */
    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                bookingService.getMyBookings(userDetails.getUsername()));
    }

    /** GET /api/bookings/{id} — xem chi tiết */
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(bookingService.getById(id));
    }

    /** PUT /api/bookings/{id}/status — Staff/Manager cập nhật trạng thái */
    @PutMapping("/{id}/status")
    public ResponseEntity<BookingResponse> updateStatus(
            @PathVariable Integer id,
            @Valid @RequestBody BookingStatusRequest req) {
        return ResponseEntity.ok(bookingService.updateStatus(id, req));
    }

    /** PATCH /api/bookings/{id}/cancel — Customer hủy booking */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancel(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer id,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(
                bookingService.cancelBooking(userDetails.getUsername(), id, reason));
    }
}