package rental.project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stripe.exception.ApiException;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import rental.project.dto.booking.BookingWithAccommodationInfoDto;
import rental.project.dto.payment.CreatePaymentDto;
import rental.project.dto.payment.PaymentDto;
import rental.project.exception.PaymentException;
import rental.project.mapper.BookingMapper;
import rental.project.mapper.PaymentMapper;
import rental.project.model.Booking;
import rental.project.model.Payment;
import rental.project.repository.booking.BookingsRepository;
import rental.project.repository.payment.PaymentsRepository;
import rental.project.service.booking.BookingService;
import rental.project.service.notificaiton.NotificationService;
import rental.project.service.payment.PaymentServiceImpl;
import rental.project.stripe.StripeUtil;
import rental.project.supplier.BookingSupplier;
import rental.project.supplier.PaymentSupplier;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTests {
    @Mock
    private BookingService bookingService;
    @Mock
    private StripeUtil stripeUtil;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private PaymentsRepository paymentsRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private BookingsRepository bookingsRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    @DisplayName("Verify correct booking dto")
    public void getAllByUserId_ShouldReturnPaymentDtoList() {
        //Given (Arrange)
        List<PaymentDto> paymentDtoList = PaymentSupplier.getPaymentDtoList();
        List<Payment> paymentList = PaymentSupplier.getPaymentList();
        Page<Payment> payments = new PageImpl<>(PaymentSupplier.getPaymentList());
        Pageable pageable = PageRequest.of(0, 2);
        long userId = 1L;

        when(paymentsRepository.findAllByUserId(userId, pageable))
                .thenReturn(payments);
        when(paymentMapper.toDto(any(Payment.class)))
                .thenReturn(paymentDtoList.get(0));

        //When (Act)
        List<PaymentDto> expected = paymentService.getAllByUserId(
                pageable, userId);

        //Then (Assert)
        assertEquals(expected, paymentDtoList);
    }

    @Test
    @DisplayName("Verify exception thrown with pre existing booking")
    public void save_WithExistingLinkedBooking_ThrowsException() {
        //Given (Arrange)
        CreatePaymentDto createDto = PaymentSupplier.getCreatePaymentDto();
        Long bookingId = createDto.getBookingId();
        Payment payment = PaymentSupplier.getPayment();

        when(paymentsRepository.findByBookingId(bookingId))
                .thenReturn(Optional.of(payment));

        //When (Act)
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.save(createDto));

        //Then (Assert)
        String expectedMessage = "Payment already exists";
        assertEquals(exception.getMessage(), expectedMessage);
        verify(paymentsRepository).findByBookingId(bookingId);
    }

    @Test
    @DisplayName("Verify correct payment dto returned with new booking")
    public void save_WithCorrectBooking_ThrowsException()
            throws StripeException {
        //Given (Arrange)
        CreatePaymentDto createDto = PaymentSupplier.getCreatePaymentDto();
        Long bookingId = createDto.getBookingId();
        Payment payment = PaymentSupplier.getPayment();
        PaymentDto paymentDto = PaymentSupplier.getPaymentDto();
        Booking booking = BookingSupplier.getBooking();
        Session session = PaymentSupplier.getSession();
        BookingWithAccommodationInfoDto bookingDetailsById =
                BookingSupplier.getBookingWithAccommodationInfoDto();
        BigDecimal totalAmount = new BigDecimal(550);
        String sessionName = "payment";

        when(paymentsRepository.findByBookingId(bookingId))
                .thenReturn(Optional.empty());
        when(bookingService.getBookingDetailsById(bookingId))
                .thenReturn(bookingDetailsById);
        when(bookingService.countTotalAmount(bookingId))
                .thenReturn(totalAmount);
        when(stripeUtil.createSession(totalAmount, sessionName))
                    .thenReturn(session);
        when(bookingMapper.toEntity(bookingDetailsById))
                .thenReturn(booking);
        when(bookingsRepository.save(booking))
                .thenReturn(booking);
        when(paymentMapper.toDto(payment))
                .thenReturn(paymentDto);
        when(paymentsRepository.save(any(Payment.class)))
                .thenReturn(payment);

        //When (Act)
        PaymentDto actual = paymentService.save(createDto);

        //Then (Assert)
        assertNotNull(actual);
        assertEquals(actual, paymentDto);
        verify(paymentsRepository).findByBookingId(bookingId);
        verify(bookingService).getBookingDetailsById(bookingId);
        verify(bookingService).countTotalAmount(bookingId);
        verify(stripeUtil).createSession(totalAmount, sessionName);
        verify(bookingMapper).toEntity(bookingDetailsById);
        verify(bookingsRepository).save(booking);
        verify(paymentMapper).toDto(payment);
        verify(paymentsRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("Verify correct payment dto returned with correct data")
    public void save_WithWrongUrl_ThrowsException()
            throws StripeException {
        //Given (Arrange)
        CreatePaymentDto createDto = PaymentSupplier.getCreatePaymentDto();
        Long bookingId = createDto.getBookingId();
        Booking booking = BookingSupplier.getBooking();
        Session session = PaymentSupplier.getWrongUrlSession();
        BookingWithAccommodationInfoDto bookingDetailsById =
                BookingSupplier.getBookingWithAccommodationInfoDto();
        BigDecimal totalAmount = new BigDecimal(550);
        String sessionName = "payment";

        when(paymentsRepository.findByBookingId(bookingId))
                .thenReturn(Optional.empty());
        when(bookingService.getBookingDetailsById(bookingId))
                .thenReturn(bookingDetailsById);
        when(bookingService.countTotalAmount(bookingId))
                .thenReturn(totalAmount);
        when(stripeUtil.createSession(totalAmount, sessionName))
                .thenReturn(session);
        when(bookingMapper.toEntity(bookingDetailsById))
                .thenReturn(booking);
        when(bookingsRepository.save(booking))
                .thenReturn(booking);

        //When (Act)
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.save(createDto));

        //Then (Assert)
        String expectedMessage = "Url format is wrong";
        assertEquals(expectedMessage, exception.getMessage());
        verify(paymentsRepository).findByBookingId(bookingId);
        verify(bookingService).getBookingDetailsById(bookingId);
        verify(bookingService).countTotalAmount(bookingId);
        verify(stripeUtil).createSession(totalAmount, sessionName);
        verify(bookingMapper).toEntity(bookingDetailsById);
        verify(bookingsRepository).save(booking);
    }

    @Test
    @DisplayName("Verify correct payment dto returned with correct data")
    public void save_WithWrongSessionData_ThrowsException()
            throws StripeException {
        //Given (Arrange)
        CreatePaymentDto createDto = PaymentSupplier.getCreatePaymentDto();
        Long bookingId = createDto.getBookingId();
        BookingWithAccommodationInfoDto bookingDetailsById =
                BookingSupplier.getBookingWithAccommodationInfoDto();
        BigDecimal totalAmount = new BigDecimal(550);

        when(paymentsRepository.findByBookingId(bookingId))
                .thenReturn(Optional.empty());
        when(bookingService.getBookingDetailsById(bookingId))
                .thenReturn(bookingDetailsById);
        when(bookingService.countTotalAmount(bookingId))
                .thenReturn(totalAmount);

        String sessionName = "payment";
        when(stripeUtil.createSession(any(BigDecimal.class), anyString()))
                 .thenThrow(new ApiException(null,
                         null, null, null, null
                 ));

        //When (Act)
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.save(createDto));

        //Then (Assert)
        String expectedMessage = "Can't create payment session";
        assertEquals(expectedMessage, exception.getMessage());
        verify(paymentsRepository).findByBookingId(bookingId);
        verify(bookingService).getBookingDetailsById(bookingId);
        verify(bookingService).countTotalAmount(bookingId);
        verify(stripeUtil).createSession(totalAmount, sessionName);
    }

    @Test
    @DisplayName("Verify correct payment dto returned with correct data")
    public void success_WithCorrectData_ReturnsDto()
            throws StripeException {
        //Given (Arrange)
        Session session = PaymentSupplier.getSession();
        String sessionId = session.getId();
        Payment payment = PaymentSupplier.getPayment();
        PaymentDto paymentDto = PaymentSupplier.getPaymentDto();

        when(paymentsRepository.findBySessionId(sessionId))
                .thenReturn(Optional.of(payment));;
        when(paymentMapper.toDto(payment))
                .thenReturn(paymentDto);
        when(stripeUtil.receiveSession(sessionId))
                .thenReturn(session);

        //When (Act)
        PaymentDto actual = paymentService.success(sessionId);

        //Then (Assert)
        assertEquals(paymentDto, actual);
        verify(paymentsRepository).findBySessionId(sessionId);
        verify(paymentMapper).toDto(any(Payment.class));
        verify(stripeUtil).receiveSession(sessionId);
    }

    @Test
    @DisplayName("Verify exception thrown using cancelled payment")
    public void success_WithCanceledPayment_ThrowsException()
            throws StripeException {
        //Given (Arrange)
        Session session = PaymentSupplier.getSession();
        String sessionId = session.getId();
        Payment payment = PaymentSupplier.getCancelledPayment();

        when(paymentsRepository.findBySessionId(sessionId))
                .thenReturn(Optional.of(payment));;

        //When (Act)
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.success(sessionId));

        //Then (Assert)
        String exceptionMessage = "Payment is already cancelled";
        assertEquals(exceptionMessage, exception.getMessage());
        verify(paymentsRepository).findBySessionId(sessionId);
    }

    @Test
    @DisplayName("Verify exception thrown using unpaid payment")
    public void success_WithUnpaidPayment_ThrowsException()
            throws StripeException {
        //Given (Arrange)
        Session session = PaymentSupplier.getOpenSession();
        String sessionId = session.getId();
        Payment payment = PaymentSupplier.getPayment();

        when(paymentsRepository.findBySessionId(sessionId))
                .thenReturn(Optional.of(payment));
        when(stripeUtil.receiveSession(sessionId))
                .thenReturn(session);

        //When (Act)
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.success(sessionId));

        //Then (Assert)
        String exceptionMessage = "Payment with session id: " + sessionId
                + " is not paid";
        assertEquals(exceptionMessage, exception.getMessage());
        verify(paymentsRepository).findBySessionId(sessionId);
    }

    @Test
    @DisplayName("Verify exception thrown using not found session")
    public void success_WithNotFoundSession_ThrowsException()
            throws StripeException {
        //Given (Arrange)
        Session session = PaymentSupplier.getOpenSession();
        String sessionId = session.getId();
        Payment payment = PaymentSupplier.getPayment();

        when(paymentsRepository.findBySessionId(sessionId))
                .thenReturn(Optional.of(payment));
        when(stripeUtil.receiveSession(sessionId))
                .thenThrow(new ApiException(null,
                        null, null, null, null
                ));

        //When (Act)
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.success(sessionId));

        //Then (Assert)
        String exceptionMessage = "Can't find payment session";
        assertEquals(exceptionMessage, exception.getMessage());
        verify(paymentsRepository).findBySessionId(sessionId);
        verify(stripeUtil).receiveSession(sessionId);
    }

    @Test
    @DisplayName("Verify correct payment dto using unpaid payment")
    public void cancel_WithValidData_ReturnsPaymentDto()
            throws StripeException {
        //Given (Arrange)
        Session session = PaymentSupplier.getOpenSession();
        String sessionId = session.getId();
        Payment payment = PaymentSupplier.getPayment();
        PaymentDto paymentDto = PaymentSupplier.getPaymentDto();

        when(paymentsRepository.findBySessionId(sessionId))
                .thenReturn(Optional.of(payment));
        when(stripeUtil.receiveSession(sessionId))
                .thenReturn(session);
        when(paymentMapper.toDto(payment))
                .thenReturn(paymentDto);

        //When (Act)
        PaymentDto actual = paymentService.cancel(sessionId);

        //Then (Assert)
        assertEquals(paymentDto, actual);
        verify(paymentsRepository).findBySessionId(sessionId);
        verify(stripeUtil).receiveSession(sessionId);
        verify(paymentMapper).toDto(payment);
    }

    @Test
    @DisplayName("Verify exception thrown using closed session")
    public void cancel_WithClosedSession_ThrowsException()
            throws StripeException {
        //Given (Arrange)
        Session session = PaymentSupplier.getSession();
        String sessionId = session.getId();

        when(stripeUtil.receiveSession(sessionId))
                .thenReturn(session);

        //When (Act)
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.cancel(sessionId));

        //Then (Assert)
        String exceptionMessage = "Payment with session id: " + sessionId
                + " is not open!";
        assertEquals(exceptionMessage, exception.getMessage());
        verify(stripeUtil).receiveSession(sessionId);
    }

    @Test
    @DisplayName("Verify exception thrown with not found session")
    public void cancel_WithNotFoundSession_ThrowsException()
            throws StripeException {
        //Given (Arrange)
        Session session = PaymentSupplier.getSession();
        String sessionId = session.getId();

        when(stripeUtil.receiveSession(sessionId))
                .thenThrow(new ApiException(null,
                        null, null, null, null
                ));

        //When (Act)
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.cancel(sessionId));

        //Then (Assert)
        String exceptionMessage = "Can't find payment session";
        assertEquals(exceptionMessage, exception.getMessage());
        verify(stripeUtil).receiveSession(sessionId);
    }

    @Test
    @DisplayName("Verify correct payment dto returned")
    public void renew_WithNotFoundSession_ReturnsPaymentDto()
            throws StripeException {
        //Given (Arrange)
        Session session = PaymentSupplier.getSession();
        Payment payment = PaymentSupplier.getPayment();
        Long paymentId = payment.getId();
        BigDecimal totalAmount = new BigDecimal("125.5");
        PaymentDto paymentDto = PaymentSupplier.getPaymentDto();

        when(paymentsRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));
        when(stripeUtil.createSession(totalAmount, "payment"))
                .thenReturn(session);
        when(paymentMapper.toDto(payment))
                .thenReturn(paymentDto);
        when(paymentsRepository.save(any(Payment.class)))
                .thenReturn(payment);

        //When (Act)
        PaymentDto actual = paymentService.renew(paymentId);

        //Then (Assert)
        assertEquals(paymentDto, actual);
    }
}

