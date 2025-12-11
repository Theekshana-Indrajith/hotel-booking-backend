package com.hotel.bookingsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HotelBookingSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(HotelBookingSystemApplication.class, args);
        System.out.println("✅ Hotel Booking System Started Successfully!");
        System.out.println("🔗 Frontend: http://localhost:3000");
        System.out.println("🔗 Backend API: http://localhost:8080");
        System.out.println("📊 Database: MySQL");
    }
}