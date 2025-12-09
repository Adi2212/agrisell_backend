package com.agrisell.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.agrisell.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import com.agrisell.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);
}
