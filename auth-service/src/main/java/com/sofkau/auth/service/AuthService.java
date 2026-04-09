package com.sofkau.auth.service;

import com.sofkau.auth.dto.LoginRequest;
import com.sofkau.auth.dto.LoginResponse;
import com.sofkau.auth.dto.RegisterRequest;
import com.sofkau.auth.entity.Role;
import com.sofkau.auth.entity.User;
import com.sofkau.auth.exception.InvalidCredentialsException;
import com.sofkau.auth.exception.UserAlreadyExistsException;
import com.sofkau.auth.repository.RoleRepository;
import com.sofkau.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }

    private void validateUserNotExists(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException("Username already exists");
        }
    }

    private User createUser(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role USER not found"));
        user.addRole(role);
        return user;
    }

    private LoginResponse buildLoginResponse(User user) {
        String role = user.getRoles().stream()
                .findFirst()
                .map(Role::getName)
                .orElse("USER");

        String token = jwtService.generateToken(
                user.getUsername(),
                role,
                user.getId()
        );
        return new LoginResponse(token, user.getId(), user.getUsername(), role);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = findUserByUsername(request.username());
        validatePassword(request.password(), user.getPassword());
        return buildLoginResponse(user);
    }

    public LoginResponse register(RegisterRequest request) {
        validateUserNotExists(request.username());
        User user = createUser(request);
        User savedUser = userRepository.save(user);
        return buildLoginResponse(savedUser);
    }
}
