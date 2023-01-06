package storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LikeStorageTest {

    private final Film film = new Film()
            .toBuilder()
            .name("Фильм")
            .description("Интересный")
            .releaseDate(LocalDate.now())
            .duration(100)
            .mpa(new Mpa(1, null))
            .rate(5)
            .build();

    private final User user = new User()
            .toBuilder()
            .name("Игорь")
            .login("pwned")
            .birthday(LocalDate.now())
            .email("igor@what.eng")
            .build();

    private final LikeStorage likeStorage;

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    @Test
    public void addAndDeleteLike() {
        Film newFilm = filmStorage.createFilm(film);
        User newUser = userStorage.createUser(user);

        likeStorage.addLikeToFilm(newFilm.getId(), newUser.getId());

        List<Like> likes = new ArrayList<>(likeStorage.getAllLikes());

        assertEquals(likes.size(), 1);
        assertThat(likes.get(0)).hasFieldOrPropertyWithValue("filmId", newFilm.getId());
        assertThat(likes.get(0)).hasFieldOrPropertyWithValue("userId", newUser.getId());

        likeStorage.deleteLikeFromFilm(newFilm.getId(), newUser.getId());
        
        likes = new ArrayList<>(likeStorage.getAllLikes());

        assertEquals(likes.size(), 0);
    }
}
