package ru.yandex.practicum.filmorate.storage.mpa;

public final class MpaSqlQueries {
    private MpaSqlQueries() {
    }

    public static String GET_MPA_BY_ID = "select * from mpas where id = ?";
    public static String GET_ALL_MPA = "select * from mpas";
}
