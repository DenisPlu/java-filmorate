package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.validator.ValidReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private int id;
    @NotBlank
    @NonNull
    private String name;
    @Size(max = 200)
    private String description;
    @ValidReleaseDate
    private LocalDate releaseDate;
    @Positive
    private int duration;
}
