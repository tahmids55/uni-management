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

    // 1. Test valid student registration saves to PostgreSQL
    @Test
    void testRegisterStudentSavesToDatabase() {
        AppUser student = new AppUser();
        student.setUsername("test_student");
        student.setPassword("1234");
        student.setFullName("Test Student");
        student.setDepartment("CSE");
        student.setRole(Role.ROLE_STUDENT);

        ResponseEntity<?> response = controller.register(student);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(repo.findByUsername("test_student").isPresent());
    }

    // 2. Test registering as ADMIN is blocked
    @Test
    void testCannotRegisterAsAdmin() {
        AppUser fakeAdmin = new AppUser();
        fakeAdmin.setUsername("hacker");
        fakeAdmin.setPassword("1234");
        fakeAdmin.setRole(Role.ROLE_ADMIN);

        ResponseEntity<?> response = controller.register(fakeAdmin);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    // 3. NEW: Test duplicate username is rejected by database check
    @Test
    void testDuplicateUsernameIsRejected() {
        // First user registers with username "taki"
        AppUser firstUser = new AppUser();
        firstUser.setUsername("taki");
        firstUser.setPassword("1234");
        firstUser.setRole(Role.ROLE_STUDENT);
        controller.register(firstUser);

        // Second user tries to register with the exact same username "taki"
        AppUser duplicateUser = new AppUser();
        duplicateUser.setUsername("taki");
        duplicateUser.setPassword("5678");
        duplicateUser.setRole(Role.ROLE_TEACHER);

        ResponseEntity<?> response = controller.register(duplicateUser);

        // Verify the controller checks the database and returns 400 BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username already exists!", response.getBody());
    }
}