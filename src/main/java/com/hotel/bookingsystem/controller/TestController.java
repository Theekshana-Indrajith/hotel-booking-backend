package com.hotel.bookingsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/db-connection")
    public String testConnection() {
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT version()");
            return "✅ Database connected successfully! PostgreSQL version: " + result.get(0).get("version");
        } catch (Exception e) {
            return "❌ Database connection failed: " + e.getMessage();
        }
    }

    @GetMapping("/ping")
    public String ping() {
        return "✅ Backend is running!";
    }

    @GetMapping("/tables")
    public List<Map<String, Object>> listTables() {
        return jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'"
        );
    }
}