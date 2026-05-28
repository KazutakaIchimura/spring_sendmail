package com.example.sendmail.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateUserRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 100)
    private String nameKana;

    private LocalDate birthDate;

    private String notes;
}
