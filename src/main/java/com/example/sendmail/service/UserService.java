package com.example.sendmail.service;

import com.example.sendmail.domain.entity.Office;
import com.example.sendmail.domain.entity.User;
import com.example.sendmail.domain.entity.UserOffice;
import com.example.sendmail.dto.request.CreateUserRequest;
import com.example.sendmail.dto.request.UpdateUserRequest;
import com.example.sendmail.dto.response.OfficeResponse;
import com.example.sendmail.dto.response.UserResponse;
import com.example.sendmail.exception.DuplicateResourceException;
import com.example.sendmail.exception.ResourceNotFoundException;
import com.example.sendmail.repository.OfficeRepository;
import com.example.sendmail.repository.UserOfficeRepository;
import com.example.sendmail.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OfficeRepository officeRepository;
    private final UserOfficeRepository userOfficeRepository;

    @Transactional(readOnly = true)
    public List<UserResponse> listUsers(boolean includeInactive) {
        List<User> users = includeInactive
                ? userRepository.findAll()
                : userRepository.findByIsActiveTrue();
        return users.stream().map(UserResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        User user = findUserById(id);
        List<OfficeResponse> offices = userOfficeRepository.findByUserId(id).stream()
                .map(uo -> OfficeResponse.from(uo.getOffice()))
                .toList();
        return UserResponse.fromWithOffices(user, offices);
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest req) {
        User user = new User();
        user.setName(req.getName());
        user.setNameKana(req.getNameKana());
        user.setBirthDate(req.getBirthDate());
        user.setNotes(req.getNotes());
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest req) {
        User user = findUserById(id);
        user.setName(req.getName());
        user.setNameKana(req.getNameKana());
        user.setBirthDate(req.getBirthDate());
        user.setNotes(req.getNotes());
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public void deactivateUser(Long id) {
        User user = findUserById(id);
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Transactional
    public UserResponse activateUser(Long id) {
        User user = findUserById(id);
        user.setIsActive(true);
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<OfficeResponse> getUserOffices(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("利用者が見つかりません: " + userId);
        }
        return userOfficeRepository.findByUserId(userId).stream()
                .map(uo -> OfficeResponse.from(uo.getOffice()))
                .toList();
    }

    @Transactional
    public void addOfficeToUser(Long userId, Long officeId) {
        if (userOfficeRepository.existsByUserIdAndOfficeId(userId, officeId)) {
            throw new DuplicateResourceException("既に紐付けられています");
        }
        User user = findUserById(userId);
        Office office = officeRepository.findById(officeId)
                .orElseThrow(() -> new ResourceNotFoundException("事業所が見つかりません: " + officeId));
        UserOffice uo = new UserOffice();
        uo.setUser(user);
        uo.setOffice(office);
        userOfficeRepository.save(uo);
    }

    @Transactional
    public void removeOfficeFromUser(Long userId, Long officeId) {
        UserOffice uo = userOfficeRepository.findByUserIdAndOfficeId(userId, officeId)
                .orElseThrow(() -> new ResourceNotFoundException("紐付けが見つかりません"));
        userOfficeRepository.delete(uo);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("利用者が見つかりません: " + id));
    }
}
