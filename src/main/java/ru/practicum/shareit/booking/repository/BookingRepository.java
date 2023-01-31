package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    //byBooker
    @Query(value = "SELECT * " + //ALL
            "FROM bookings AS b " +
            "WHERE booker_id = ?1 " +
            "ORDER BY start_date DESC", nativeQuery = true)
    List<Booking> findAllByUser(Long userId);

    List<Booking> findByBooker_IdAndEndIsBeforeOrderByEndDesc(Long bookerId, LocalDateTime time); //PAST

    List<Booking> findByBooker_IdAndEndIsAfterOrderByEndDesc(Long bookerId, LocalDateTime time); //FUTURE

    List<Booking> findByBooker_IdAndStatusEquals(Long bookerId, BookingStatus status); //WAITING & REJECTED

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end); //CURRENT

    //byItem
    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime time); //PAST

    List<Booking> findAllByItem_Owner_IdAndEndIsAfterOrderByStartDesc(Long ownerId, LocalDateTime time); //FUTURE

    List<Booking> findAllByItem_Owner_IdAndStatusEqualsOrderByStartDesc(Long ownerId, BookingStatus status); //WAITING & REJECTED

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime start, LocalDateTime end); //CURRENT

    //for Last and Next Booking
    Booking findByItem_IdAndEndBeforeAndStatusNot(Long itemId, LocalDateTime end, BookingStatus status);

    Booking findByItem_IdAndStartAfterAndStatus(Long itemId, LocalDateTime start, BookingStatus status);
}
