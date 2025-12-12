package com.hotel.bookingsystem.controller;

import com.hotel.bookingsystem.model.Room;
import com.hotel.bookingsystem.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "${spring.mvc.cors.allowed-origins}")
public class RoomController {

    @Autowired
    private RoomService roomService;

    // Get all rooms
    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    // Get available rooms
    @GetMapping("/available")
    public ResponseEntity<List<Room>> getAvailableRooms() {
        List<Room> rooms = roomService.getAvailableRooms();
        return ResponseEntity.ok(rooms);
    }

    // Get room by ID
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable UUID id) {
        return roomService.getRoomById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create new room
    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        Room savedRoom = roomService.saveRoom(room);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoom);
    }

    // Update room
    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable UUID id, @RequestBody Room roomDetails) {
        return roomService.getRoomById(id)
                .map(existingRoom -> {
                    existingRoom.setRoomNumber(roomDetails.getRoomNumber());
                    existingRoom.setRoomType(roomDetails.getRoomType());
                    existingRoom.setPricePerNight(roomDetails.getPricePerNight());
                    existingRoom.setDescription(roomDetails.getDescription());
                    existingRoom.setMaxGuests(roomDetails.getMaxGuests());
                    existingRoom.setIsAvailable(roomDetails.getIsAvailable());
                    existingRoom.setAmenities(roomDetails.getAmenities());
                    existingRoom.setImageUrl(roomDetails.getImageUrl());
                    Room updatedRoom = roomService.saveRoom(existingRoom);
                    return ResponseEntity.ok(updatedRoom);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete room
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable UUID id) {
        if (roomService.getRoomById(id).isPresent()) {
            roomService.deleteRoom(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Search rooms
    @GetMapping("/search")
    public ResponseEntity<List<Room>> searchRooms(
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer minGuests) {

        List<Room> rooms = roomService.searchRooms(roomType, maxPrice, minGuests);
        return ResponseEntity.ok(rooms);
    }

    // Check availability between dates
    @GetMapping("/availability")
    public ResponseEntity<List<Room>> getAvailableRoomsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {

        List<Room> rooms = roomService.getAvailableRoomsBetweenDates(checkIn, checkOut);
        return ResponseEntity.ok(rooms);
    }

    // Get rooms by type
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Room>> getRoomsByType(@PathVariable String type) {
        List<Room> rooms = roomService.getRoomsByType(type);
        return ResponseEntity.ok(rooms);
    }
}