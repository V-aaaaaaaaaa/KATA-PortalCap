package com.empresa.portal.service;

import com.empresa.portal.dto.AuthResponse;
import com.empresa.portal.dto.LoginRequest;
import com.empresa.portal.dto.RegisterRequest;
import com.empresa.portal.model.User;
import com.empresa.portal.repository.UserRepository;
import com.empresa.portal.security.CustomUserDetailsService;
import com.empresa.portal.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Value("${app.corporate.domain}")
    private String corporateDomain;

    @Value("${app.security.max-failed-attempts}")
    private Integer maxFailedAttempts;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (!request.getEmail().endsWith("@" + corporateDomain)) {
            throw new RuntimeException("Debes usar tu correo corporativo (@" + corporateDomain + ")");// DEBE TENER
                                                                                                      // @EMPRESA.COM
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.USER);

        user = userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));

        if (user.getAccountLocked()) {
            throw new RuntimeException("Tu cuenta está bloqueada. Por favor, recupera tu contraseña.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())); // Autenticacion

            // Intentos fallidos a 0 si tuvo intentos previos
            if (user.getFailedLoginAttempts() > 0) {
                user.setFailedLoginAttempts(0);
                userRepository.save(user);
            }

            // Generar token
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            String token = jwtUtil.generateToken(userDetails);

            return new AuthResponse(
                    token,
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().name());

        } catch (BadCredentialsException e) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

            if (user.getFailedLoginAttempts() >= maxFailedAttempts) {
                user.setAccountLocked(true);
                userRepository.save(user);
                throw new RuntimeException("Has excedido el número de intentos. Tu cuenta ha sido bloqueada.");
            }

            userRepository.save(user);

            int remainingAttempts = maxFailedAttempts - user.getFailedLoginAttempts();
            throw new RuntimeException("Credenciales incorrectas. Te quedan " + remainingAttempts + " intentos.");
        }
    }
}