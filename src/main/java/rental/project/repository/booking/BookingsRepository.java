package rental.project.repository.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rental.project.model.Booking;

public interface BookingsRepository
        extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b "
            + "JOIN b.user u "
            + "WHERE u.id = :userId "
            + "AND (:statuses IS NULL "
            + "OR b.status IN :statuses)")
    Page<Booking> findByUserIdAndStatus(Long userId,
                                        Booking.BookingStatus[] statuses,
                                        Pageable pageable);

    @Query("SELECT b FROM Booking b "
            + "JOIN b.user u "
            + "WHERE u.id = :userId")
    Page<Booking> findByUserId(Long userId, Pageable pageable);
}
