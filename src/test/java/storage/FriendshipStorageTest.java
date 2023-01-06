package storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FriendshipStorageTest {

    private final User user1 = new User()
            .toBuilder()
            .name("Игорь")
            .login("pwned")
            .birthday(LocalDate.now())
            .email("igor@what.eng")
            .build();

    private final User user2 = new User()
            .toBuilder()
            .name("Стас")
            .login("pwned")
            .birthday(LocalDate.now())
            .email("igor@what.eng")
            .build();

    private final FriendshipStorage friendshipStorage;

    private final UserStorage userStorage;

    User newUser1;
    User newUser2;

    @BeforeEach
    public void addFriends() {
        newUser1 = userStorage.createUser(user1);
        newUser2 = userStorage.createUser(user2);

        friendshipStorage.addFriend(newUser1.getId(), newUser2.getId());
    }

    @Test
    public void addFriend() {
        Collection<Friendship> friendships = friendshipStorage.getAllFriends();

        assertEquals(friendships.size(), 1);
    }

    @Test
    public void deleteFriend() {
        friendshipStorage.deleteFriend(newUser1.getId(), newUser2.getId());

        Collection<Friendship> friendships = friendshipStorage.getAllFriends();

        assertEquals(friendships.size(), 0);
    }

    @Test
    public void getUserFriendsById() {
        Collection<Friendship> friendships = friendshipStorage.getUserFriendsById(newUser1.getId());
        Friendship friendship = new ArrayList<>(friendships).get(0);

        assertThat(friendship).hasFieldOrPropertyWithValue("id", 1);
        assertThat(friendship).hasFieldOrPropertyWithValue("userId", newUser1.getId());
        assertThat(friendship).hasFieldOrPropertyWithValue("friendId", newUser2.getId());
    }
}
