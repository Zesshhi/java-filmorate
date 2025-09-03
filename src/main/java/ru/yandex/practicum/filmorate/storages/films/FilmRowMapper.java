package ru.yandex.practicum.filmorate.storages.films;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.Mpa;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));

        film.setMpa(getMpa(resultSet));
        film.setGenres(getGenres(resultSet));

        return film;
    }

    private Mpa getMpa(ResultSet resultSet) throws SQLException {
        Mpa mpa = null;

        if (resultSet.getString("mpa") != null) {
            String[] parts = resultSet.getString("mpa").split(":");
            Integer mpaId = Integer.parseInt(parts[0]);
            String mpaName = parts[1];

            mpa = new Mpa();
            mpa.setId(mpaId);
            mpa.setName(mpaName);
        }

        return mpa;
    }

    private List<Genre> getGenres(ResultSet resultSet) throws SQLException {
        List<Genre> genres = null;

        if (resultSet.getString("genres") != null) {
            Array genresArray = resultSet.getArray("genres");
            Object[] genresData = (Object[]) genresArray.getArray();

            if (genresData.length > 0) {
                genres = new ArrayList<>();
            }

            for (Object genreData : genresData) {
                String[] parts = genreData.toString().split(":");
                Integer genreId = Integer.parseInt(parts[0]);
                String genreName = parts[1];

                Genre genre = new Genre();
                genre.setId(genreId);
                genre.setName(genreName);

                genres.add(genre);
            }
        }
        return genres;
    }
}