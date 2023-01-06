package ru.yandex.practicum.filmorate.storage.filmGenre;

public final class FilmGenreSqlQueries {
    private FilmGenreSqlQueries() {
    }

    public static String ADD_FILM_GENRE = "insert into film_genres (film_id, genre_id) values (?, ?)";
    public static String GET_ALL_FILM_GENRES = "select * from film_genres where film_id = ?";
    public static String DELETE_ALL_FILM_GENRES = "delete from film_genres where film_id = ?";
}
