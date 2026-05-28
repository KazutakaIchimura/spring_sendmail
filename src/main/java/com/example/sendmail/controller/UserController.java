package com.example.sendmail.controller;

import com.example.sendmail.dto.request.CreateUserRequest;
import com.example.sendmail.dto.request.UpdateUserRequest;
import com.example.sendmail.dto.response.OfficeResponse;
import com.example.sendmail.dto.response.UserResponse;
import com.example.sendmail.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponse> listUsers() {
        return userService.listUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest req) {
        return userService.createUser(req);
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id,
                                   @Valid @RequestBody UpdateUserRequest req) {
        return userService.updateUser(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
    }

    @GetMapping("/{userId}/offices")
    public List<OfficeResponse> getUserOffices(@PathVariable Long userId) {
        return userService.getUserOffices(userId);
    }

    @PostMapping("/{userId}/offices")
    @ResponseStatus(HttpStatus.CREATED)
    public void addOfficeToUser(@PathVariable Long userId,
                                @RequestBody Map<String, Long> body) {
        userService.addOfficeToUser(userId, body.get("officeId"));
    }

    @DeleteMapping("/{userId}/offices/{officeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeOfficeFromUser(@PathVariable Long userId,
                                     @PathVariable Long officeId) {
        userService.removeOfficeFromUser(userId, officeId);
    }
}
