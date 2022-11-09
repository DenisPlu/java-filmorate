package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.validator.ValidReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private Integer id;
    @NotBlank
    @NonNull
    private String name;
    @Size(max = 200)
    private String description;
    @ValidReleaseDate
    private LocalDate releaseDate;
    @Positive
    private int duration;
    @Positive
    private Integer likesNumber;
    private Genre genre;
    private MpaRate rate;
    private Set<Integer> likes = new HashSet<>();

    public Map<String, ?> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("releaseDate", releaseDate);
        values.put("description", description);
        values.put("duration", duration);
        values.put("likesNumber", likesNumber);
        return values;
    }
}
