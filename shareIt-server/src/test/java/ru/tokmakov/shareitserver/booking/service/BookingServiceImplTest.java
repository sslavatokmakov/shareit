package ru.tokmakov.shareitserver.booking.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.tokmakov.shareitserver.booking.dto.BookingDto;
import ru.tokmakov.shareitserver.booking.dto.BookingSaveDto;
import ru.tokmakov.shareitserver.booking.model.Booking;
import ru.tokmakov.shareitserver.booking.model.BookingState;
import ru.tokmakov.shareitserver.booking.model.BookingStatus;
import ru.tokmakov.shareitserver.booking.repository.BookingRepository;
import ru.tokmakov.shareitserver.exception.booking.BookingAccessDeniedException;
import ru.tokmakov.shareitserver.exception.booking.BookingConflictException;
import ru.tokmakov.shareitserver.exception.booking.BookingNotFoundException;
import ru.tokmakov.shareitserver.exception.booking.InvalidBookingPeriodException;
import ru.tokmakov.shareitserver.exception.item.ItemAccessDeniedException;
import ru.tokmakov.shareitserver.exception.item.ItemNotFoundException;
import ru.tokmakov.shareitserver.exception.item.ItemUnavailableException;
import ru.tokmakov.shareitserver.exception.user.UserNotFoundException;
import ru.tokmakov.shareitserver.item.model.Item;
import ru.tokmakov.shareitserver.item.repository.ItemRepository;
import ru.tokmakov.shareitserver.user.model.User;
import ru.tokmakov.shareitserver.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookingServiceImplTest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EntityManager em;

    private User booker;
    private Item availableItem;
    private Booking booking;
    private User itemOwner;

    @BeforeEach
    void setUp() {
        itemOwner = new User();
        itemOwner.setName("Item Owner");
        itemOwner.setEmail("owner@example.com");
        itemOwner = userRepository.save(itemOwner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        booker = userRepository.save(booker);

        availableItem = new Item();
        availableItem.setName("Available Item");
        availableItem.setDescription("An item that is available for booking");
        availableItem.setAvailable(true);
        availableItem.setOwner(itemOwner);
        availableItem = itemRepository.save(availableItem);

        booking = new Booking();
        booking.setItem(availableItem);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusDays(5));
        booking.setEnd(LocalDateTime.now().plusDays(6));
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);
    }


    @Test
    void testSaveBooking() {
        BookingSaveDto bookingSaveDto = new BookingSaveDto(
                availableItem.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        Booking savedBooking = bookingService.save(booker.getId(), bookingSaveDto);

        TypedQuery<Booking> query = em.createQuery("select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", savedBooking.getId()).getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(bookingSaveDto.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingSaveDto.getEnd()));
        assertThat(booking.getItem().getId(), equalTo(availableItem.getId()));
        assertThat(booking.getBooker().getId(), equalTo(booker.getId()));
        assertThat(booking.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void testSaveUserNotFound() {
        BookingSaveDto bookingSaveDto = new BookingSaveDto(
                availableItem.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        assertThrows(UserNotFoundException.class, () -> bookingService.save(999L, bookingSaveDto));
    }

    @Test
    void testSaveItemNotAvailable() {
        Item unavailableItem = new Item();
        unavailableItem.setName("Unavailable Item");
        unavailableItem.setDescription("An item that is not available for booking");
        unavailableItem.setAvailable(false);
        unavailableItem.setOwner(booker);
        unavailableItem = itemRepository.save(unavailableItem);

        BookingSaveDto bookingSaveDto = new BookingSaveDto(
                unavailableItem.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        assertThrows(ItemUnavailableException.class, () -> bookingService.save(booker.getId(), bookingSaveDto));
    }

    @Test
    void testSaveInvalidBookingPeriod() {
        BookingSaveDto bookingSaveDto = new BookingSaveDto(
                availableItem.getId(),
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1)
        );

        assertThrows(InvalidBookingPeriodException.class, () -> bookingService.save(booker.getId(), bookingSaveDto));
    }

    @Test
    void testSaveConflictingBooking() {
        BookingSaveDto bookingSaveDto1 = new BookingSaveDto(
                availableItem.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );
        bookingService.save(booker.getId(), bookingSaveDto1);

        BookingSaveDto bookingSaveDto2 = new BookingSaveDto(
                availableItem.getId(),
                LocalDateTime.now().plusDays(1).plusHours(1),
                LocalDateTime.now().plusDays(1).plusHours(3)
        );

        assertThrows(BookingConflictException.class, () -> bookingService.save(booker.getId(), bookingSaveDto2));
    }

    @Test
    void testSaveItemNotFound() {
        BookingSaveDto bookingSaveDto = new BookingSaveDto(
                999L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        assertThrows(ItemNotFoundException.class, () -> bookingService.save(booker.getId(), bookingSaveDto));
    }

    @Test
    void testResponseToRequestApprove() {
        Booking waitingBooking = new Booking();
        waitingBooking.setStart(LocalDateTime.now().plusDays(1));
        waitingBooking.setEnd(LocalDateTime.now().plusDays(2));
        waitingBooking.setItem(availableItem);
        waitingBooking.setBooker(booker);
        waitingBooking.setStatus(BookingStatus.WAITING);
        waitingBooking = bookingRepository.save(waitingBooking);

        Booking updatedBooking = bookingService.responseToRequest(itemOwner.getId(), waitingBooking.getId(), true);

        assertThat(updatedBooking).isNotNull();
        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(updatedBooking.getId()).isEqualTo(waitingBooking.getId());
    }


    @Test
    void testResponseToRequestReject() {
        Booking newBooking = new Booking();
        newBooking.setItem(availableItem);
        newBooking.setBooker(booker);
        newBooking.setStart(LocalDateTime.now().plusDays(1));
        newBooking.setEnd(LocalDateTime.now().plusDays(2));
        newBooking.setStatus(BookingStatus.WAITING);
        newBooking = bookingRepository.save(newBooking);

        Booking updatedBooking = bookingService.responseToRequest(itemOwner.getId(), newBooking.getId(), false);

        assertThat(updatedBooking).isNotNull();
        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.REJECTED);
        assertThat(updatedBooking.getId()).isEqualTo(newBooking.getId());
    }


    @Test
    void testResponseToRequestUserNotOwner() {
        User nonOwner = new User();
        nonOwner.setName("NonOwner");
        nonOwner.setEmail("nonowner@example.com");
        User savedNonOwner = userRepository.save(nonOwner);

        assertThrows(ItemAccessDeniedException.class, () -> bookingService.responseToRequest(savedNonOwner.getId(), booking.getId(), true));
    }

    @Test
    void testResponseToRequestAlreadyProcessed() {
        Booking updatedBooking = bookingService.responseToRequest(itemOwner.getId(), booking.getId(), true);

        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);

        assertThrows(IllegalStateException.class, () -> bookingService.responseToRequest(itemOwner.getId(), booking.getId(), false));
    }


    @Test
    void testResponseToRequestBookingNotFound() {
        assertThrows(BookingNotFoundException.class, () -> bookingService.responseToRequest(booker.getId(), 999L, true));
    }

    @Test
    void testFindBookingByIdAsBooker() {
        BookingDto bookingDto = bookingService.findBookingById(booker.getId(), booking.getId());

        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getItem().getId()).isEqualTo(availableItem.getId());
        assertThat(bookingDto.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void testFindBookingByIdAccessDenied() {
        User anotherUser = new User();
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@example.com");
        User savedUser = userRepository.save(anotherUser);

        assertThrows(BookingAccessDeniedException.class, () -> bookingService.findBookingById(savedUser.getId(), booking.getId()));
    }

    @Test
    void testFindBookingByIdNotFound() {
        long nonExistentBookingId = 999L;
        assertThrows(BookingNotFoundException.class, () -> bookingService.findBookingById(booker.getId(), nonExistentBookingId));
    }

    @Test
    void testFindAllBookingsForUserAll() {
        Booking pastBooking = new Booking();
        pastBooking.setStart(LocalDateTime.now().minusDays(3));
        pastBooking.setEnd(LocalDateTime.now().minusDays(2));
        pastBooking.setItem(availableItem);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(pastBooking);

        Booking currentBooking = new Booking();
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        currentBooking.setItem(availableItem);
        currentBooking.setBooker(booker);
        currentBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(currentBooking);

        Booking futureBooking = new Booking();
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        futureBooking.setItem(availableItem);
        futureBooking.setBooker(booker);
        futureBooking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(futureBooking);

        List<Booking> bookings = bookingService.findAllForUser(booker.getId(), BookingState.ALL);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(4);
    }

    @Test
    void testFindAllBookingsForUserCurrent() {
        Booking currentBooking = new Booking();
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        currentBooking.setItem(availableItem);
        currentBooking.setBooker(booker);
        currentBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(currentBooking);

        Booking pastBooking = new Booking();
        pastBooking.setStart(LocalDateTime.now().minusDays(3));
        pastBooking.setEnd(LocalDateTime.now().minusDays(2));
        pastBooking.setItem(availableItem);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(pastBooking);

        List<Booking> bookings = bookingService.findAllForUser(booker.getId(), BookingState.CURRENT);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getId()).isEqualTo(currentBooking.getId());
    }

    @Test
    void testFindAllBookingsForUserPast() {
        Booking pastBooking = new Booking();
        pastBooking.setStart(LocalDateTime.now().minusDays(3));
        pastBooking.setEnd(LocalDateTime.now().minusDays(2));
        pastBooking.setItem(availableItem);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(pastBooking);

        List<Booking> bookings = bookingService.findAllForUser(booker.getId(), BookingState.PAST);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getId()).isEqualTo(pastBooking.getId());
    }

    @Test
    void testFindAllBookingsForUserFuture() {
        List<Booking> bookings = bookingService.findAllForUser(booker.getId(), BookingState.FUTURE);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getId()).isEqualTo(booking.getId());
    }


    @Test
    void testFindAllBookingsForUserWaiting() {
        List<Booking> bookings = bookingService.findAllForUser(booker.getId(), BookingState.WAITING);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getId()).isEqualTo(booking.getId());
    }

    @Test
    void testFindAllBookingsForUserRejected() {
        Booking rejectedBooking = new Booking();
        rejectedBooking.setStart(LocalDateTime.now().plusDays(1));
        rejectedBooking.setEnd(LocalDateTime.now().plusDays(2));
        rejectedBooking.setItem(availableItem);
        rejectedBooking.setBooker(booker);
        rejectedBooking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(rejectedBooking);

        List<Booking> bookings = bookingService.findAllForUser(booker.getId(), BookingState.REJECTED);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getId()).isEqualTo(rejectedBooking.getId());
    }

    @Test
    void testFindAllReservations() {
        List<Booking> bookings = bookingService.findReservations(itemOwner.getId(), BookingState.ALL);
        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getId()).isEqualTo(booking.getId());
    }

    @Test
    void testFindCurrentReservations() {
        Booking newBooking = new Booking();

        newBooking.setItem(availableItem);
        newBooking.setBooker(booker);
        newBooking.setStart(LocalDateTime.now().minusDays(1));
        newBooking.setEnd(LocalDateTime.now().plusDays(1));
        newBooking.setStatus(BookingStatus.WAITING);
        newBooking = bookingRepository.save(newBooking);

        List<Booking> bookings = bookingService.findReservations(itemOwner.getId(), BookingState.CURRENT);
        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getId()).isEqualTo(newBooking.getId());
    }


    @Test
    void testFindPastReservations() {
        Booking pastBooking = new Booking();
        pastBooking.setItem(availableItem);
        pastBooking.setBooker(booker);
        pastBooking.setStart(LocalDateTime.now().minusDays(3));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        pastBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(pastBooking);

        List<Booking> bookings = bookingService.findReservations(itemOwner.getId(), BookingState.PAST);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getId()).isEqualTo(pastBooking.getId());
    }

    @Test
    void testFindFutureReservations() {
        List<Booking> bookings = bookingService.findReservations(itemOwner.getId(), BookingState.FUTURE);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getId()).isEqualTo(booking.getId());
    }

    @Test
    void testFindWaitingReservations() {
        List<Booking> bookings = bookingService.findReservations(itemOwner.getId(), BookingState.WAITING);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getId()).isEqualTo(booking.getId());
    }

    @Test
    void testFindRejectedReservations() {
        Booking rejectedBooking = new Booking();
        rejectedBooking.setItem(availableItem);
        rejectedBooking.setBooker(booker);
        rejectedBooking.setStart(LocalDateTime.now().plusDays(1));
        rejectedBooking.setEnd(LocalDateTime.now().plusDays(2));
        rejectedBooking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(rejectedBooking);

        List<Booking> bookings = bookingService.findReservations(itemOwner.getId(), BookingState.REJECTED);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getId()).isEqualTo(rejectedBooking.getId());
    }

    @Test
    void testFindReservationsNoBookingsFound() {
        long userId = booker.getId();
        BookingState state = BookingState.ALL;

        BookingNotFoundException exception = assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.findReservations(userId, state),
                "Expected findReservations to throw, but it didn't"
        );

        assertThat(exception.getMessage()).isEqualTo("No reservations found for user " + userId);
    }
}
