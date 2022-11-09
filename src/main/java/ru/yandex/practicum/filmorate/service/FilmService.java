package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Qualifier("FilmDbStorage")
@RequiredArgsConstructor
public class FilmService {

    private final FilmDbStorage filmStorage;

    public int addLike(int filmId, int userId){
/*        filmStorage.getAll().get(filmId).getLikes().add(userId);
        filmStorage.getAll().get(filmId).setLikesNumber(filmStorage.getAll().get(filmId).getLikes().size());*/
        return userId;
    }

    public int removeLike(int filmId, int userId){
        filmStorage.getAll().get(filmId).getLikes().remove(userId);
        filmStorage.getAll().get(filmId).setLikesNumber(filmStorage.getAll().get(filmId).getLikes().size());
        return userId;
    }

    public List<Film> getFilmsWithBestLikes(int count){
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt(Film::getLikesNumber).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public FilmDbStorage getFilmStorage() {
        return filmStorage;
    }
}
