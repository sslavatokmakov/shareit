package ru.tokmakov.shareit.booking.service;

import ru.tokmakov.shareit.booking.dto.BookingDto;
import ru.tokmakov.shareit.booking.dto.BookingSaveDto;
import ru.tokmakov.shareit.booking.model.BookingState;
import ru.tokmakov.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking save(Long userId, BookingSaveDto bookingSaveDto);

    Booking responseToRequest(long userId, long bookingId, boolean approved);

    BookingDto findBookingById(long userId, long bookingId);

    List<Booking> findAllForUser(long userId, BookingState state);

    List<Booking> findReservations(long userId, BookingState state);
}
