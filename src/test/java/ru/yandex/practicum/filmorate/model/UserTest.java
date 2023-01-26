package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;

class UserTest {

    private final User user = User
        .builder()
        .login("a_b")
        .name("Nick Name")
        .email("mail@mail.ru")
        .birthday(LocalDate.now())
        .build();
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @Test
    void shouldCreateUser() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    void shouldNotCreateUserIfLoginIsWrong() {
        String[] logins = {"dolore ullamco", "d olore ullamc o", "", " ", null};

        Arrays.stream(logins).forEach(login -> {
            User userWithIncorrectLogin = user
                .toBuilder()
                .login(login)
                .build();

            Set<ConstraintViolation<User>> violations = validator.validate(userWithIncorrectLogin);

            Assertions.assertFalse(violations.isEmpty());
        });
    }

    @Test
    void shouldNotCreateUserIfEmailIsWrong() {
        String[] emails = {"user @domain.com", ".user@domain.co.in", "@domain.com", "user?name@doma in.co.in",
            "@domain.com",
            "",
            null};

        Arrays.stream(emails).forEach(email -> {
            User userWithIncorrectEmail = user
                .toBuilder()
                .email(email)
                .build();

            Set<ConstraintViolation<User>> violations = validator.validate(userWithIncorrectEmail);

            Assertions.assertFalse(violations.isEmpty());
        });
    }

    @Test
    void shouldNotCreateUserIfBirthdayIsWrong() {
        User userWithIncorrectBirthday = user
            .toBuilder()
            .birthday(LocalDate.now().plusDays(1))
            .build();

        Set<ConstraintViolation<User>> violations = validator.validate(userWithIncorrectBirthday);

        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals(1, violations.size());
    }
}
