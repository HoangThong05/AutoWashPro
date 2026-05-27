package com.autowashpro.service;

import com.autowashpro.dto.request.ContactRequest;
import com.autowashpro.dto.response.ContactMessageResponse;
import com.autowashpro.dto.response.ContactResponse;
import com.autowashpro.entity.Contact;
import com.autowashpro.entity.ContactMessage;
import com.autowashpro.entity.User;
import com.autowashpro.repository.ContactMessageRepository;
import com.autowashpro.repository.ContactRepository;
import com.autowashpro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final ContactMessageRepository contactMessageRepository;
    private final UserRepository userRepository;

    /* ===== GỬI TIN NHẮN (public) ===== */
    @Transactional
    public ContactResponse sendMessage(ContactRequest req) {
        Contact contact = new Contact();
        contact.setFullName(req.getFullName());
        contact.setEmail(req.getEmail());
        contact.setPhone(req.getPhone());
        contact.setStatus("UNREAD");
        contactRepository.save(contact);

        ContactMessage message = new ContactMessage();
        message.setContact(contact);
        message.setSenderType("CUSTOMER");
        message.setMessage(req.getMessage());
        contactMessageRepository.save(message);

        return toResponse(contact);
    }

    /* ===== XEM TẤT CẢ (Staff/Manager) ===== */
    public List<ContactResponse> getAll(String status) {
        if (status != null) {
            return contactRepository.findByStatusOrderByCreatedAtDesc(status)
                    .stream().map(this::toResponse).collect(Collectors.toList());
        }
        return contactRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    /* ===== XEM CHI TIẾT (Staff/Manager) ===== */
    @Transactional
    public ContactResponse getById(Integer id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));
        if ("UNREAD".equals(contact.getStatus())) {
            contact.setStatus("READ");
            contactRepository.save(contact);
        }
        return toResponse(contact);
    }

    /* ===== KHÁCH GỬI THÊM TIN ===== */
    @Transactional
    public ContactMessageResponse customerReply(
            Integer contactId, String message, String email) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        if (!contact.getEmail().equals(email)) {
            throw new RuntimeException("Không có quyền truy cập");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ContactMessage msg = new ContactMessage();
        msg.setContact(contact);
        msg.setSenderType("CUSTOMER");
        msg.setMessage(message);
        msg.setSender(user);
        contactMessageRepository.save(msg);

        if ("RESOLVED".equals(contact.getStatus())) {
            contact.setStatus("UNREAD");
            contactRepository.save(contact);
        }

        return toMessageResponse(msg);
    }

    /* ===== STAFF REPLY ===== */
    @Transactional
    public ContactMessageResponse staffReply(
            Integer contactId, String message, String staffEmail) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        ContactMessage msg = new ContactMessage();
        msg.setContact(contact);
        msg.setSenderType("STAFF");
        msg.setMessage(message);
        msg.setSender(staff);
        contactMessageRepository.save(msg);

        contact.setStatus("RESOLVED");
        contact.setHandledBy(staff);
        contact.setHandledAt(LocalDateTime.now());
        contactRepository.save(contact);

        return toMessageResponse(msg);
    }

    /* ===== KHÁCH XEM TIN + REPLY ===== */
    public List<ContactResponse> getMyContacts(String email) {
        return contactRepository.findByEmailOrderByCreatedAtDesc(email)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    /* ===== XEM MESSAGES ===== */
    public List<ContactMessageResponse> getMessages(Integer contactId) {
        return contactMessageRepository
                .findByContact_ContactIdOrderByCreatedAtAsc(contactId)
                .stream().map(this::toMessageResponse).collect(Collectors.toList());
    }

    // ===== HELPERS =====
    private ContactResponse toResponse(Contact c) {
        List<ContactMessageResponse> messages = contactMessageRepository
                .findByContact_ContactIdOrderByCreatedAtAsc(c.getContactId())
                .stream().map(this::toMessageResponse).collect(Collectors.toList());

        return ContactResponse.builder()
                .contactId(c.getContactId())
                .fullName(c.getFullName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .status(c.getStatus())
                .handledBy(c.getHandledBy() != null
                        ? c.getHandledBy().getFullName() : null)
                .handledAt(c.getHandledAt())
                .createdAt(c.getCreatedAt())
                .messages(messages)
                .build();
    }

    private ContactMessageResponse toMessageResponse(ContactMessage m) {
        return ContactMessageResponse.builder()
                .messageId(m.getMessageId())
                .contactId(m.getContact().getContactId())
                .senderType(m.getSenderType())
                .senderName(m.getSender() != null
                        ? m.getSender().getFullName() : "Khách")
                .message(m.getMessage())
                .createdAt(m.getCreatedAt())
                .build();
    }
}