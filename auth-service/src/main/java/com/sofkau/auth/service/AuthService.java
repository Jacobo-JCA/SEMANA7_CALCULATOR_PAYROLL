package com.sofkau.auth.service;

import com.sofkau.auth.dto.LoginRequest;
import com.sofkau.auth.dto.LoginResponse;
import com.sofkau.auth.dto.RegisterRequest;
import com.sofkau.auth.entity.Role;
import com.sofkau.auth.entity.User;
import com.sofkau.auth.repository.RoleRepository;
import com.sofkau.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String role = user.getRoles().stream()
                .findFirst()
                .map(Role::getName)
                .orElse("USER");

        String token = jwtService.generateToken(user.getUsername(), role, user.getId());

        return new LoginResponse(token, user.getId(), user.getUsername(), role);
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role role = roleRepository.findByName(request.getRole())
                .orElseGet(() -> {
                    Role newRole = new Role(request.getRole());
                    return roleRepository.save(newRole);
                });

        user.addRole(role);
        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser.getUsername(), role.getName(), savedUser.getId());

        return new LoginResponse(token, savedUser.getId(), savedUser.getUsername(), role.getName());
    }
}
