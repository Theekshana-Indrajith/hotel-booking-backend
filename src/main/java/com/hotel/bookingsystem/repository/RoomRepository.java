package com.hotel.bookingsystem.repository;

import com.hotel.bookingsystem.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
    // Find all available rooms
    List<Room> findByIsAvailableTrue();

    // Find rooms by type
    List<Room> findByRoomTypeAndIsAvailableTrue(String roomType);

    // Find rooms by max guests capacity
    List<Room> findByMaxGuestsGreaterThanEqualAndIsAvailableTrue(Integer guests);

    // Find available rooms that are not booked between dates
    @Query("SELECT r FROM Room r WHERE r.isAvailable = true AND r.id NOT IN " +
            "(SELECT b.room.id FROM Booking b WHERE " +
            "(b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn) AND " +
            "b.status != 'CANCELLED')")
    List<Room> findAvailableRoomsBetweenDates(
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut);

    // Search rooms with multiple criteria
    @Query("SELECT r FROM Room r WHERE r.isAvailable = true " +
            "AND (:roomType IS NULL OR r.roomType = :roomType) " +
            "AND (:maxPrice IS NULL OR r.pricePerNight <= :maxPrice) " +
            "AND (:minGuests IS NULL OR r.maxGuests >= :minGuests)")
    List<Room> searchRooms(
            @Param("roomType") String roomType,
            @Param("maxPrice") Double maxPrice,
            @Param("minGuests") Integer minGuests);
}