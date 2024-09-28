package ru.tokmakov.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tokmakov.shareit.booking.dto.BookingDto;
import ru.tokmakov.shareit.booking.dto.BookingMapper;
import ru.tokmakov.shareit.booking.dto.BookingSaveDto;
import ru.tokmakov.shareit.booking.model.BookingState;
import ru.tokmakov.shareit.exception.booking.BookingAccessDeniedException;
import ru.tokmakov.shareit.exception.booking.BookingConflictException;
import ru.tokmakov.shareit.exception.booking.InvalidBookingPeriodException;
import ru.tokmakov.shareit.exception.item.ItemAccessDeniedException;
import ru.tokmakov.shareit.exception.item.ItemUnavailableException;
import ru.tokmakov.shareit.booking.model.Booking;
import ru.tokmakov.shareit.booking.model.BookingStatus;
import ru.tokmakov.shareit.booking.repository.BookingRepository;
import ru.tokmakov.shareit.exception.booking.BookingNotFoundException;
import ru.tokmakov.shareit.exception.item.ItemNotFoundException;
import ru.tokmakov.shareit.item.model.Item;
import ru.tokmakov.shareit.item.repository.ItemRepository;
import ru.tokmakov.shareit.exception.user.UserNotFoundException;
import ru.tokmakov.shareit.user.model.User;
import ru.tokmakov.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public Booking save(Long bookerId, BookingSaveDto bookingSaveDto) {
        User booker = userRepository.findById(bookerId).orElseThrow(
                () -> new UserNotFoundException("User with id " + bookerId + " not found"));

        Item item = itemRepository.findById(bookingSaveDto.getItemId()).orElseThrow(
                () -> new ItemNotFoundException(
                        "Item with id " + bookingSaveDto.getItemId() + " not found"));

        if (!item.getAvailable()) {
            throw new ItemUnavailableException("Item is not available");
        }

        if (bookingSaveDto.getEnd().isBefore(bookingSaveDto.getStart())) {
            throw new InvalidBookingPeriodException("End time cannot be before start time");
        }

        boolean hasConflictingBookings =
                bookingRepository.existsByItemAndPeriod(
                        item.getId(), bookingSaveDto.getStart(), bookingSaveDto.getEnd());
        if (hasConflictingBookings) {
            throw new BookingConflictException("Booking period conflicts with an existing booking");
        }

        Booking booking = new Booking(
                bookingSaveDto.getStart(),
                bookingSaveDto.getEnd(),
                item,
                booker,
                BookingStatus.WAITING
        );

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking responseToRequest(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new BookingNotFoundException("Booking with id " + bookingId + " not found"));

        if (booking.getItem().getOwner().getId() != userId) {
            throw new ItemAccessDeniedException(
                    "User with id " + userId + " is not the owner of the item with id " + booking.getItem().getId());
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new IllegalStateException("Booking is already processed: " + booking.getStatus());
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return booking;
    }

    @Override
    public BookingDto findBookingById(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new BookingNotFoundException("booking with id " + bookingId + " not found"));

        long bookerId = booking.getBooker().getId();
        long ownerId = booking.getItem().getOwner().getId();

        if (bookerId != userId && ownerId != userId) {
            throw new BookingAccessDeniedException(
                    "access denied for booking with id " + bookingId + " for user with id " + userId);
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<Booking> findAllForUser(long userId, BookingState state) {
        return switch (state) {
            case ALL -> bookingRepository.findAllByUserId(userId);
            case CURRENT -> bookingRepository.findCurrentByUserId(userId);
            case PAST -> bookingRepository.findPastByUserId(userId);
            case FUTURE -> bookingRepository.findFutureByUserId(userId);
            case WAITING -> bookingRepository.findWaitingByUserId(userId);
            case REJECTED -> bookingRepository.findRejectedByUserId(userId);
        };
    }

    @Override
    public List<Booking> findReservations(long userId, BookingState state) {
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllReservationsByUserId(userId);
            case CURRENT -> bookingRepository.findCurrentReservationsByUserId(userId);
            case PAST -> bookingRepository.findPastReservationsByUserId(userId);
            case FUTURE -> bookingRepository.findFutureReservationsByUserId(userId);
            case WAITING -> bookingRepository.findWaitingReservationsByUserId(userId);
            case REJECTED -> bookingRepository.findRejectedReservationsByUserId(userId);
        };

        return Optional.ofNullable(bookings)
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new BookingNotFoundException("No reservations found for user " + userId));
    }
}