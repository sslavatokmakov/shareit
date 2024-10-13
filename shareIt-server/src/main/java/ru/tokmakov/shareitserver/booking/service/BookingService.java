package ru.tokmakov.shareitserver.booking.service;

import ru.tokmakov.shareitserver.booking.dto.BookingDto;
import ru.tokmakov.shareitserver.booking.dto.BookingSaveDto;
import ru.tokmakov.shareitserver.booking.model.BookingState;
import ru.tokmakov.shareitserver.booking.model.Booking;
import ru.tokmakov.shareitserver.booking.dto.BookingSaveDto;
import ru.tokmakov.shareitserver.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    Booking save(Long userId, ru.tokmakov.shareitserver.booking.dto.BookingSaveDto bookingSaveDto);

    Booking responseToRequest(long userId, long bookingId, boolean approved);

    BookingDto findBookingById(long userId, long bookingId);

    List<Booking> findAllForUser(long userId, ru.tokmakov.shareitserver.booking.model.BookingState state);

    List<Booking> findReservations(long userId, ru.tokmakov.shareitserver.booking.model.BookingState state);
}
