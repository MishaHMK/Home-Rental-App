package rental.project.repository.payment;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rental.project.model.Payment;

public interface PaymentsRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p "
            + "WHERE p.booking.id = :bookingId")
    Optional<Payment> findByBookingId(long bookingId);

    @Query("SELECT p FROM Payment p "
            + "WHERE p.sessionId = :sessionId")
    Optional<Payment> findBySessionId(String sessionId);

    @Query("SELECT p FROM Payment p "
            + "WHERE p.booking.user.id = :userId")
    Page<Payment> findAllByUserId(long userId, Pageable pageable);
}
