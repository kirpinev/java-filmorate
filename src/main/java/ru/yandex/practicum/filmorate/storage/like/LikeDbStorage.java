package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.Collection;

@Component
@Qualifier("LikeDbStorage")
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLikeToFilm(Integer filmId, Integer userId) {
        jdbcTemplate.update(LikeSqlQueries.ADD_LIKE_TO_FILM, filmId, userId);
    }

    @Override
    public void deleteLikeFromFilm(Integer filmId, Integer userId) {
        jdbcTemplate.update(LikeSqlQueries.DELETE_LIKE_FROM_FILM, filmId, userId);
    }

    @Override
    public Collection<Like> getAllLikes() {
        return jdbcTemplate.query(LikeSqlQueries.GET_ALL_LIKES, new LikeMapper());
    }

    @Override
    public Collection<Like> getTopLikes(Integer count) {
        return jdbcTemplate.query(LikeSqlQueries.GET_TOP_LIKES, new LikeMapper(), count);
    }
}
