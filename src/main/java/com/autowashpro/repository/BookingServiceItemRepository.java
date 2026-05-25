package com.autowashpro.repository;

import com.autowashpro.entity.BookingServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingServiceItemRepository extends JpaRepository<BookingServiceItem, Integer> {
}