package ru.yandex.practicum.filmorate.storage.friendship;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.Collection;

@Component
@Qualifier("FriendshipDbStorage")
public class FriendshipDbStorage implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;

    public FriendshipDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        jdbcTemplate.update(FriendshipSqlQueries.ADD_FRIEND, userId, friendId);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        jdbcTemplate.update(FriendshipSqlQueries.DELETE_FRIEND, userId, friendId);
    }

    @Override
    public Collection<Friendship> getUserFriendsById(Integer userId) {
        return jdbcTemplate.query(FriendshipSqlQueries.GET_USER_FRIENDS, new FriendshipMapper(), userId);
    }

    @Override
    public Collection<Friendship> getAllFriends() {
        return jdbcTemplate.query(FriendshipSqlQueries.GER_ALL_FRIENDS, new FriendshipMapper());
    }
}
