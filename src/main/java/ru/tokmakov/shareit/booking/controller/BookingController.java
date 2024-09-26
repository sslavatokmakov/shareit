package ru.tokmakov.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.tokmakov.shareit.booking.dto.BookingDto;
import ru.tokmakov.shareit.booking.dto.BookingSaveDto;
import ru.tokmakov.shareit.booking.model.BookingState;
import ru.tokmakov.shareit.booking.model.Booking;
import ru.tokmakov.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping
    public Booking save(@RequestHeader(X_SHARER_USER_ID) Long userId,
                        @RequestBody BookingSaveDto bookingSaveDto) {
        log.info("BookingController: save is called");
        Booking booking = bookingService.save(userId, bookingSaveDto);
        log.info("BookingController: booking save successfully");
        return booking;
    }

    @PatchMapping("{bookingId}")
    public Booking responseToRequest(@RequestHeader(X_SHARER_USER_ID) long userId,
                                     @PathVariable long bookingId,
                                     @RequestParam boolean approved) {
        log.info("BookingController: responseToRequest is called");
        Booking booking = bookingService.responseToRequest(userId, bookingId, approved);
        log.info("BookingController: responseToRequest successfully");
        return booking;
    }

    @GetMapping("{bookingId}")
    public BookingDto findBookingById(@RequestHeader(X_SHARER_USER_ID) long bookerId,
                                      @PathVariable long bookingId) {
        log.info("BookingController: findBookingById is called");
        BookingDto bookingDto = bookingService.findBookingById(bookerId, bookingId);
        log.info("BookingController: findBookingById successfully");
        return bookingDto;
    }

    @GetMapping
    public List<Booking> findAllForUser(@RequestHeader(X_SHARER_USER_ID) long userId,
                                        @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("BookingController: findAllForUser is called");
        List<Booking> bookingList = bookingService.findAllForUser(userId, state);
        log.info("BookingController: findAllForUser successfully");
        return bookingList;
    }

    @GetMapping("owner")
    public List<Booking> findReservations(@RequestHeader(X_SHARER_USER_ID) long userId,
                                          @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("BookingController: findReservations is called");
        List<Booking> reservationsList = bookingService.findReservations(userId, state);
        log.info("BookingController: findReservations successfully");
        return reservationsList;
    }
}