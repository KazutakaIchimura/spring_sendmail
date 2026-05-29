package com.example.sendmail.service;

import com.example.sendmail.domain.entity.Role;
import com.example.sendmail.domain.entity.Staff;
import com.example.sendmail.dto.request.CreateStaffRequest;
import com.example.sendmail.dto.response.StaffResponse;
import com.example.sendmail.exception.DuplicateResourceException;
import com.example.sendmail.exception.ResourceNotFoundException;
import com.example.sendmail.repository.RoleRepository;
import com.example.sendmail.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<StaffResponse> listStaffs() {
        return staffRepository.findByIsActiveTrue().stream()
                .map(StaffResponse::from)
                .toList();
    }

    @Transactional
    public StaffResponse createStaff(CreateStaffRequest req) {
        if (staffRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new DuplicateResourceException("このメールアドレスは既に使用されています: " + req.getEmail());
        }
        if (req.getPassword() == null || req.getPassword().isBlank()) {
            throw new IllegalArgumentException("パスワードは必須です");
        }
        Role role = findRoleByName(req.getRole());
        Staff staff = new Staff();
        staff.setName(req.getName());
        staff.setEmail(req.getEmail());
        staff.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        staff.setRole(role);
        return StaffResponse.from(staffRepository.save(staff));
    }

    @Transactional
    public StaffResponse updateStaff(Long id, CreateStaffRequest req) {
        Role role = findRoleByName(req.getRole());
        Staff staff = findStaffById(id);
        staff.setName(req.getName());
        staff.setRole(role);
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            staff.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        }
        return StaffResponse.from(staffRepository.save(staff));
    }

    @Transactional
    public void activateStaff(Long id) {
        Staff staff = findStaffById(id);
        staff.setIsActive(true);
        staffRepository.save(staff);
    }

    @Transactional
    public void deactivateStaff(Long id) {
        Staff staff = findStaffById(id);
        staff.setIsActive(false);
        staffRepository.save(staff);
    }

    private Staff findStaffById(Long id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("スタッフが見つかりません: " + id));
    }

    private Role findRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("ロールが見つかりません: " + name));
    }
}
