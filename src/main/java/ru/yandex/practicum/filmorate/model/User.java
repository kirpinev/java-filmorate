package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
public class User {
    private int id;
    @Email(regexp = ".+@.+\\..+")
    private String email;
    @Pattern(regexp = "^[a-zA-Z0-9]+")
    private String login;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}
