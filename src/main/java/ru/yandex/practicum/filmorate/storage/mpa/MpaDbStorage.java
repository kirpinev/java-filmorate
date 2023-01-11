package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Component
@Qualifier("MpaDbStorage")
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa getMpaById(Integer mpaId) {
        final String sql = "select * from mpas where id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new MpaMapper(), mpaId);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Collection<Mpa> getAllMpa() {
        final String sql = "select * from mpas";

        return jdbcTemplate.query(sql, new MpaMapper());
    }
}
