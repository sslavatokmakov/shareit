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
import ru.tokmakov.shareit.exception.item.ItemUnavailableException;
import ru.tokmakov.shareit.booking.model.Booking;
import ru.tokmakov.shareit.booking.model.BookingStatus;
import ru.tokmakov.shareit.booking.repository.BookingRepository;
import ru.tokmakov.shareit.exception.booking.BookingNotFoundException;
import ru.tokmakov.shareit.item.exception.AccessDeniedException;
import ru.tokmakov.shareit.item.exception.ItemNotFoundException;
import ru.tokmakov.shareit.item.model.Item;
import ru.tokmakov.shareit.item.repository.ItemRepository;
import ru.tokmakov.shareit.exception.user.UserNotFoundException;
import ru.tokmakov.shareit.user.model.User;
import ru.tokmakov.shareit.user.repository.UserRepository;

import java.util.List;

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
                () -> new UserNotFoundException("user with id " + bookerId + " not found"));
        Item item = itemRepository.findById(bookingSaveDto.getItemId()).orElseThrow(
                () -> new ItemNotFoundException("item with id " + bookingSaveDto.getItemId() + " not found"));
        if (!item.getAvailable()) {
            throw new ItemUnavailableException("item is not available");
        }

        Booking booking = new Booking(bookingSaveDto.getStart(), bookingSaveDto.getEnd(), item, booker, BookingStatus.WAITING);

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking responseToRequest(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new BookingNotFoundException("booking with id " + bookingId + " not found"));
        if (booking.getItem().getOwner().getId() != userId)
            throw new AccessDeniedException("user with id " + userId + " not a owner of item with id " + booking.getItem().getId());

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return bookingRepository.save(booking);
    }

    @Override
    public BookingDto findBookingById(long userId, long bookingId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new BookingNotFoundException("booking with id " + bookingId + " not found"));

        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new BookingAccessDeniedException("access denied for booking with id " + bookingId + "  for user with id " + bookingId);
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
        List<Booking> res = switch (state) {
            case ALL -> bookingRepository.findAllReservationsByUserId(userId);
            case CURRENT -> bookingRepository.findCurrentReservationsByUserId(userId);
            case PAST -> bookingRepository.findPastReservationsByUserId(userId);
            case FUTURE -> bookingRepository.findFutureReservationsByUserId(userId);
            case WAITING -> bookingRepository.findWaitingReservationsByUserId(userId);
            case REJECTED -> bookingRepository.findRejectedReservationsByUserId(userId);
        };

        if (res.isEmpty()) {
            throw new BookingNotFoundException("booking for user " + userId + " not found");
        }

        return res;
    }
}