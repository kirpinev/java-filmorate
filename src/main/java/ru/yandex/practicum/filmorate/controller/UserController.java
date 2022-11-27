package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private int id = 1;
    private final HashMap<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        LocalDate todayDate = LocalDate.now();

        if (user.getBirthday().isAfter(todayDate)) {
            throw new ValidationException("Указана неверная дата!");
        }
        
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        user.setId(id);

        id += 1;

        log.debug("Создается пользователь: {}", user);

        users.put(user.getId(), user);

        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.debug("Обновляется пользователь: {}", user);

        if (Objects.isNull(users.get(user.getId()))) {
            throw new ValidationException("Такого фильма нет");
        }

        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);

        return user;
    }
}
