package ru.yandex.practicum.filmorate.storage.friendship;

public final class FriendshipSqlQueries {
    private FriendshipSqlQueries() {
    }

    public static String ADD_FRIEND = "insert into friendships (user_id, friend_id) values (?, ?)";
    public static String DELETE_FRIEND = "delete from friendships where user_id = ? and friend_id = ?";

    public static String GET_USER_FRIENDS = "select * from friendships where user_id = ?";
    public static String GER_ALL_FRIENDS = "select * from friendships";
}
