package ru.yandex.practicum.filmorate.daoImplStorage;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class GenreStorage implements GenreDao {
    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final JdbcTemplate jdbcTemplate;
    private static final String SELECT_ALL_GENRES = "SELECT * FROM genre";
    private static final String SELECT_GENRE_BY_ID = "SELECT * FROM genre WHERE genreId = ?";
    private static final String SELECT_FILM_GENRES_BY_FILM_ID = "SELECT * FROM film_genre " +
            "LEFT OUTER JOIN genre ON film_genre.genreId = genre.genreId" +
            " WHERE filmId = ?";

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genreId"))
                .name(resultSet.getString("name")).build();
    }

    @Override
    public List<Genre> getFilmsGenre() {
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList(SELECT_ALL_GENRES);
            List<Genre> listGenre = new ArrayList<>();
            for (Map<String, Object> map : result) {
                listGenre.add(Genre.builder()
                        .id(Integer.parseInt(map.get("genreId").toString()))
                        .name((String) map.get("name")).build());
            }
            listGenre.sort((g1, g2) -> Integer.compare(g1.getId(), g2.getId()));
            return listGenre;

        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre в базе  не существует");
        }
    }

    @Override
    public Optional<Genre> getFilmGenreById(String id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_GENRE_BY_ID, this::mapRowToGenre, id));
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Запрошенного genre не существует");
        }
    }

    @Override
    public List<Genre> getFilmGenreByFilmId(String id) {
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList(SELECT_FILM_GENRES_BY_FILM_ID, id);
            List<Genre> listGenre = new ArrayList<>();
            if (result.size() != 0) {
                for (Map<String, Object> map : result) {
                    listGenre.add(Genre.builder()
                            .id(Integer.parseInt(map.get("genreId").toString()))
                            .name((String) map.get("name")).build());
                }
            }
            return listGenre;
        } catch (EmptyResultDataAccessException e) {
            System.out.println("EmptyResultDataAccessException");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "У заданного фильма genre не существует");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("IndexOutOfBoundsException");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "У заданного фильма genre не существует");
        }
    }
}
