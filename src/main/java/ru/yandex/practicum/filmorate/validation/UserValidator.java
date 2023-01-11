package ru.yandex.practicum.filmorate.validation;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class UserValidator {

    public static boolean isUserNotFound(Collection<User> users, User user) {
        return Objects.isNull(user) || Objects.isNull(users.stream()
                .collect(Collectors.toMap(User::getId, u -> u)).get(user.getId()));
    }

    public static boolean isUserNameValid(String userName) {
        return Objects.nonNull(userName) && !userName.isEmpty() && !userName.isBlank();
    }
}
