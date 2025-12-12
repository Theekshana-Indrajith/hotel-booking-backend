package com.hotel.bookingsystem.repository;

import com.hotel.bookingsystem.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    // Find bookings by guest email
    List<Booking> findByGuestEmail(String guestEmail);

    // Find bookings by user ID
    List<Booking> findByUserId(UUID userId);

    // Find bookings by status
    List<Booking> findByStatus(String status);

    // Find bookings within a date range
    List<Booking> findByCheckInDateBetween(LocalDate startDate, LocalDate endDate);

    // Find bookings for a specific room
    List<Booking> findByRoomId(UUID roomId);

    // Check if room is booked between dates
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.room.id = :roomId " +
            "AND b.status != 'CANCELLED' " +
            "AND ((b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn))")
    boolean isRoomBookedBetweenDates(
            @Param("roomId") UUID roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut);
}