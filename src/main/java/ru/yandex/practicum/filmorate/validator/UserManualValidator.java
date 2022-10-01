package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Optional;

//сервис ручной проверки входящих данных для класса User
public class UserManualValidator {

    public static boolean isValid(User user){
        boolean isEmailEmpty = user.getEmail().equals("") || !user.getEmail().contains("@");
        boolean isLoginEmpty = user.getLogin().equals("") || user.getLogin().contains(" ");
        boolean isBirthdayCorrect = user.getBirthday().isAfter(LocalDate.now());
        Optional<String> optionalEmail = Optional.ofNullable(user.getName());
        if (optionalEmail.isEmpty() || optionalEmail.get().equals("")) {
            user.setName(user.getLogin());
        }
        return !(isEmailEmpty || isLoginEmpty || isBirthdayCorrect);
    }
}
