package com.hotel.bookingsystem.controller;

import com.hotel.bookingsystem.model.Booking;
import com.hotel.bookingsystem.service.BookingService;
import com.hotel.bookingsystem.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:3000")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RoomService roomService;

    // Get all bookings
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    // Get booking by ID
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get bookings by email
    @GetMapping("/email/{email}")
    public ResponseEntity<List<Booking>> getBookingsByEmail(@PathVariable String email) {
        List<Booking> bookings = bookingService.getBookingsByEmail(email);
        return ResponseEntity.ok(bookings);
    }

    // Get bookings by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUserId(@PathVariable Long userId) {
        List<Booking> bookings = bookingService.getBookingsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    // Create new booking - UPDATED to accept userId
    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody Map<String, Object> bookingRequest) {
        try {
            Long userId = Long.valueOf(bookingRequest.get("userId").toString());
            Booking booking = new Booking();

            // Map booking data from request
            booking.setGuestName((String) bookingRequest.get("guestName"));
            booking.setGuestEmail((String) bookingRequest.get("guestEmail"));
            booking.setGuestPhone((String) bookingRequest.get("guestPhone"));
            booking.setCheckInDate(LocalDate.parse(bookingRequest.get("checkInDate").toString()));
            booking.setCheckOutDate(LocalDate.parse(bookingRequest.get("checkOutDate").toString()));
            booking.setNumberOfGuests(Integer.valueOf(bookingRequest.get("numberOfGuests").toString()));
            booking.setSpecialRequests((String) bookingRequest.get("specialRequests"));

            // Create room object with ID
            Map<String, Object> roomData = (Map<String, Object>) bookingRequest.get("room");
            Long roomId = Long.valueOf(roomData.get("id").toString());
            com.hotel.bookingsystem.model.Room room = new com.hotel.bookingsystem.model.Room();
            room.setId(roomId);
            booking.setRoom(room);

            // Check if room is available for the dates
            boolean isAvailable = bookingService.isRoomAvailable(
                    roomId,
                    booking.getCheckInDate(),
                    booking.getCheckOutDate()
            );

            if (!isAvailable) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }

            Booking createdBooking = bookingService.createBooking(userId, booking);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Update booking
    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @RequestBody Booking bookingDetails) {
        Booking updatedBooking = bookingService.updateBooking(id, bookingDetails);
        if (updatedBooking != null) {
            return ResponseEntity.ok(updatedBooking);
        }
        return ResponseEntity.notFound().build();
    }

    // Cancel booking
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        boolean cancelled = bookingService.cancelBooking(id);
        if (cancelled) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Delete booking
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        if (bookingService.getBookingById(id).isPresent()) {
            bookingService.deleteBooking(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Check room availability
    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> checkRoomAvailability(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {

        boolean isAvailable = bookingService.isRoomAvailable(roomId, checkIn, checkOut);
        return ResponseEntity.ok(isAvailable);
    }
}