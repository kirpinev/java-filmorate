package ru.yandex.practicum.filmorate.storage.filmMpa;

public final class FilmMpaSqlQueries {

    private FilmMpaSqlQueries() {
    }

    public static String ADD_FILM_MPA = "insert into film_mpas (film_id, mpa_id) values (?, ?)";
    public static String GET_FILM_MPA = "select * from film_mpas where film_id = ?";
    public static String DELETE_FILM_MPA = "delete from film_mpas where film_id = ?";
    public static String GET_ALL_FILM_MPA = "select * from film_mpas";
}
