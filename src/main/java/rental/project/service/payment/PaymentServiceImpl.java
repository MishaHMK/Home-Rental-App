package rental.project.service.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.net.MalformedURLException;
import java.net.URL;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.ServletEndpointManagementContextConfiguration;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rental.project.dto.booking.BookingWithAccommodationInfoDto;
import rental.project.dto.payment.CreatePaymentDto;
import rental.project.dto.payment.PaymentDto;
import rental.project.exception.PaymentException;
import rental.project.mapper.BookingMapper;
import rental.project.mapper.PaymentMapper;
import rental.project.model.Payment;
import rental.project.repository.payment.PaymentsRepository;
import rental.project.service.booking.BookingService;
import rental.project.stripe.StripeUtil;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final BookingService bookingService;
    private final StripeUtil stripeUtil;
    private final BookingMapper bookingMapper;
    private final PaymentMapper paymentMapper;
    private final PaymentsRepository paymentsRepository;

    public static final String SESSION_COMPLETE_STATUS = "complete";
    public static final String SESSION_OPEN_STATUS = "open";
    private final ServletEndpointManagementContextConfiguration servletEndpointManagementContextConfiguration;

    @Override
    public List<PaymentDto> getAllByUserId(Pageable pageable, Long userId) {
        return paymentsRepository.findAllByUserId(userId, pageable)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public PaymentDto save(CreatePaymentDto createPaymentDto) {
        Long bookingId = createPaymentDto.getBookingId();
        Optional<Payment> byBookingId = paymentsRepository.findByBookingId(bookingId);
        if (byBookingId.isPresent()) {
            throw new PaymentException("Payment already exists");
        }
        BookingWithAccommodationInfoDto bookingDetailsById =
                bookingService.getBookingDetailsById(bookingId);
        return createPayment(bookingId, bookingDetailsById);
    }

    @Override
    public PaymentDto success(String sessionId) {
        try {
            Session session = stripeUtil.receiveSession(sessionId);
            if (!session.getStatus().equals(SESSION_COMPLETE_STATUS)){
                throw new PaymentException("Payment with session id: " + sessionId
                        + " cancelled!");
            }
            Payment.PaymentStatus paidStatus = Payment.PaymentStatus.PAID;
            Payment payment = findBySessionId(sessionId);
            if (payment.getStatus().equals(paidStatus)){
                throw new PaymentException("Payment is already paid");
            }
            payment.setStatus(paidStatus);
            PaymentDto dto = paymentMapper.toDto(payment);
            return dto;
        } catch (StripeException e) {
            throw new PaymentException("Can't find payment session");
        }
    }

    @Override
    public PaymentDto cancel(String sessionId) {
        try {
            Session session = stripeUtil.receiveSession(sessionId);
            if (!session.getStatus().equals(SESSION_OPEN_STATUS)){
                throw new PaymentException("Payment with session id: " + sessionId
                        + " is not open!");
            }
            Payment payment = findBySessionId(sessionId);
            payment.setStatus(Payment.PaymentStatus.CANCELED);
            PaymentDto dto = paymentMapper.toDto(payment);
            return dto;
        } catch (StripeException e) {
            throw new PaymentException("Can't find payment session");
        }
    }

    private Payment findBySessionId(String sessionId) {
        return paymentsRepository.findBySessionId(sessionId)
                .orElseThrow(
                () -> new EntityNotFoundException("Payment with session id: "
                        + sessionId + " not found")
        );
    }

    private PaymentDto createPayment(
            Long bookingId, BookingWithAccommodationInfoDto bookingData) {
        BigDecimal totalAmount = bookingService.countTotalAmount(bookingId);
        Payment payment = new Payment();

        try {
            Session session = stripeUtil.createSession(totalAmount, "total amount");
            payment.setBooking(bookingMapper.toEntity(bookingData))
                    .setAmount(totalAmount)
                    .setStatus(Payment.PaymentStatus.PENDING)
                    .setSessionId(session.getId())
                    .setSessionUrl(new URL(session.getUrl()));

            PaymentDto paymentDto = paymentMapper.toDto(paymentsRepository.save(payment));
            return paymentDto;
        } catch (StripeException | MalformedURLException e) {
            throw new PaymentException("Can't create payment session");
        }
    }
}
