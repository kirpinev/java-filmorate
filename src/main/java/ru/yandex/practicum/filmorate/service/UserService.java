package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
        User user = getUserById(id);
        Collection<Friendship> friendships = friendshipStorage.getUserFriendsById(user.getId());

        return friendships.stream().map(friendship ->
                getUserById(friendship.getFriendId())).collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Integer firstUserId, Integer secondUserId) {
        List<Integer> firstUserFriends = friendshipStorage.getUserFriendsById(getUserById(firstUserId)
                .getId()).stream().map(Friendship::getFriendId).collect(Collectors.toList());
        List<Integer> secondUserFriends = friendshipStorage.getUserFriendsById(getUserById(secondUserId)
                .getId()).stream().map(Friendship::getFriendId).collect(Collectors.toList());

        firstUserFriends.retainAll(secondUserFriends);

        return firstUserFriends.stream().map(this::getUserById).collect(Collectors.toList());
    }

    public User updateUser(User user) {
        checkUserIsNotNull(user, user.getId());
        setUserName(user);

        return userStorage.updateUser(user);
    }

    public void addFriend(Integer userId, Integer friendId) {
        friendshipStorage.addFriend(getUserById(userId).getId(), getUserById(friendId).getId());
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        friendshipStorage.deleteFriend(getUserById(userId).getId(), getUserById(friendId).getId());
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
