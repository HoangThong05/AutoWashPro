package com.autowashpro.repository;

import com.autowashpro.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {
    List<Contact> findAllByOrderByCreatedAtDesc();
    List<Contact> findByStatusOrderByCreatedAtDesc(String status);
    List<Contact> findByEmailOrderByCreatedAtDesc(String email);
}