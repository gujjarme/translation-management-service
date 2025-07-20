package com.digitaltolk.translation_service.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import com.digitaltolk.translation_service.service.UserService;
import com.digitaltolk.translation_service.dao.User;
import com.digitaltolk.translation_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserService userService;

    @Test
    void createUser_shouldEncodePasswordAndSave() {
        // Setup
        when(userRepository.findByUsername("test")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("rawPass")).thenReturn("encodedPass");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Execute
        User result = userService.createUser("test", "rawPass");

        // Verify
        assertEquals("test", result.getUsername());
        assertEquals("encodedPass", result.getPassword());
        verify(userRepository).save(result);
    }

    @Test
    void createUser_shouldRejectDuplicateUsername() {
        // Setup
        when(userRepository.findByUsername("existing"))
                .thenReturn(Optional.of(new User("existing", "pass")));

        // Execute & Verify
        assertThrows(IllegalArgumentException.class, () ->
                userService.createUser("existing", "password")
        );
    }
}