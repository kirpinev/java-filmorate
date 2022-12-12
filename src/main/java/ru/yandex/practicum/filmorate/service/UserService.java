package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage inMemoryUserStorage) {
        this.userStorage = inMemoryUserStorage;
    }

    public User createUser(User user) {
        return userStorage.create(user);
    }

    public Map<Integer, User> getUsers() {
        return userStorage.getAll();
    }

    public User getUserById(Integer id) {
        return userStorage.getById(id);
    }

    public List<User> getUserFriends(Integer id) {
        User user = getUserById(id);
        Map<Integer, User> users = getUsers();

        return user
                .getFriends()
                .stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(User user1, User user2) {
        Map<Integer, User> users = getUsers();
        Set<Integer> user1Friends = user1.getFriends();
        Set<Integer> user2Friends = user2.getFriends();

        return user1Friends
                .stream()
                .filter(user2Friends::contains)
                .map(users::get)
                .collect(Collectors.toList());
    }

    public User updateUser(User user) {
        return userStorage.update(user);
    }

    public void addFriendToUser(User user, User friend) {
        user.getFriends().add(friend.getId());
        userStorage.update(user);
    }

    public void deleteFriendFromUser(User user, User friend) {
        user.getFriends().remove(friend.getId());
    }
}
