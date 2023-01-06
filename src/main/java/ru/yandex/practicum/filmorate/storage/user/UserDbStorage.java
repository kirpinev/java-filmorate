package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.util.*;

@Component
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

    }

    @Override
    public User createUser(User user) {
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(UserSqlQueries.CREATE_USER,
                    new String[]{"id"});
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getLogin());
            preparedStatement.setObject(3, user.getBirthday());
            preparedStatement.setString(4, user.getEmail());

            return preparedStatement;
        }, generatedKeyHolder);

        int userId = Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();

        return user.toBuilder().id(userId).build();
    }

    @Override
    public User getUserById(Integer userId) {
        try {
            return jdbcTemplate.queryForObject(UserSqlQueries.FIND_USER, new UserMapper(), userId);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        return jdbcTemplate.query(UserSqlQueries.FIND_ALL_USERS, new UserMapper());
    }

    @Override
    public User updateUser(User user) {
        jdbcTemplate.update(
                UserSqlQueries.UPDATE_USER,
                user.getName(), user.getLogin(), user.getBirthday(), user.getEmail(), user.getId()
        );

        return user;
    }
}
