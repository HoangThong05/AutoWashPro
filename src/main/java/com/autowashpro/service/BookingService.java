package com.autowashpro.service;

import com.autowashpro.dto.request.BookingRequest;
import com.autowashpro.dto.request.BookingStatusRequest;
import com.autowashpro.dto.response.BookingResponse;
import com.autowashpro.entity.*;
import com.autowashpro.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingServiceItemRepository bookingServiceItemRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ServicePriceRepository servicePriceRepository;
    private final EmployeeRepository employeeRepository;

    /* ===== TẠO BOOKING ===== */
    @Transactional
    public BookingResponse createBooking(String email, BookingRequest req) {

        Customer customer = customerRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Vehicle vehicle = vehicleRepository.findById(req.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        if (!vehicle.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            throw new RuntimeException("Vehicle does not belong to you");
        }

        TimeSlot slot = timeSlotRepository.findById(req.getSlotId())
                .orElseThrow(() -> new RuntimeException("Time slot not found"));
        if (!slot.getIsActive()) {
            throw new RuntimeException("Time slot is not available");
        }

        // Kiểm tra slot còn chỗ không
        long slotCount = bookingRepository.countActiveBookingsInSlot(
                slot.getSlotId(), req.getBookingDate());
        if (slotCount >= slot.getMaxBookings()) {
            throw new RuntimeException("Time slot is fully booked");
        }

        // Kiểm tra advance booking hours theo tier
        MemberTier tier = customer.getTier();
        int advanceHours = (tier != null) ? tier.getAdvanceBookingHours() : 24;
        LocalDateTime earliest = LocalDateTime.now().plusHours(advanceHours);
        LocalDateTime slotDateTime = req.getBookingDate().atTime(slot.getStartTime());
        if (slotDateTime.isBefore(earliest)) {
            throw new RuntimeException(
                    "Must book at least " + advanceHours + " hours in advance");
        }

        // Kiểm tra max bookings per day theo tier
        if (tier != null) {
            long dayCount = bookingRepository.countBookingsByCustomerAndDate(
                    customer.getCustomerId(), req.getBookingDate());
            if (dayCount >= tier.getMaxBookingsPerDay()) {
                throw new RuntimeException("Exceeded max bookings per day");
            }
        }

        // Tạo booking
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setVehicle(vehicle);
        booking.setTimeSlot(slot);
        booking.setBookingDate(req.getBookingDate());
        booking.setNotes(req.getNotes());
        booking.setStatus("PENDING");
        booking.setBookingType("ADVANCE");
        booking.setQrCode(UUID.randomUUID().toString());
        bookingRepository.save(booking);

        // Gắn dịch vụ
        BigDecimal subtotal = BigDecimal.ZERO;
        List<BookingServiceItem> items = new ArrayList<>();

        for (Integer spId : req.getServicePriceIds()) {
            ServicePrice sp = servicePriceRepository.findById(spId)
                    .orElseThrow(() -> new RuntimeException("ServicePrice not found: " + spId));

            BookingServiceItem item = new BookingServiceItem();
            item.setBooking(booking);
            item.setServicePrice(sp);
            item.setPriceAtBooking(sp.getPrice());
            item.setDurationAtBooking(sp.getDurationMinutes());
            items.add(item);
            subtotal = subtotal.add(sp.getPrice());
        }
        bookingServiceItemRepository.saveAll(items);
        booking.setBookingServices(items);

        return toResponse(booking, subtotal);
    }

    /* ===== XEM BOOKING CỦA TÔI ===== */
    public List<BookingResponse> getMyBookings(String email) {
        Customer customer = customerRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return bookingRepository
                .findByCustomer_CustomerIdOrderByCreatedAtDesc(customer.getCustomerId())
                .stream()
                .map(b -> toResponse(b, calcSubtotal(b)))
                .collect(Collectors.toList());
    }

    /* ===== XEM 1 BOOKING ===== */
    public BookingResponse getById(Integer bookingId) {
        Booking b = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return toResponse(b, calcSubtotal(b));
    }

    /* ===== CẬP NHẬT TRẠNG THÁI (Staff/Manager) ===== */
    @Transactional
    public BookingResponse updateStatus(Integer bookingId, BookingStatusRequest req) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        validateTransition(booking.getStatus(), req.getStatus());
        booking.setStatus(req.getStatus());

        if (req.getCancelReason() != null) booking.setCancelReason(req.getCancelReason());

        if (req.getEmployeeId() != null) {
            Employee emp = employeeRepository.findById(req.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            booking.setEmployee(emp);
        }

        switch (req.getStatus()) {
            case "CHECKED_IN"  -> booking.setCheckinAt(LocalDateTime.now());
            case "IN_PROGRESS" -> booking.setStartAt(LocalDateTime.now());
            case "COMPLETED"   -> booking.setCompleteAt(LocalDateTime.now());
        }

        bookingRepository.save(booking);
        return toResponse(booking, calcSubtotal(booking));
    }

    /* ===== HỦY BOOKING (Customer) ===== */
    @Transactional
    public BookingResponse cancelBooking(String email, Integer bookingId, String reason) {
        Customer customer = customerRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            throw new RuntimeException("Access denied");
        }
        if (!List.of("PENDING", "CONFIRMED").contains(booking.getStatus())) {
            throw new RuntimeException("Cannot cancel booking in status: " + booking.getStatus());
        }

        booking.setStatus("CANCELLED");
        booking.setCancelReason(reason);
        bookingRepository.save(booking);
        return toResponse(booking, calcSubtotal(booking));
    }

    /* ===== XEM SLOT THEO NGÀY ===== */
    public List<Map<String, Object>> getAvailableSlots(LocalDate date) {
        return timeSlotRepository.findByIsActiveTrueOrderByStartTime().stream()
                .map(slot -> {
                    long booked = bookingRepository.countActiveBookingsInSlot(
                            slot.getSlotId(), date);
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("slotId", slot.getSlotId());
                    map.put("startTime", slot.getStartTime());
                    map.put("endTime", slot.getEndTime());
                    map.put("maxBookings", slot.getMaxBookings());
                    map.put("bookedCount", booked);
                    map.put("available", booked < slot.getMaxBookings());
                    return map;
                })
                .collect(Collectors.toList());
    }

    // ===== HELPERS =====

    private BigDecimal calcSubtotal(Booking b) {
        return b.getBookingServices().stream()
                .map(BookingServiceItem::getPriceAtBooking)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateTransition(String current, String next) {
        Map<String, List<String>> allowed = Map.of(
            "PENDING",     List.of("CONFIRMED", "CANCELLED"),
            "CONFIRMED",   List.of("CHECKED_IN", "CANCELLED", "NO_SHOW"),
            "CHECKED_IN",  List.of("IN_PROGRESS", "CANCELLED"),
            "IN_PROGRESS", List.of("COMPLETED"),
            "COMPLETED",   List.of(),
            "CANCELLED",   List.of(),
            "NO_SHOW",     List.of()
        );
        if (!allowed.getOrDefault(current, List.of()).contains(next)) {
            throw new RuntimeException("Invalid transition: " + current + " → " + next);
        }
    }

    private BookingResponse toResponse(Booking b, BigDecimal subtotal) {
        List<BookingResponse.ServiceDetail> details = b.getBookingServices().stream()
                .map(bs -> BookingResponse.ServiceDetail.builder()
                        .serviceName(bs.getServicePrice().getService().getName())
                        .category(bs.getServicePrice().getService().getCategory())
                        .price(bs.getPriceAtBooking())
                        .durationMinutes(bs.getDurationAtBooking())
                        .build())
                .collect(Collectors.toList());

        return BookingResponse.builder()
                .bookingId(b.getBookingId())
                .bookingDate(b.getBookingDate())
                .status(b.getStatus())
                .bookingType(b.getBookingType())
                .qrCode(b.getQrCode())
                .notes(b.getNotes())
                .customerName(b.getCustomer().getUser().getFullName())
                .customerPhone(b.getCustomer().getUser().getPhone())
                .licensePlate(b.getVehicle().getLicensePlate())
                .vehicleType(b.getVehicle().getVehicleType().getName())
                .brand(b.getVehicle().getBrand())
                .model(b.getVehicle().getModel())
                .startTime(b.getTimeSlot().getStartTime())
                .endTime(b.getTimeSlot().getEndTime())
                .services(details)
                .subtotal(subtotal)
                .employeeName(b.getEmployee() != null
                        ? b.getEmployee().getUser().getFullName() : null)
                .createdAt(b.getCreatedAt())
                .build();
    }
}