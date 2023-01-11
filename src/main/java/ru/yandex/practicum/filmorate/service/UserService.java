package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.util.Collection;

@Service
public class UserService {

    private static final String NOT_FOUND_MESSAGE = "пользователя с id %s нет";
    private final UserStorage userStorage;

    private final FriendshipStorage friendshipStorage;

    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage,
                       @Qualifier("FriendshipDbStorage") FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
    }

    public User createUser(User user) {
        setUserName(user);

        return userStorage.createUser(user);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Integer id) {
        User user = userStorage.getUserById(id);

        checkUserIsNotNull(user, id);

        return user;
    }

    public Collection<User> getUserFriends(Integer id) {
        return userStorage.getUserFriends(id);
    }

    public Collection<User> getCommonFriends(Integer firstUserId, Integer secondUserId) {
        return userStorage.getCommonFriends(firstUserId, secondUserId);
    }

    public User updateUser(User user) {
        checkUserIsNotNull(user, user.getId());
        setUserName(user);

        return userStorage.updateUser(user);
    }

    public void addFriend(Integer userId, Integer friendId) {
        // здесь запрашиваю друга потому что нужно проверить существует он или нет
        friendshipStorage.addFriend(userId, getUserById(friendId).getId());
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        friendshipStorage.deleteFriend(userId, friendId);
    }

    private void checkUserIsNotNull(User user, Integer id) {
        if (UserValidator.isUserNotFound(getAllUsers(), user)) {
            throw new NotFoundException(String.format(NOT_FOUND_MESSAGE, id));
        }
    }

    private void setUserName(User user) {
        if (!UserValidator.isUserNameValid(user.getName())) {
            user.setName(user.getLogin());
        }
    }
}
