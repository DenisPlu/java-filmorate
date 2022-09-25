package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data@NoArgsConstructor@AllArgsConstructor
public class Film {
    int id;
    @NonNull@NotBlank
    String name;
    String description;
    LocalDate releaseDate;
    @Positive
    int duration;

    public Film(@NonNull String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
