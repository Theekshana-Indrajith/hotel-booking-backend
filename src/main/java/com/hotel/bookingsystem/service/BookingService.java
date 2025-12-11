package com.hotel.bookingsystem.service;

import com.hotel.bookingsystem.model.Booking;
import com.hotel.bookingsystem.model.Room;
import com.hotel.bookingsystem.model.User;
import com.hotel.bookingsystem.repository.BookingRepository;
import com.hotel.bookingsystem.repository.RoomRepository;
import com.hotel.bookingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;  // ADD THIS

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public List<Booking> getBookingsByEmail(String email) {
        return bookingRepository.findByGuestEmail(email);
    }

    // ADD THIS METHOD - Get bookings by user ID
    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    // UPDATED METHOD - Now accepts userId
    public Booking createBooking(Long userId, Booking booking) {
        // Fetch the user from database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Fetch the room from database
        Room room = roomRepository.findById(booking.getRoom().getId())
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + booking.getRoom().getId()));

        // Set user and room on booking
        booking.setUser(user);
        booking.setRoom(room);

        // Calculate total price
        long nights = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        double totalPrice = nights * room.getPricePerNight();
        booking.setTotalPrice(totalPrice);

        // Set default status if not provided
        if (booking.getStatus() == null) {
            booking.setStatus("CONFIRMED");
        }

        return bookingRepository.save(booking);
    }

    // UPDATED METHOD - Add user fetch
    public Booking updateBooking(Long id, Booking bookingDetails) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setGuestName(bookingDetails.getGuestName());
            booking.setGuestEmail(bookingDetails.getGuestEmail());
            booking.setGuestPhone(bookingDetails.getGuestPhone());
            booking.setCheckInDate(bookingDetails.getCheckInDate());
            booking.setCheckOutDate(bookingDetails.getCheckOutDate());
            booking.setNumberOfGuests(bookingDetails.getNumberOfGuests());
            booking.setSpecialRequests(bookingDetails.getSpecialRequests());

            // Get room ID from bookingDetails
            Long roomId = bookingDetails.getRoom().getId();

            // Recalculate total price if room or dates changed
            if (!booking.getRoom().getId().equals(roomId) ||
                    !booking.getCheckInDate().equals(bookingDetails.getCheckInDate()) ||
                    !booking.getCheckOutDate().equals(bookingDetails.getCheckOutDate())) {

                // Fetch the room from database
                Room room = roomRepository.findById(roomId)
                        .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));

                long nights = ChronoUnit.DAYS.between(
                        bookingDetails.getCheckInDate(),
                        bookingDetails.getCheckOutDate());
                double totalPrice = nights * room.getPricePerNight();
                booking.setTotalPrice(totalPrice);
                booking.setRoom(room);
            }

            return bookingRepository.save(booking);
        }
        return null;
    }

    public boolean cancelBooking(Long id) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setStatus("CANCELLED");
            bookingRepository.save(booking);
            return true;
        }
        return false;
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    public boolean isRoomAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        // Check if room exists
        if (!roomRepository.existsById(roomId)) {
            throw new RuntimeException("Room not found with id: " + roomId);
        }
        return !bookingRepository.isRoomBookedBetweenDates(roomId, checkIn, checkOut);
    }
}