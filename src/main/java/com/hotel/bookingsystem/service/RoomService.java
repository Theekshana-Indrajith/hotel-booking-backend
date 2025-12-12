package com.hotel.bookingsystem.service;

import com.hotel.bookingsystem.model.Room;
import com.hotel.bookingsystem.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<Room> getAvailableRooms() {
        return roomRepository.findByIsAvailableTrue();
    }

    public Optional<Room> getRoomById(UUID id) {
        return roomRepository.findById(id);
    }

    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }

    public void deleteRoom(UUID id) {
        roomRepository.deleteById(id);
    }

    public List<Room> getRoomsByType(String type) {
        return roomRepository.findByRoomTypeAndIsAvailableTrue(type);
    }

    public List<Room> getAvailableRoomsBetweenDates(LocalDate checkIn, LocalDate checkOut) {
        return roomRepository.findAvailableRoomsBetweenDates(checkIn, checkOut);
    }

    public List<Room> searchRooms(String roomType, Double maxPrice, Integer minGuests) {
        return roomRepository.searchRooms(roomType, maxPrice, minGuests);
    }

    public boolean existsById(UUID id) {
        return roomRepository.existsById(id);
    }
}