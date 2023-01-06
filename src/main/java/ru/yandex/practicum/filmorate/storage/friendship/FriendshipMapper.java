package ru.yandex.practicum.filmorate.storage.friendship;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendshipMapper implements RowMapper<Friendship> {

    @Override
    public Friendship mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Friendship()
                .toBuilder()
                .id(rs.getInt("id"))
                .userId(rs.getInt("user_id"))
                .friendId(rs.getInt("friend_id"))
                .build();
    }
}
