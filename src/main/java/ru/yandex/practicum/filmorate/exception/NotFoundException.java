package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends IOException {
    public NotFoundException(String s) {
        super(s);
    }
}
