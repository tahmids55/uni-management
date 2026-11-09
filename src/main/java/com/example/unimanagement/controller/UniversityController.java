package com.example.unimanagement.controller;

import com.example.unimanagement.entity.AppUser;
import com.example.unimanagement.entity.Role;
import com.example.unimanagement.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UniversityController {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UniversityController(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    // 1. CREATE: Register a Student or Teacher account
    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody AppUser user) {
        if (user.getRole() == Role.ROLE_ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot register as ADMIN!");
        }
        if (repo.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists!");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(user));
    }

    // 2. READ: View Own Profile
    @GetMapping("/profile/me")
    public ResponseEntity<AppUser> getMyProfile(Authentication auth) {
        return repo.findByUsername(auth.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. READ: Teacher & Admin see all Students
    @GetMapping("/students")
    public List<AppUser> getAllStudents() {
        return repo.findByRole(Role.ROLE_STUDENT);
    }

    // 4. READ: Admin sees all users
    @GetMapping("/admin/users")
    public List<AppUser> getAllUsers() {
        return repo.findAll();
    }

    // 5. UPDATE: Edit User Profile (Full Name, Department, and Optional New Password)
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody AppUser updatedData, Authentication auth) {
        AppUser caller = repo.findByUsername(auth.getName()).orElseThrow();
        AppUser target = repo.findById(id).orElse(null);

        if (target == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        if (!canModify(caller, target)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You do not have permission to edit this user!");
        }

        target.setFullName(updatedData.getFullName());
        target.setDepartment(updatedData.getDepartment());

        // Only update password if the user typed a new one
        if (updatedData.getPassword() != null && !updatedData.getPassword().isBlank()) {
            target.setPassword(encoder.encode(updatedData.getPassword()));
        }

        return ResponseEntity.ok(repo.save(target));
    }

    // 6. DELETE: Delete User Account
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication auth) {
        AppUser caller = repo.findByUsername(auth.getName()).orElseThrow();
        AppUser target = repo.findById(id).orElse(null);

        if (target == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Prevent Admin from accidentally deleting the main admin account
        if (target.getRole() == Role.ROLE_ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot delete an ADMIN account!");
        }

        if (!canModify(caller, target)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You do not have permission to delete this user!");
        }

        repo.delete(target);
        return ResponseEntity.ok("User deleted successfully");
    }

    // Authorization Matrix Helper for Edit & Delete
    private boolean canModify(AppUser caller, AppUser target) {
        // Admin can modify any Teacher or Student (and their own profile)
        if (caller.getRole() == Role.ROLE_ADMIN) {
            return target.getRole() != Role.ROLE_ADMIN || caller.getId().equals(target.getId());
        }
        // Teacher can modify their own profile OR any Student
        if (caller.getRole() == Role.ROLE_TEACHER) {
            return caller.getId().equals(target.getId()) || target.getRole() == Role.ROLE_STUDENT;
        }
        // Student can ONLY modify their own profile
        if (caller.getRole() == Role.ROLE_STUDENT) {
            return caller.getId().equals(target.getId());
        }
        return false;
    }
}