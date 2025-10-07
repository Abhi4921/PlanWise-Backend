package com.srmist.academia.service;

import com.srmist.academia.model.User;
import com.srmist.academia.repository.UserRepository;
import com.srmist.academia.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AcademiaService academiaService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil = new JwtUtil();

    @Transactional
    public void registerUser(String email, String password) throws Exception {
        // Check if user exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("User already exists with this email.");
        }

        // Step 1: Login to Academia and fetch session
        var cookies = academiaService.loginToAcademia(email, password);
        // Step 2: Hash password
        String passwordHash = encoder.encode(password);

        User user = User.builder()
                .email(email)
                .passwordHash(passwordHash)
                .cookies(cookies)
                .build();

        userRepository.save(user);
    }

    public String loginUser(String email, String password) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Step 1: Verify password
        if (!encoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        userRepository.save(user);

        return jwtUtil.generateToken(user.getEmail(), 3600000);
    }
}
