package ru.yandex.practicum.filmorate.storages.users;


import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storages.BaseRepository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Repository
public class DbUserStorage extends BaseRepository<User> implements UserStorage {

    public DbUserStorage(JdbcTemplate jdbcTemplate, UserRowMapper rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    @Override
    public List<User> getUsers() {
        String stmt = """
                SELECT *
                FROM users
                """;
        return this.getAll(stmt);
    }

    public Optional<User> getUser(int id) {
        String stmt = """
                SELECT *
                FROM users
                WHERE id = ?
                """;
        return this.getOne(stmt, id);
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
        return jdbcTemplate.query(stmt, rowMapper, userId);
    }

    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        String stmt = """
                    SELECT u.*
                    FROM users u
                    JOIN friends uf ON u.id = uf.friend_id
                    JOIN friends ff ON uf.friend_id = ff.friend_id
                    WHERE uf.user_id = ?
                      AND ff.user_id = ?;
                """;
        return jdbcTemplate.query(stmt, rowMapper, userId, friendId);
    }

}
