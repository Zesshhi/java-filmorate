package ru.yandex.practicum.filmorate;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.services.UserService;
import ru.yandex.practicum.filmorate.storages.users.DbUserStorage;
import ru.yandex.practicum.filmorate.storages.users.UserRowMapper;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;


@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
        UserService.class,
        DbUserStorage.class,
        UserRowMapper.class
})
@Sql(scripts = "/setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserControllerTests {

    @Autowired
    private UserService userService;

    private User user2 = new User(2, "aboba2@mail.ru", "abobaLogin2", "aboba user3", "1946-08-20");

    @Test
    public void should_create_user() throws Exception {
        userService.create(user2);
        User newUser = userService.getUser(2);
        assertThat(newUser.getId()).isEqualTo(user2.getId());
    }

    @Test
    public void should_create_user_with_empty_name() throws Exception {
        user2.setName("");
        userService.create(user2);

        User newUser = userService.getUser(2);
        assertThat(newUser.getId()).isEqualTo(user2.getId());
    }


    @Test
    public void should_not_create_user_with_same_email() throws Exception {
        userService.create(user2);

        assertThatThrownBy(() -> userService.create(user2))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    public void should_update_user() throws Exception {
        userService.create(user2);

        user2.setLogin("abobaLoginUpdate");
        user2.setName("Aboba User Update");
        user2.setEmail("AbobaUpdate@mail.ru");
        user2.setBirthday(LocalDate.parse("1956-08-20"));

        userService.update(user2);

        User updatedUser = userService.getUser(2);

        assertThat(updatedUser).hasFieldOrPropertyWithValue("login", "abobaLoginUpdate");
        assertThat(updatedUser).hasFieldOrPropertyWithValue("name", "Aboba User Update");
        assertThat(updatedUser).hasFieldOrPropertyWithValue("email", "AbobaUpdate@mail.ru");
        assertThat(updatedUser).hasFieldOrPropertyWithValue("birthday", LocalDate.parse("1956-08-20"));
    }

    @Test
    public void should_not_find_id_update_user() throws Exception {
        assertThatThrownBy(() -> userService.getUser(666)).isInstanceOf(NotFoundException.class);
    }

    @Test
    public void should_get_all_users() throws Exception {

        User user3 = new User(3, "aboba3@mail.ru", "abobaLogin3", "aboba user4", "1946-08-20");
        User user4 = new User(4, "aboba4@mail.ru", "abobaLogin4", "aboba User4", "1946-08-20");

        userService.create(user2);
        userService.create(user3);
        userService.create(user4);

        assertEquals(4, userService.getUsers().size());
    }


    @Test
    public void should_add_friend() throws Exception {
        userService.create(user2);

        userService.addFriend(user2.getId(), 1);

        assertEquals(1, userService.getUserFriends(user2.getId()).size());
    }

    @Test
    public void should_not_add_unknown_friend() throws Exception {
        userService.create(user2);

        assertThatThrownBy(() -> userService.addFriend(user2.getId(), 666)).isInstanceOf(NotFoundException.class);
    }

    @Test
    public void should_not_return_friend_with_unknown_user() throws Exception {
        assertThatThrownBy(() -> userService.getUserFriends(666));
    }

    @Test
    public void should_remove_friend() throws Exception {
        userService.create(user2);

        userService.addFriend(user2.getId(), 1);
        userService.deleteFriend(user2.getId(), 1);

        assertEquals(0, userService.getUserFriends(user2.getId()).size());
    }

    @Test
    public void should_not_remove_unknown_friend() throws Exception {
        userService.create(user2);

        assertThatThrownBy(() -> userService.deleteFriend(user2.getId(), 666)).isInstanceOf(NotFoundException.class);
    }

    @Test
    public void should_not_remove_from_unknown_user() throws Exception {
        userService.create(user2);

        assertThatThrownBy(() -> userService.deleteFriend(666, 1)).isInstanceOf(NotFoundException.class);
    }

    @Test
    public void should_return_common_friends() throws Exception {
        User user3 = new User(3, "aboba3@mail.ru", "abobaLogin3", "aboba user4", "1946-08-20");
        User user4 = new User(4, "aboba4@mail.ru", "abobaLogin4", "aboba User4", "1946-08-20");

        userService.create(user2);
        userService.create(user3);
        userService.create(user4);

        userService.addFriend(user2.getId(), 1);
        userService.addFriend(user2.getId(), 4);

        userService.addFriend(user3.getId(), 4);

        assertEquals(1, userService.getCommonFriends(user2.getId(), user3.getId()).size());
        assertEquals(4, userService.getCommonFriends(user2.getId(), user3.getId()).get(0).getId());
    }
}
