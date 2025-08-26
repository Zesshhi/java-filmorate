package ru.yandex.practicum.filmorate.storages.users;


import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storages.BaseRepository;

import java.sql.Date;
import java.util.List;

@Slf4j
@Component
@Repository
public class DbUserStorage extends BaseRepository<User> implements UserStorage {


    public DbUserStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public List<User> getUsers() {
        String stmt = """
                SELECT *
                FROM users
                """;
        return jdbcTemplate.query(stmt, new UserRowMapper());
    }

    public User getUser(int id) {
        String stmt = """
                SELECT *
                FROM users
                WHERE id = ?
                """;
        return jdbcTemplate.queryForObject(stmt, new UserRowMapper(), id);
    }

    @Override
    public void clearUsers() {
        String stmt = """
                TRUNCATE TABLE users CASCADE
                """;
        jdbcTemplate.update(
                stmt
        );
    }

    @Override
    public User create(User user) {
        String stmt = """
                INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)
                """;
        int id = insert(
                stmt,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                Date.valueOf(user.getBirthday())
        );
        user.setId(id);

        return user;
    }

    @Override
    public User update(User user) {
        String stmt = """
                UPDATE users
                SET email = ?, login = ?, name = ?, birthday = ?
                WHERE id = ?
                """;
        jdbcTemplate.update(
                stmt,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    public void connectFriends(Integer userId, Integer friendId) {
        String stmt = """
                INSERT INTO friends (user_id, friend_id) VALUES (?, ?)
                """;
        jdbcTemplate.update(stmt, userId, friendId);
    }

    public void removeFriendsConnection(Integer userId, Integer friendId) {
        String stmt = """
                DELETE FROM friends WHERE user_id = ? and friend_id = ?
                """;

        jdbcTemplate.update(stmt, userId, friendId);
    }

    public List<User> getFriends(Integer userId) {
        String stmt = """
                SELECT *
                FROM friends f
                JOIN users u ON f.friend_id = u.id
                WHERE f.user_id = ?
                """;
        return jdbcTemplate.query(stmt, new UserRowMapper(), userId);
    }

    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        String stmt = """
                    SELECT *
                    FROM users u
                    JOIN (SELECT user_friends.friend_id
                    from friends user_friends
                    JOIN friends friend_friends ON user_friends.friend_id = friend_friends.friend_id
                    WHERE user_friends.user_id = ? and friend_friends.user_id = ?
                    ) f ON f.friend_id = u.id
                """;
        return jdbcTemplate.query(stmt, new UserRowMapper(), userId, friendId);
    }

}
