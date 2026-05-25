package com.autowashpro.service;

import com.autowashpro.entity.Booking;
import com.autowashpro.entity.Transaction;
import com.autowashpro.repository.BookingRepository;
import com.autowashpro.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;
import vn.payos.model.webhooks.WebhookData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayOSService {

    private final PayOS payOS;
    private final TransactionRepository transactionRepository;
    private final BookingRepository bookingRepository;

    @Value("${payos.return-url}")
    private String returnUrl;

    @Value("${payos.cancel-url}")
    private String cancelUrl;

    /* ===== TẠO PAYMENT LINK ===== */
    public String createPaymentLink(Transaction transaction) throws Exception {

        Long orderCode = transaction.getTransactionId().longValue();  // fix 1
        Long amount = transaction.getFinalAmount().longValue();        // fix 2
        String description = "AutoWash#" + transaction.getBooking().getBookingId();

        // fix 3 — dùng ArrayList thay vì stream để tránh type inference issue
        List<PaymentLinkItem> items = new ArrayList<>();
        for (var bs : transaction.getBooking().getBookingServices()) {
            items.add(PaymentLinkItem.builder()
                    .name(bs.getServicePrice().getService().getName())
                    .quantity(1)
                    .price(bs.getPriceAtBooking().longValue())
                    .build());
        }

        CreatePaymentLinkRequest request = CreatePaymentLinkRequest.builder()
                .orderCode(orderCode)
                .amount(amount)
                .description(description)
                .items(items)
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .build();

        CreatePaymentLinkResponse response = payOS.paymentRequests().create(request);
        return response.getCheckoutUrl();
    }

    /* ===== HỦY PAYMENT LINK ===== */
    public void cancelPaymentLink(Integer transactionId) throws Exception {
        payOS.paymentRequests().cancel(String.valueOf(transactionId), null);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        transaction.setPaymentStatus("CANCELLED");
        transactionRepository.save(transaction);
    }

    /* ===== XỬ LÝ WEBHOOK ===== */
    public void handleWebhook(String webhookBody) throws Exception {
        // fix 4 — dùng đúng method name của version 2.0.1
        WebhookData webhookData = payOS.webhooks().verify(webhookBody);

        if ("00".equals(webhookData.getCode())) {
            long orderCode = webhookData.getOrderCode();

            Transaction transaction = transactionRepository.findById((int) orderCode)
                    .orElseThrow(() -> new RuntimeException("Transaction not found"));

            transaction.setPaymentStatus("PAID");
            transaction.setPaidAt(LocalDateTime.now());
            transactionRepository.save(transaction);

            Booking booking = transaction.getBooking();
            booking.setStatus("COMPLETED");
            bookingRepository.save(booking);
        }
    }
}