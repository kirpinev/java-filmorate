package ru.yandex.practicum.filmorate.storage.filmReview;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component("ReviewDbStorage")
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate template;
    private static final String GET_REVIEW_BASE_QUERY =
            "SELECT review_id, film_id, user_id, useful, is_positive, content FROM reviews ";

    private static final String GET_ALL_REVIEWS = GET_REVIEW_BASE_QUERY + " ORDER BY review_id";

    private static final String GET_REVIEW_BY_ID_QUERY =
            GET_REVIEW_BASE_QUERY + " WHERE review_id = ?";

    private static final String GET_REVIEW_BY_FILM_ID_QUERY =
            GET_REVIEW_BASE_QUERY + " WHERE film_id = ? ORDER BY review_id";

    private static final String SAVE_REVIEW_QUERY =
            "INSERT INTO reviews (film_id, user_id, useful, is_positive, content) VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_REVIEW_QUERY =
            "UPDATE reviews SET film_id = ?, user_id = ?, useful = ?, is_positive = ?, content = ?) WHERE review_id = ?";

    private static final String DELETE_REVIEW_BY_ID_QUERY =
            "DELETE FROM reviews WHERE review_id = ?";

    private static final String CHECK_REVIEW_EXISTS_BY_ID_QUERY =
            "SELECT EXISTS(SELECT review_id FROM reviews WHERE review_id = ?) isExists";

    @Autowired
    public ReviewDbStorage(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public Review add(Review review) {
        KeyHolder holder = new GeneratedKeyHolder();
        int savedReviewId;

        template.update(conn -> {
            PreparedStatement smt = conn.prepareStatement(SAVE_REVIEW_QUERY, new String[] {"review_id"});
            smt.setInt(1, review.getFilmId());
            smt.setInt(2, review.getUserId());
            smt.setInt(3, review.getUseful());
            smt.setBoolean(4, review.getIsPositive());
            smt.setString(5, review.getContent());
            return smt;
        }, holder);

        try {
            savedReviewId = holder.getKey().intValue();
        } catch (NullPointerException e) {
            log.warn("Ошибка при получении id записываемого в БД отзыва {}: {}", review, e.getMessage());
            throw e;
        }
        log.debug("Отзыв записан с id = {}", savedReviewId);

        return getById(savedReviewId);
    }

    @Override
    public Review update(Review filmReview) {
        template.update(UPDATE_REVIEW_QUERY,
                filmReview.getFilmId(),
                filmReview.getUserId(),
                filmReview.getUseful(),
                filmReview.getIsPositive(),
                filmReview.getContent(),
                filmReview.getReviewId()
        );
        return getById(filmReview.getReviewId());
    }

    @Override
    public void delete(Integer id) {
        template.update(DELETE_REVIEW_BY_ID_QUERY, id);
    }

    @Override
    public Review getById(Integer id) {
        return template.queryForObject(GET_REVIEW_BY_ID_QUERY, this::mapRowToReview, id);
    }

    @Override
    public List<Review> getByFilmId(Integer id) {
        return template.query(GET_REVIEW_BY_FILM_ID_QUERY, this::mapRowToReview, id);
    }

    @Override
    public List<Review> getAll() {
        return template.query(GET_ALL_REVIEWS, this::mapRowToReview);
    }

    @Override
    public boolean isExists(Integer id) {
        return Boolean.TRUE.equals(template.queryForObject(
                CHECK_REVIEW_EXISTS_BY_ID_QUERY, (rs, rowNum) -> rs.getBoolean("isExists"), id
        ));
    }

    private Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getInt("review_id"))
                .filmId(rs.getInt("film_id"))
                .userId(rs.getInt("user_id"))
                .useful(rs.getInt("useful"))
                .isPositive(rs.getBoolean("is_positive"))
                .content(rs.getString("content"))
                .build();
    }

}
