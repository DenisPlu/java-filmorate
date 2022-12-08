package ru.yandex.practicum.filmorate.daoImplStorage;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class MpaStorage implements MpaDao {
    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final JdbcTemplate jdbcTemplate;
    private static final String SELECT_ALL_MPA = "SELECT * FROM mpa";
    private static final String SELECT_MPA_BY_ID = "SELECT id, name FROM mpa WHERE id = ?";

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name")).build();
    }

    @Override
    public List<Mpa> getFilmsMpa() {
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList(SELECT_ALL_MPA);
            List<Mpa> listMpa = new ArrayList<>();
            for (Map<String, Object> map : result) {
                listMpa.add(Mpa.builder()
                        .id(Integer.parseInt(map.get("id").toString()))
                        .name((String) map.get("name")).build());
            }
            listMpa.sort(new Comparator<Mpa>() {
                public int compare(Mpa m1, Mpa m2) {
                    return Integer.compare(m1.getId(), m2.getId());
                }
            });
            return listMpa;

        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mpa в базе не существует");
        }
    }

    @Override
    public Optional<Mpa> getFilmMpaById(String id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_MPA_BY_ID, this::mapRowToMpa, id));
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Запрошенного mpa не существует");
        }
    }
}
