package ru.yandex.practicum.filmorate.storage.genre;

public final class GenreSqlQueries {
    private GenreSqlQueries() {
    }

    public static String GET_GENRE_BY_ID = "select * from genres where id = ?";
    public static String GET_ALL_GENRES = "select * from genres";
}
