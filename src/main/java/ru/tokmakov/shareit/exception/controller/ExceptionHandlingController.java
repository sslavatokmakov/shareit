package ru.tokmakov.shareit.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import ru.tokmakov.shareit.exception.booking.BookingAccessDeniedException;
import ru.tokmakov.shareit.exception.booking.BookingConflictException;
import ru.tokmakov.shareit.exception.booking.BookingNotFoundException;
import ru.tokmakov.shareit.exception.booking.InvalidBookingPeriodException;
import ru.tokmakov.shareit.exception.item.ItemNotFoundException;
import ru.tokmakov.shareit.exception.item.ItemUnavailableException;
import ru.tokmakov.shareit.exception.model.ErrorDetails;
import ru.tokmakov.shareit.exception.user.EmailAlreadyExistException;
import ru.tokmakov.shareit.exception.user.UserNotFoundException;
import ru.tokmakov.shareit.item.exception.AccessDeniedException;

@Slf4j
@ControllerAdvice
public class ExceptionHandlingController {
    private ResponseEntity<ErrorDetails> handleException(Exception e, HttpStatus status, WebRequest request) {
        log.error(e.getMessage(), e);
        ErrorDetails errorDetails = new ErrorDetails(e.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, status);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({AccessDeniedException.class, BookingAccessDeniedException.class})
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e, WebRequest request) {
        return handleException(e, HttpStatus.FORBIDDEN, request);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ItemUnavailableException.class, InvalidBookingPeriodException.class})
    public ResponseEntity<?> handleItemUnavailableException(ItemUnavailableException e, WebRequest request) {
        return handleException(e, HttpStatus.BAD_REQUEST, request);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({EmailAlreadyExistException.class, BookingConflictException.class})
    public ResponseEntity<?> handleEmailAlreadyExistException(EmailAlreadyExistException e, WebRequest request) {
        return handleException(e, HttpStatus.CONFLICT, request);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class, BookingNotFoundException.class})
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException e, WebRequest request) {
        return handleException(e, HttpStatus.NOT_FOUND, request);
    }
}