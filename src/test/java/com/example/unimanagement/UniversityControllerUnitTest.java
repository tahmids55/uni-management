package com.example.unimanagement;

import com.example.unimanagement.controller.UniversityController;
import com.example.unimanagement.entity.AppUser;
import com.example.unimanagement.entity.Role;
import com.example.unimanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UniversityControllerUnitTest {

    @Mock
    private UserRepository repo;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UniversityController controller;

    @Test
    void register_ShouldBlockAdminRegistration() {
        AppUser hacker = new AppUser();
        hacker.setUsername("evil_admin");
        hacker.setPassword("123");
        hacker.setRole(Role.ROLE_ADMIN);

        ResponseEntity<?> response = controller.register(hacker);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Cannot register as ADMIN!", response.getBody());
        verify(repo, never()).save(any());
    }

    @Test
    void register_ShouldCreateStudentSuccessfully() {
        AppUser student = new AppUser();
        student.setUsername("taki");
        student.setPassword("rawPass");
        student.setFullName("Md. Taki Tahmid Saad");
        student.setDepartment("CSE");
        student.setRole(Role.ROLE_STUDENT);

        when(repo.findByUsername("taki")).thenReturn(Optional.empty());
        when(encoder.encode("rawPass")).thenReturn("hashedPass");
        when(repo.save(any(AppUser.class))).thenReturn(student);

        ResponseEntity<?> response = controller.register(student);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("hashedPass", student.getPassword());
        verify(repo, times(1)).save(student);
    }
}