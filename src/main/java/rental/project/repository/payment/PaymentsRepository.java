package rental.project.repository.payment;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rental.project.model.Payment;

public interface PaymentsRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p "
            + "JOIN p.booking b "
            + "WHERE b.id = :bookingId")
    Optional<Payment> findByBookingId(long bookingId);

    @Query("SELECT p FROM Payment p "
            + "JOIN p.booking b "
            + "WHERE p.sessionId = :sessionId")
    Optional<Payment> findBySessionId(String sessionId);

    @Query("SELECT p FROM Payment p "
            + "JOIN p.booking b "
            + "WHERE b.user.id = :userId")
    Page<Payment> findAllByUserId(@Param("userId")Long userId, Pageable pageable);

    @Query("SELECT p FROM Payment p "
            + "JOIN p.booking b "
            + "JOIN b.user u "
            + "WHERE p.status = :paymentStatus "
            + "AND (:userId IS NULL OR u.id = :userId)")
    List<Payment> findAllByStatus(Payment.PaymentStatus paymentStatus, Long userId);
}
