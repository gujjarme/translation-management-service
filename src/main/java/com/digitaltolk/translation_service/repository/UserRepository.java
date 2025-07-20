package com.digitaltolk.translation_service.repository;

import com.digitaltolk.translation_service.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    public Optional<User> findByUsername(String username);
}
