package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class User {
    private int id;
    @Email(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotBlank
    private String email;
    @Pattern(regexp = "^[a-zA-Z0-9]+")
    @NotBlank
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
}
