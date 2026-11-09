package com.example.unimanagement.repository;

import com.example.unimanagement.entity.AppUser;
import com.example.unimanagement.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);

    List<AppUser> findByRole(Role role);
}
