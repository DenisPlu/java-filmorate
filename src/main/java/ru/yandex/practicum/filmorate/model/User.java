package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import java.time.LocalDate;

@Data
@Builder
public class User {
    int id;
    @Email
    String email;
    String login;
    String name;
    LocalDate birthday;
}
