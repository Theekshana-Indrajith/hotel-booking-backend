package com.hotel.bookingsystem;

import com.hotel.bookingsystem.model.Room;
import com.hotel.bookingsystem.model.User;
import com.hotel.bookingsystem.repository.RoomRepository;
import com.hotel.bookingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Profile("dev") // Only run in development, not in production
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) {
        // Only add sample data if database is empty
        if (roomRepository.count() == 0) {
            System.out.println("📊 Initializing sample data for development...");

            Room room1 = new Room();
            room1.setRoomNumber("101");
            room1.setRoomType("SINGLE");
            room1.setPricePerNight(100.0);
            room1.setDescription("Cozy single room with city view");
            room1.setMaxGuests(1);
            room1.setIsAvailable(true);
            room1.setAmenities(Arrays.asList("WiFi", "TV", "AC", "Breakfast"));
            room1.setImageUrl("https://images.unsplash.com/photo-1631049307264-da0ec9d70304");

            Room room2 = new Room();
            room2.setRoomNumber("102");
            room2.setRoomType("DOUBLE");
            room2.setPricePerNight(150.0);
            room2.setDescription("Spacious double room with balcony");
            room2.setMaxGuests(2);
            room2.setIsAvailable(true);
            room2.setAmenities(Arrays.asList("WiFi", "TV", "AC", "Minibar", "Breakfast"));
            room2.setImageUrl("https://images.unsplash.com/photo-1566665797739-1674de7a421a");

            Room room3 = new Room();
            room3.setRoomNumber("201");
            room3.setRoomType("SUITE");
            room3.setPricePerNight(300.0);
            room3.setDescription("Luxury suite with jacuzzi");
            room3.setMaxGuests(4);
            room3.setIsAvailable(true);
            room3.setAmenities(Arrays.asList("WiFi", "TV", "AC", "Minibar", "Jacuzzi", "Room Service"));
            room3.setImageUrl("https://images.unsplash.com/photo-1590490360182-c33d57733427");

            roomRepository.save(room1);
            roomRepository.save(room2);
            roomRepository.save(room3);

            System.out.println("✅ Sample rooms added");
        }

        // Initialize users if there are none
        if (userRepository.count() == 0) {
            System.out.println("📊 Initializing sample users...");

            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@hotel.com");
            admin.setPassword("admin123");
            admin.setFullName("Hotel Administrator");
            admin.setPhone("+1234567890");
            admin.setRole("ADMIN");
            userRepository.save(admin);

            User demoUser = new User();
            demoUser.setUsername("john");
            demoUser.setEmail("john@example.com");
            demoUser.setPassword("password");
            demoUser.setFullName("John Doe");
            demoUser.setPhone("+1234567891");
            demoUser.setRole("USER");
            userRepository.save(demoUser);

            System.out.println("✅ Sample users added");
        }
    }
}