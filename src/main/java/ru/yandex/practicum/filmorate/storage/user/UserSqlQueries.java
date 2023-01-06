package ru.yandex.practicum.filmorate.storage.user;

public final class UserSqlQueries {
    private UserSqlQueries() {
    }

    public static String CREATE_USER = "insert into users (name, login, birthday, email) values (?, ?, ?, ?)";
    public static String FIND_USER = "select * from users where id = ?";
    public static String FIND_ALL_USERS = "select * from users";
    public static String UPDATE_USER = "update users set name = ?, login = ?, birthday = ?, email = ? where id = ?";
    public static String DELETE_USER = "delete from users where id = ?";
}
