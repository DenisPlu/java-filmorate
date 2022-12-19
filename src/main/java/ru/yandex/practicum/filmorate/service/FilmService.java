package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.daoImplStorage.FilmDbStorage;
import ru.yandex.practicum.filmorate.daoImplStorage.GenreStorage;
import ru.yandex.practicum.filmorate.daoImplStorage.MpaStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
public class FilmService {
    private final FilmDbStorage filmStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    public List<Film> getFilmsWithBestLikes(int count) {
        System.out.println("count " + count);
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt(Film::getRate).reversed())
                .sorted(Comparator.comparingInt(Film::getId).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
