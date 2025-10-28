package com.empresa.portal.service;

import com.empresa.portal.dto.UserDTO;
import com.empresa.portal.model.User;
import com.empresa.portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertToDTO(user);
    }

    @Transactional
    public UserDTO toggleAdminRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (user.getRole() == User.Role.ADMIN) {
            user.setRole(User.Role.USER);
        } else {
            user.setRole(User.Role.ADMIN);
        }

        user = userRepository.save(user);
        return convertToDTO(user);
    }

    @Transactional
    public UserDTO makeAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (user.getRole() == User.Role.ADMIN) {
            throw new RuntimeException("El usuario ya es administrador");
        }

        user.setRole(User.Role.ADMIN);
        user = userRepository.save(user);
        return convertToDTO(user);
    }

    @Transactional
    public UserDTO removeAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (user.getRole() == User.Role.USER) {
            throw new RuntimeException("El usuario no es administrador");
        }

        user.setRole(User.Role.USER);
        user = userRepository.save(user);
        return convertToDTO(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que no sea el último admin
        long adminCount = userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.Role.ADMIN)
                .count();

        if (user.getRole() == User.Role.ADMIN && adminCount <= 1) {
            throw new RuntimeException("No se puede eliminar unico administrador del sistema");
        }

        userRepository.delete(user);
    }

    @Transactional
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (userDTO.getName() != null) {
            user.setName(userDTO.getName());
        }

        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                throw new RuntimeException("El email ya está en uso");
            }
            user.setEmail(userDTO.getEmail());
        }

        user = userRepository.save(user);
        return convertToDTO(user);
    }

    // desbloquear cuenta
    @Transactional
    public UserDTO unlockAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);

        user = userRepository.save(user);
        return convertToDTO(user);
    }

    // Estadisticas de usuarios
    public Map<String, Object> getUserStats() {
        List<User> allUsers = userRepository.findAll();

        long totalUsers = allUsers.size();
        long adminUsers = allUsers.stream()
                .filter(u -> u.getRole() == User.Role.ADMIN)
                .count();
        long regularUsers = allUsers.stream()
                .filter(u -> u.getRole() == User.Role.USER)
                .count();
        long lockedAccounts = allUsers.stream()
                .filter(User::getAccountLocked)
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("adminUsers", adminUsers);
        stats.put("regularUsers", regularUsers);
        stats.put("lockedAccounts", lockedAccounts);

        return stats;
    }

    // UserDTO
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setAccountLocked(user.getAccountLocked());
        dto.setFailedLoginAttempts(user.getFailedLoginAttempts());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}