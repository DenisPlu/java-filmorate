package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final InMemoryFilmStorage inMemoryFilmStorage;

    public int addLike(int filmId, int userId){
        inMemoryFilmStorage.getAll().get(filmId).getLikes().add(userId);
        inMemoryFilmStorage.getAll().get(filmId).setLikesNumber(inMemoryFilmStorage.getAll().get(filmId).getLikes().size());
        return userId;
    }

    public int removeLike(int filmId, int userId){
        inMemoryFilmStorage.getAll().get(filmId).getLikes().remove(userId);
        inMemoryFilmStorage.getAll().get(filmId).setLikesNumber(inMemoryFilmStorage.getAll().get(filmId).getLikes().size());
        return userId;
    }

    public List<Film> getFilmsWithBestLikes(int count){
        return inMemoryFilmStorage.getAll().stream()
                .sorted(Comparator.comparingInt(Film::getLikesNumber).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public InMemoryFilmStorage getInMemoryFilmStorage() {
        return inMemoryFilmStorage;
    }
}
