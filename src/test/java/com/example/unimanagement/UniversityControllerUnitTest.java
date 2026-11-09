package com.example.unimanagement;

import com.example.unimanagement.entity.AppUser;
import com.example.unimanagement.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class UniversityUnitTest {

    // 1. Check Student creation
    @Test
    void testUserCreation() {
        AppUser user = new AppUser();
        user.setUsername("tahmid");
        user.setFullName("Md. Taki Tahmid Saad");
        user.setDepartment("CSE");
        user.setRole(Role.ROLE_STUDENT);

        assertEquals("tahmid", user.getUsername());
        assertEquals("CSE", user.getDepartment());
        assertEquals(Role.ROLE_STUDENT, user.getRole());
    }

    // 2. Check correct password matches encrypted hash
    @Test
    void testPasswordEncryption() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashed = encoder.encode("12345");

        assertTrue(encoder.matches("12345", hashed));
    }

    // 3. Check wrong password fails to match
    @Test
    void testWrongPasswordFails() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashed = encoder.encode("correctPass");

        // Verify that a wrong password returns false
        assertFalse(encoder.matches("wrongPass", hashed));
    }

    // 4. Check updating user profile fields (Edit simulation)
    @Test
    void testUpdateUserFields() {
        AppUser user = new AppUser();
        user.setFullName("Old Name");
        user.setDepartment("EEE");

        // Update the values
        user.setFullName("New Name");
        user.setDepartment("CSE");

        assertEquals("New Name", user.getFullName());
        assertEquals("CSE", user.getDepartment());
    }

    // 5. Check Teacher role assignment
    @Test
    void testTeacherRoleAssignment() {
        AppUser teacher = new AppUser();
        teacher.setUsername("teacher1");
        teacher.setRole(Role.ROLE_TEACHER);

        assertEquals(Role.ROLE_TEACHER, teacher.getRole());
        assertNotEquals(Role.ROLE_STUDENT, teacher.getRole());
    }

    // 6. Check encrypted password is not stored as plain text
    @Test
    void testEncryptedPasswordIsDifferentFromRaw() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "mySecretPassword";
        String hashedPassword = encoder.encode(rawPassword);

        // Verify the hash is not equal to the plain text password
        assertNotEquals(rawPassword, hashedPassword);
    }
}