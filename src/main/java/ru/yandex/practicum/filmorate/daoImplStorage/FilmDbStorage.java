package ru.yandex.practicum.filmorate.daoImplStorage;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage, PreparedStatementCreator {
    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private static final String SELECT_ALL_FILMS = "SELECT * FROM films";
    private static final String CREATE_FILM_BASE = "INSERT INTO films(name, description, releaseDate, duration, mpaID) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String CREATE_FILM_GENRE = "INSERT INTO film_genre (filmID, genreID) VALUES (?, ?)";
    private static final String UPDATE_FILM_BASE =
            "UPDATE films SET name = ?, description = ?, releaseDate = ?, duration = ?, mpaID = ? WHERE id = ?";
    private static final String DELETE_FILM_GENRES = "DELETE FROM film_genre WHERE filmID = ?";
    private static final String UPDATE_FILM_GENRES = "INSERT INTO film_genre (filmID, genreID) VALUES (?, ?)";
    private static final String SELECT_FILM_BY_ID = "SELECT * FROM films WHERE id = ?";
    private static final String CREATE_FILM_LIKE = "INSERT INTO filmLikes (filmID, userID) VALUES (?, ?)";
    private static final String SELECT_FILM_LIKES_BY_ID = "SELECT userID FROM filmLikes WHERE filmID = ?";
    private static final String DELETE_FILM_LIKES_BY_USER_ID = "DELETE FROM filmLikes WHERE filmID = ? AND userID = ? ";

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("releaseDate").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .rate(resultSet.getInt("rate"))
                .mpa(mpaStorage.getFilmMpaById(resultSet.getString("mpaID")).get())
                .genres(genreStorage.getFilmGenreByFilmId(String.valueOf(resultSet.getInt("id"))))
                .likes(new HashSet<>(getFilmsLikes(resultSet.getString("id"))))
                .build();
    }

    @Override
    public List<Film> getAll() {
        return jdbcTemplate.query(SELECT_ALL_FILMS, this::mapRowToFilm);
    }

    @Override
    public Optional<Film> create(Film film) {
        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement statement = con.prepareStatement(CREATE_FILM_BASE, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, film.getName());
                statement.setString(2, film.getDescription());
                statement.setDate(3, Date.valueOf(film.getReleaseDate()));
                statement.setInt(4, film.getDuration());
                statement.setInt(5, film.getMpa().getId());
                return statement;
            }
        }, holder);

        String filmID = Objects.requireNonNull(holder.getKey()).toString();

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(CREATE_FILM_GENRE, filmID, genre.getId());
            }
        }
        if (film.getLikes() != null) {
            for (Integer userId : film.getLikes()) {
                addLike(filmID, userId.toString());
            }
        }
        return findFilmById(filmID);
    }

    @Override
    public Optional<Film> update(Film film) {
        jdbcTemplate.update(UPDATE_FILM_BASE,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        if (Stream.of(film.getGenres()).allMatch((object) -> object != null)) {
            jdbcTemplate.update(DELETE_FILM_GENRES, film.getId());
            Set<Genre> unicGenre = new HashSet<>(film.getGenres());
            List<Genre> unicListGenre = new ArrayList<>(unicGenre);
            Collections.reverse(unicListGenre);
            for (Genre genre : unicListGenre) {
                jdbcTemplate.update(UPDATE_FILM_GENRES, film.getId(), genre.getId());
            }
        } else {
            jdbcTemplate.update(DELETE_FILM_GENRES, film.getId());
        }
        return findFilmById(film.getId().toString());
    }

    @Override
    public Optional<Film> findFilmById(String id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_FILM_BY_ID, this::mapRowToFilm, id));
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Запрошенного фильма не существует");
        }
    }

    @Override
    public String addLike(String filmId, String userId) {
        jdbcTemplate.update(CREATE_FILM_LIKE, filmId, userId);
        Film updatedFilm = findFilmById(filmId).get();
        updatedFilm.setRate(getFilmsLikes(filmId).size());
        update(updatedFilm);
        return userId;
    }

    @Override
    public List<Integer> getFilmsLikes(String id) {
        try {
            SqlRowSet srs = jdbcTemplate.queryForRowSet(SELECT_FILM_LIKES_BY_ID, id);
            int rowCount = 0;
            List<Integer> filmsLikes = new ArrayList<>();
            while (srs.next()) {
                filmsLikes.add(srs.getInt("userID"));
                rowCount++;
            }
            return filmsLikes;
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Заданного фильма не существует");
        }
    }

    @Override
    public int removeLike(int filmId, int userId) {
        jdbcTemplate.update(DELETE_FILM_LIKES_BY_USER_ID, filmId, userId);
        return userId;
    }

    @Override
    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        return null;
    }
}
