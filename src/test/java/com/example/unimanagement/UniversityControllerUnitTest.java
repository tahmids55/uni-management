package com.example.unimanagement;

import com.example.unimanagement.entity.AppUser;
import com.example.unimanagement.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UniversityUnitTest {

    @Test
    void testUserCreation() {
        // 1. Create a user object in memory
        AppUser user = new AppUser();
        user.setUsername("tahmid");
        user.setFullName("Md. Taki Tahmid Saad");
        user.setDepartment("CSE");
        user.setRole(Role.ROLE_STUDENT);

        // 2. Check if the getters return the exact values we set
        assertEquals("tahmid", user.getUsername());
        assertEquals("CSE", user.getDepartment());
        assertEquals(Role.ROLE_STUDENT, user.getRole());
    }

    @Test
    void testPasswordEncryption() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashed = encoder.encode("12345");

        // Verify that BCrypt matches the raw password with the encrypted hash
        assertTrue(encoder.matches("12345", hashed));
    }
}