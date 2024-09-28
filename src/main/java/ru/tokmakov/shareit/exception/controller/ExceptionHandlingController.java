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

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e, WebRequest request) {
        log.error(e.getMessage(), e);
        ErrorDetails errorDetails = new ErrorDetails(e.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ItemUnavailableException.class)
    public ResponseEntity<?> handleItemUnavailableException(ItemUnavailableException e, WebRequest request) {
        log.error(e.getMessage(), e);
        ErrorDetails errorDetails = new ErrorDetails(e.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<?> handleBookingNotFoundException(BookingNotFoundException e, WebRequest request) {
        log.error(e.getMessage(), e);
        ErrorDetails errorDetails = new ErrorDetails(e.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<?> handleEmailAlreadyExistException(EmailAlreadyExistException e, WebRequest request) {
        log.error(e.getMessage(), e);
        ErrorDetails errorDetails = new ErrorDetails(e.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException e, WebRequest request) {
        log.error(e.getMessage(), e);
        ErrorDetails errorDetails = new ErrorDetails(e.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(BookingAccessDeniedException.class)
    public ResponseEntity<?> handleBookingAccessDeniedException(BookingAccessDeniedException e, WebRequest request) {
        log.error(e.getMessage(), e);
        ErrorDetails errorDetails = new ErrorDetails(e.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<?> handleItemNotFoundException(ItemNotFoundException e, WebRequest request) {
        log.error(e.getMessage(), e);
        ErrorDetails errorDetails = new ErrorDetails(e.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(BookingConflictException.class)
    public ResponseEntity<?> handleBookingConflictException(BookingConflictException e, WebRequest request) {
        log.error(e.getMessage(), e);
        ErrorDetails errorDetails = new ErrorDetails(e.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidBookingPeriodException.class)
    public ResponseEntity<?> handleInvalidBookingPeriodException(InvalidBookingPeriodException e, WebRequest request) {
        log.error(e.getMessage(), e);
        ErrorDetails errorDetails = new ErrorDetails(e.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}