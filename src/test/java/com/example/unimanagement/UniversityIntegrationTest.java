package com.example.unimanagement;

import com.example.unimanagement.controller.UniversityController;
import com.example.unimanagement.entity.AppUser;
import com.example.unimanagement.entity.Role;
import com.example.unimanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional // Automatically cleans up the database after each test
class UniversityIntegrationTest {

    @Autowired
    private UniversityController controller;

    @Autowired
    private UserRepository repo;

    @Test
    void testRegisterStudentSavesToDatabase() {
        // 1. Create a new student
        AppUser student = new AppUser();
        student.setUsername("test_student");
        student.setPassword("1234");
        student.setFullName("Test Student");
        student.setDepartment("CSE");
        student.setRole(Role.ROLE_STUDENT);

        // 2. Call the controller's register method
        ResponseEntity<?> response = controller.register(student);

        // 3. Check if HTTP status is 201 CREATED and user exists in PostgreSQL
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(repo.findByUsername("test_student").isPresent());
    }

    @Test
    void testCannotRegisterAsAdmin() {
        // 1. Try to create an ADMIN user
        AppUser fakeAdmin = new AppUser();
        fakeAdmin.setUsername("hacker");
        fakeAdmin.setPassword("1234");
        fakeAdmin.setRole(Role.ROLE_ADMIN);

        // 2. Call the controller's register method
        ResponseEntity<?> response = controller.register(fakeAdmin);

        // 3. Verify that our controller blocks it with 403 FORBIDDEN
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}