package rental.project.repository.booking;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rental.project.model.Booking;

public interface BookingsRepository
        extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b "
            + "JOIN b.user u "
            + "WHERE :userId IS NULL OR u.id = :userId "
            + "AND (:statuses IS NULL "
            + "OR b.status IN :statuses)")
    Page<Booking> findByUserIdAndStatus(Long userId,
                                        Booking.BookingStatus[] statuses,
                                        Pageable pageable);

    @Query("SELECT b FROM Booking b "
            + "JOIN b.user u "
            + "WHERE u.id = :userId")
    Page<Booking> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b "
            + "JOIN b.accommodation a "
            + "WHERE a.id = :accommodationId "
            + "AND b.checkinDate <= :second "
            + "AND b.checkoutDate >= :first ")
    List<Booking> findByDateRange(Long accommodationId,
                                  LocalDate first,
                                  LocalDate second);

    @Query("SELECT b FROM Booking b "
            + "WHERE b.checkoutDate < :date")
    List<Booking> findAfterDate(LocalDate date);
}
