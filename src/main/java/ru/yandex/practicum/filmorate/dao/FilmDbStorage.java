package ru.yandex.practicum.filmorate.dao;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@Getter
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("releaseDate").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .build();
    }

    @Override
    public List<Film> getAll() {
        String sql = "select * from films";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Optional<Film> create(Film film) {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        String key = simpleJdbcInsert.executeAndReturnKey(film.toMap()).toString();
        return findFilmById(key);
    }

    @Override
    public Optional<Film> update(Film film) {
        String sqlQuery = "update films set name = ?, description = ?, releaseDate = ?, duration = ?, likesNumber = ? "
                + "where id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getLikesNumber(),
                film.getId());
        return findFilmById(film.getId().toString());
    }

    public Optional<Film> findFilmById(String id) {
        try {
            String sqlQuery = "select id, name, description, releaseDate, duration from films where id = ?";
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id));
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Заданного фильма не существует");
        }
    }
}
