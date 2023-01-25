package ru.yandex.practicum.filmorate.storage.filmReview;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Qualifier("ReviewDbStorage")
@Component
public class ReviewDbStorage implements ReviewStorage {

    @Override
    public Review add(Review filmReview) {
        return null;
    }

    @Override
    public Review update(Review filmReview) {
        return null;
    }

    @Override
    public Review delete(Integer id) {
        return null;
    }

    @Override
    public Review getById(Integer id) {
        return null;
    }

    @Override
    public List<Review> getByFilmId(Integer id) {
        return null;
    }

    @Override
    public List<Review> getAll() {
        return null;
    }

    @Override
    public boolean isExists(Integer id) {
        return false;
    }
}
