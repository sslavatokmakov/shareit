package ru.tokmakov.shareitserver.booking.dto;

import ru.tokmakov.shareitserver.booking.model.Booking;

public class BookingMapper {
    private BookingMapper() {
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getStart(), booking.getEnd(), booking.getItem(), booking.getBooker(), booking.getStatus());
    }
}
