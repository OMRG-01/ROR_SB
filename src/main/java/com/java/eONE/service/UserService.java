package com.java.eONE.service;

import com.java.eONE.model.User;
import com.java.eONE.repository.ClassroomRepository;
import com.java.eONE.repository.RoleRepository;
import com.java.eONE.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.java.eONE.DTO.UserResponseDTO;
import com.java.eONE.model.Role;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    

    @Autowired private RoleRepository roleRepository;
    @Autowired private ClassroomRepository classroomRepository;
    @Autowired private MailService mailService;
    
  //  @Autowired private MailService mailService; // You create this for sending emails
    
    public Optional<User> authenticate(String email, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(rawPassword, user.getPasswordDigest())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public boolean isApproved(User user) {
        // Assuming status 1 = approved, based on your ROR enum
        return user.getStatus() != null && user.getStatus() == 1;
    }
    
    @Transactional
    public UserResponseDTO registerUser(User user) {
        user.setStatus(0); // pending status (adjust if you use enum)
        User savedUser = userRepository.save(user);
        return toDTO(savedUser, null);
    }

    public List<UserResponseDTO> getPendingApprovals(String type, Long teacherId) {
        Role role = roleRepository.findByName(type);
        if (role == null) return List.of();

        if ("student".equalsIgnoreCase(type) && teacherId != null) {
            User teacher = userRepository.findById(teacherId).orElse(null);
            if (teacher == null) return List.of();

            return userRepository.findByRoleClassroomStatusExcludingAdmin(role.getId(), teacher.getClassroom().getId(), 0)
                    .stream().map(u -> toDTO(u, null)).collect(Collectors.toList());
        } else {
            return userRepository.findByRoleAndStatusExcludingAdmin(role.getId(), 0)
                    .stream().map(u -> toDTO(u, null)).collect(Collectors.toList());
        }
    }

    public List<UserResponseDTO> getApprovedUsers(String type, Long teacherId) {
        Role role = roleRepository.findByName(type);
        if (role == null) return List.of();

        if ("teacher".equalsIgnoreCase(type) && teacherId != null) {
            User teacher = userRepository.findById(teacherId).orElse(null);
            if (teacher == null) return List.of();

            return userRepository.findByRoleClassroomStatusExcludingAdmin(role.getId(), teacher.getClassroom().getId(), 1)
                    .stream().map(u -> toDTO(u, null)).collect(Collectors.toList());
        } else {
            return userRepository.findByRoleAndStatusExcludingAdmin(role.getId(), 1)
                    .stream().map(u -> toDTO(u, null)).collect(Collectors.toList());
        }
    }

    @Transactional
    public boolean approveUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;

        user.setStatus(1); // approved
        userRepository.save(user);
        mailService.sendApprovalEmail(user);
        return true;
    }

    @Transactional
    public boolean rejectUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;

        user.setStatus(2); // rejected
        userRepository.save(user);
        mailService.sendRejectionEmail(user);
        return true;
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(u -> toDTO(u, null)).collect(Collectors.toList());
    }

    public long getPendingApprovalsCount() {
        List<Long> roleIds = roleRepository.findByNameIn(List.of("teacher", "company"))
                .stream().map(Role::getId).collect(Collectors.toList());
        return userRepository.countByStatusAndRoleIdIn(0, roleIds);
    }

    public long getClassroomCount() {
        return classroomRepository.count();
    }

    public UserResponseDTO getUserById(Long id) {
        return userRepository.findById(id).map(u -> toDTO(u, null)).orElse(null);
    }

    private UserResponseDTO toDTO(User user, String token) {
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getMobileNumber(),
                user.getStatus(),
                user.getDateOfBirth(),
                user.getRole() != null ? user.getRole().getName() : null,
                user.getClassroom() != null ? user.getClassroom().getName() : null,
                user.getClassroom() != null ? user.getClassroom().getId() : null,
                token
        );
    }
}
