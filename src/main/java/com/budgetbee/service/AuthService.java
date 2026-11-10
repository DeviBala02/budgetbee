package com.budgetbee.service;

import com.budgetbee.dto.Dto.*;
import com.budgetbee.model.*;
import com.budgetbee.repo.*;
import com.budgetbee.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AuthService {
    private static final List<String> DEFAULT_CATEGORIES =
            List.of("Food", "Transport", "Rent", "Shopping", "Bills", "Entertainment", "Health", "Other");

    private final UserRepository users;
    private final CategoryRepository categories;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository users, CategoryRepository categories, PasswordEncoder encoder, JwtService jwt) {
        this.users = users; this.categories = categories; this.encoder = encoder; this.jwt = jwt;
    }

    @Transactional
    public AuthRes register(RegisterReq r) {
        String email = r.email().trim().toLowerCase();
        if (users.existsByEmail(email)) throw new IllegalArgumentException("Email already registered");
        AppUser u = new AppUser();
        u.setName(r.name().trim()); u.setEmail(email); u.setPasswordHash(encoder.encode(r.password()));
        users.save(u);
        for (String n : DEFAULT_CATEGORIES) {
            Category c = new Category(); c.setName(n); c.setUser(u); categories.save(c);
        }
        return new AuthRes(jwt.generate(email), u.getName());
    }

    public AuthRes login(LoginReq r) {
        AppUser u = users.findByEmail(r.email().trim().toLowerCase())
                .filter(x -> encoder.matches(r.password(), x.getPasswordHash()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        return new AuthRes(jwt.generate(u.getEmail()), u.getName());
    }
}
