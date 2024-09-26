package ru.tokmakov.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.tokmakov.shareit.booking.model.Booking;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b " +
           "FROM Booking b JOIN b.booker u " +
           "WHERE u.id = ?1 " +
           "ORDER BY b.start DESC")
    List<Booking> findAllByUserId(long userId);

    @Query("SELECT b " +
           "FROM Booking b JOIN b.booker u " +
           "WHERE u.id = ?1 AND CURRENT TIMESTAMP BETWEEN b.start AND b.end " +
           "ORDER BY b.start DESC")
    List<Booking> findCurrentByUserId(long userId);

    @Query("SELECT b " +
           "FROM Booking b JOIN b.booker u " +
           "WHERE u.id = ?1 AND CURRENT TIMESTAMP > b.end " +
           "ORDER BY b.start DESC")
    List<Booking> findPastByUserId(long userId);

    @Query("SELECT b " +
           "FROM Booking b JOIN b.booker u " +
           "WHERE u.id = ?1 AND CURRENT TIMESTAMP < b.start " +
           "ORDER BY b.start DESC")
    List<Booking> findFutureByUserId(long userId);

    @Query("SELECT b " +
           "FROM Booking b JOIN b.booker u " +
           "WHERE u.id = ?1 AND b.status = 'WAITING' " +
           "ORDER BY b.start DESC")
    List<Booking> findWaitingByUserId(long userId);

    @Query("SELECT b " +
           "FROM Booking b JOIN b.booker u " +
           "WHERE u.id = ?1 AND b.status = 'REJECTED' " +
           "ORDER BY b.start DESC")
    List<Booking> findRejectedByUserId(long userId);


    @Query("SELECT b " +
           "FROM Booking b " +
           "WHERE b.item.owner.id = ?1 " +
           "ORDER BY b.start DESC")
    List<Booking> findAllReservationsByUserId(long userId);

    @Query("SELECT b " +
           "FROM Booking b " +
           "WHERE b.item.owner.id = ?1 AND CURRENT TIMESTAMP BETWEEN b.start AND b.end " +
           "ORDER BY b.start")
    List<Booking> findCurrentReservationsByUserId(long userId);

    @Query("SELECT b " +
           "FROM Booking b " +
           "WHERE b.item.owner.id = ?1 AND CURRENT TIMESTAMP > b.end " +
           "ORDER BY b.start")
    List<Booking> findPastReservationsByUserId(long userId);


    @Query("SELECT b " +
           "FROM Booking b " +
           "WHERE b.item.owner.id = ?1 AND CURRENT TIMESTAMP < b.start " +
           "ORDER BY b.start")
    List<Booking> findFutureReservationsByUserId(long userId);

    @Query("SELECT b " +
           "FROM Booking b " +
           "WHERE b.item.owner.id = ?1 AND b.status = 'WAITING' " +
           "ORDER BY b.start")
    List<Booking> findWaitingReservationsByUserId(long userId);

    @Query("SELECT b " +
           "FROM Booking b " +
           "WHERE b.item.owner.id = ?1 AND b.status = 'REJECTED' " +
           "ORDER BY b.start")
    List<Booking> findRejectedReservationsByUserId(long userId);
}