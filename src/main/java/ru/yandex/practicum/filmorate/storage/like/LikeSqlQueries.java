package ru.yandex.practicum.filmorate.storage.like;

public final class LikeSqlQueries {
    private LikeSqlQueries() {
    }

    public static String ADD_LIKE_TO_FILM = "insert into likes (film_id, user_id) values (?, ?)";
    public static String DELETE_LIKE_FROM_FILM = "delete from likes where film_id = ? and user_id = ?";
    public static String GET_ALL_LIKES = "select * from likes";
    public static String GET_TOP_LIKES = "select *, count(film_id) likes from likes group by film_id order by count(film_id) desc limit ?";
}
