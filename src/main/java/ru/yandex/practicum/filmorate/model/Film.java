package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.validator.ValidReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    int id;
    @NotBlank
    @NonNull
    String name;
    @Size(max = 200)
    String description;
    @ValidReleaseDate
    LocalDate releaseDate;
    @Positive
    int duration;
}
