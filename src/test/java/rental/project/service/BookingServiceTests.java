package rental.project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import rental.project.dto.booking.BookingDto;
import rental.project.dto.booking.BookingWithAccommodationInfoDto;
import rental.project.dto.booking.CreateBookingDto;
import rental.project.dto.booking.UpdateBookingDto;
import rental.project.dto.booking.UpdateBookingStatusDto;
import rental.project.exception.AccessException;
import rental.project.exception.BookingException;
import rental.project.mapper.BookingMapper;
import rental.project.model.Accommodation;
import rental.project.model.Booking;
import rental.project.model.Booking.BookingStatus;
import rental.project.model.Payment;
import rental.project.model.User;
import rental.project.notification.NotificationService;
import rental.project.repository.accommodation.AccommodationRepository;
import rental.project.repository.booking.BookingsRepository;
import rental.project.repository.payment.PaymentsRepository;
import rental.project.service.booking.BookingServiceImpl;
import rental.project.supplier.AccommodationSupplier;
import rental.project.supplier.BookingSupplier;
import rental.project.supplier.PaymentSupplier;
import rental.project.supplier.UserSupplier;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTests {
    @Mock
    private BookingsRepository bookingsRepository;
    @Mock
    private AccommodationRepository accommodationRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private PaymentsRepository paymentsRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    @DisplayName("Verify correct booking dto")
    public void save_WithValidBookingData_ShouldReturnBookingDto() {
        //Given (Arrange)
        User user = UserSupplier.getUser();
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        Accommodation accommodation = AccommodationSupplier.getAccommodation();
        CreateBookingDto createBookingDto = BookingSupplier.getBookingCreateDto();
        BookingDto bookingDto = BookingSupplier.getBookingDto();
        Booking booking = BookingSupplier.getBooking();

        when(authentication.getPrincipal()).thenReturn(user);
        when(accommodationRepository.findById(createBookingDto.getAccommodationId()))
                .thenReturn(Optional.of(accommodation));
        when(bookingMapper.toEntity(createBookingDto)).thenReturn(booking);
        when(bookingsRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        //When (Act)
        BookingDto actual = bookingService.save(createBookingDto);

        //Then (Assert)
        assertNotNull(actual);
        assertEquals(actual, bookingDto);

        verify(accommodationRepository).findById(createBookingDto.getAccommodationId());
        verify(bookingMapper).toEntity(createBookingDto);
        verify(bookingsRepository).save(booking);
        verify(bookingMapper).toDto(booking);
    }

    @Test
    @DisplayName("Verify exception thrown with existing pending payment")
    public void save_WithExistingPendingPayment_ThrowsException() {
        //Given (Arrange)
        User user = UserSupplier.getUser();
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        CreateBookingDto createBookingDto = BookingSupplier.getBookingCreateDto();
        Long loggedInUserId = user.getId();
        List<Payment> payments = PaymentSupplier.getPaymentList();

        when(authentication.getPrincipal()).thenReturn(user);
        when(paymentsRepository.findAllByStatus(Payment.PaymentStatus.PENDING, loggedInUserId))
                .thenReturn(payments);

        //When (Act)
        AccessException exception = assertThrows(AccessException.class,
                () -> bookingService.save(createBookingDto));

        //Then (Assert)
        String expectedMessage = "You have already pending payment to pay first";
        assertEquals(expectedMessage, exception.getMessage());

        verify(paymentsRepository)
                .findAllByStatus(Payment.PaymentStatus.PENDING, loggedInUserId);
    }

    @Test
    @DisplayName("Verify exception thrown with unavailable accommodation")
    public void save_WithUnavailableAccommodation_ThrowsException() {
        //Given (Arrange)
        User user = UserSupplier.getUser();
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        CreateBookingDto createBookingDto = BookingSupplier.getBookingCreateDto();
        Booking booking = BookingSupplier.getBooking();
        Accommodation accommodation = AccommodationSupplier.getAccommodation();
        List<Booking> bookings = BookingSupplier.getBookingList();

        when(authentication.getPrincipal()).thenReturn(user);
        when(accommodationRepository.findById(createBookingDto.getAccommodationId()))
                .thenReturn(Optional.of(accommodation));
        when(bookingsRepository.findByDateRange(accommodation.getId(),
                booking.getCheckinDate(), booking.getCheckoutDate()))
                .thenReturn(bookings);

        //When (Act)
        AccessException exception = assertThrows(AccessException.class,
                () -> bookingService.save(createBookingDto));

        //Then (Assert)
        String expectedMessage = "This accommodation is not available.";
        assertEquals(expectedMessage, exception.getMessage());

        verify(accommodationRepository).findById(createBookingDto.getAccommodationId());
        verify(bookingsRepository).findByDateRange(accommodation.getId(),
                booking.getCheckinDate(), booking.getCheckoutDate());
    }

    @Test
    @DisplayName("Verify exception thrown with not found accommodation")
    public void save_WithNullAccommodation_ThrowsException() {
        //Given (Arrange)
        User user = UserSupplier.getUser();
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        CreateBookingDto createBookingDto = BookingSupplier.getBookingCreateDto();
        Long wrongId = 1L;

        when(authentication.getPrincipal()).thenReturn(user);
        when(accommodationRepository.findById(wrongId))
                .thenReturn(Optional.empty());

        //When (Act)
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.save(createBookingDto));

        //Then (Assert)
        String expectedMessage = "Accommodation with id "
                + wrongId + " not found";
        assertEquals(expectedMessage, exception.getMessage());

        verify(accommodationRepository).findById(wrongId);
    }

    @Test
    @DisplayName("Verify correct booking dto list returned")
    public void findByUserIdAndStatus_WithValidData_ShouldReturnBookingDtoList() {
        //Given (Arrange)
        BookingStatus[] statuses = {BookingStatus.PENDING};
        Long userId = UserSupplier.getUser().getId();
        Pageable pageable = PageRequest.of(0, 2);
        List<Booking> bookings = BookingSupplier.getBookingList();
        Page<Booking> bookingPage = new PageImpl<>(bookings);
        List<BookingDto> bookingDtoList = BookingSupplier.getBookingDtoList();

        when(bookingsRepository.findByUserIdAndStatus(userId, statuses, pageable))
                .thenReturn(bookingPage);
        when(bookingMapper.toDto(bookings.get(0))).thenReturn(bookingDtoList.get(0));
        when(bookingMapper.toDto(bookings.get(1))).thenReturn(bookingDtoList.get(1));

        //When (Act)
        List<BookingDto> actual = bookingService.findByUserIdAndStatus(pageable, userId, statuses);

        //Then (Assert)
        assertEquals(actual, bookingDtoList);

        verify(bookingsRepository).findByUserIdAndStatus(userId, statuses, pageable);
        verify(bookingMapper, times(bookings.size())).toDto(any(Booking.class));
    }

    @Test
    @DisplayName("Verify correct booking dto list returned")
    public void getAllMyBookings_ShouldReturnBookingDtoList() {
        //Given (Arrange)
        Long userId = UserSupplier.getUser().getId();
        Pageable pageable = PageRequest.of(0, 2);
        List<Booking> bookings = BookingSupplier.getBookingList();
        Page<Booking> bookingPage = new PageImpl<>(bookings);
        List<BookingDto> bookingDtoList = BookingSupplier.getBookingDtoList();

        when(bookingsRepository.findByUserId(userId, pageable))
                .thenReturn(bookingPage);
        when(bookingMapper.toDto(bookings.get(0))).thenReturn(bookingDtoList.get(0));
        when(bookingMapper.toDto(bookings.get(1))).thenReturn(bookingDtoList.get(1));

        //When (Act)
        List<BookingDto> actual = bookingService.getAllMyBookings(pageable);

        //Then (Assert)
        assertEquals(actual, bookingDtoList);

        verify(bookingsRepository).findByUserId(userId, pageable);
        verify(bookingMapper, times(bookings.size())).toDto(any(Booking.class));
    }

    @Test
    @DisplayName("Verify correct detailed booking dto returned")
    public void getBookingDetailsById_ForAdminUser_ThrowsException() {
        //Given (Arrange)
        User user = UserSupplier.getUser();
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        BookingWithAccommodationInfoDto bookingWithInfoDto =
                BookingSupplier.getBookingWithAccommodationInfoDto();
        Booking booking = BookingSupplier.getBookingOfCustomer();
        SecurityContextHolder.setContext(securityContext);

        when(bookingsRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        when(bookingMapper.toDetailedDto(booking)).thenReturn(bookingWithInfoDto);

        //When (Act)
        BookingWithAccommodationInfoDto actual = bookingService
                .getBookingDetailsById(booking.getId());

        //Then (Assert)
        assertEquals(actual, bookingWithInfoDto);

        verify(bookingsRepository).findById(booking.getId());
        verify(bookingMapper).toDetailedDto(booking);
    }

    @Test
    @DisplayName("Verify exception thrown for wrong user")
    public void getBookingDetailsById_ForWrongUser_ThrowsException() {
        //Given (Arrange)
        User customerUser = UserSupplier.getCustomerUser();
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(customerUser);
        SecurityContextHolder.setContext(securityContext);

        Booking booking = BookingSupplier.getBooking();
        when(bookingsRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        //When (Act)
        AccessException exception = assertThrows(AccessException.class,
                () -> bookingService.getBookingDetailsById(booking.getId())
        );

        //Then (Assert)
        String expectedMessage = "You can't access this booking data";
        assertEquals(exception.getMessage(), expectedMessage);

        verify(bookingsRepository).findById(booking.getId());
    }

    @Test
    @DisplayName("Verify correct dto returned using correct data")
    public void setBookingStatusCancelled_WithValidData_ReturnsDto() {
        //Given (Arrange)
        User user = UserSupplier.getUser();
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        Booking booking = BookingSupplier.getBooking();
        BookingDto bookingDto = BookingSupplier.getBookingDto();

        when(bookingsRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking))
                .thenReturn(bookingDto);
        when(bookingsRepository.save(booking))
                .thenReturn(booking);

        bookingDto.setStatus("CANCELED");

        //When (Act)
        BookingDto actual = bookingService.setBookingStatusCancelled(booking.getId());

        //Then (Assert)
        assertEquals(actual, bookingDto);

        verify(bookingsRepository).findById(booking.getId());
    }

    @Test
    @DisplayName("Verify exception thrown for expired booking")
    public void setBookingStatusCancelled_WithExpiredBooking_ThrowsException() {
        //Given (Arrange)
        User user = UserSupplier.getUser();
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        Booking booking = BookingSupplier.getExpiredBooking();
        when(bookingsRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        //When (Act)
        BookingException exception = assertThrows(BookingException.class,
                () -> bookingService.setBookingStatusCancelled(booking.getId()));

        //Then (Assert)
        String expectedMessage = "You can't cancel expired booking";
        assertEquals(expectedMessage, exception.getMessage());

        verify(bookingsRepository).findById(booking.getId());
    }

    @Test
    @DisplayName("Verify exception thrown if booking is not accessible")
    public void setBookingStatusCancelled_WithWrongAccess_ThrowsException() {
        //Given (Arrange)
        User user = UserSupplier.getCustomerUser();
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        Booking booking = BookingSupplier.getBooking();

        when(bookingsRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        //When (Act)
        AccessException exception = assertThrows(AccessException.class,
                () -> bookingService.setBookingStatusCancelled(booking.getId()));

        //Then (Assert)
        String expectedMessage = "You can't access this booking";
        assertEquals(expectedMessage, exception.getMessage());

        verify(bookingsRepository).findById(booking.getId());
    }

    @Test
    @DisplayName("Verify correct BookingDto returned using right data")
    public void updateBookingStatus_WithCorrectData_ReturnsDto() {
        //Given (Arrange)
        Booking booking = BookingSupplier.getBooking();
        BookingDto bookingDto = BookingSupplier.getBookingDto();
        UpdateBookingStatusDto updateBookingStatusDto =
                BookingSupplier.getUpdateBookingStatusDto();

        when(bookingsRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking))
                .thenReturn(bookingDto);
        when(bookingsRepository.save(booking))
                .thenReturn(booking);

        //When (Act)
        BookingDto actual = bookingService
                .updateBookingStatus(booking.getId(), updateBookingStatusDto);

        //Then (Assert)
        assertEquals(bookingDto, actual);

        verify(bookingsRepository).findById(booking.getId());
        verify(bookingMapper).toDto(booking);
        verify(bookingsRepository).save(booking);
    }

    @Test
    @DisplayName("Verify exception thrown on returned using cancelled book")
    public void updateBookingStatus_WithWrongAccess_ThrowsException() {
        //Given (Arrange)
        Booking booking = BookingSupplier.getCancelledBooking();
        UpdateBookingStatusDto updateBookingStatusDto =
                BookingSupplier.getUpdateBookingStatusDto();

        when(bookingsRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        //When (Act)
        AccessException exception = assertThrows(AccessException.class,
                () -> bookingService.updateBookingStatus(booking.getId(),
                        updateBookingStatusDto));

        //Then (Assert)
        String expectedMessage = "This booking is cancelled";
        assertEquals(expectedMessage, exception.getMessage());

        verify(bookingsRepository).findById(booking.getId());
    }

    @Test
    @DisplayName("Verify correct BookingDto returned using right data")
    public void updateBooking_WithCorrectData_ReturnsDto() {
        //Given (Arrange)
        Booking booking = BookingSupplier.getBooking();
        BookingDto bookingDto = BookingSupplier.getBookingDto();
        UpdateBookingDto updateBookingDto =
                BookingSupplier.getUpdateBookingDto();

        when(bookingsRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking))
                .thenReturn(bookingDto);
        when(bookingsRepository.save(booking))
                .thenReturn(booking);

        //When (Act)
        BookingDto actual = bookingService
                .updateBooking(booking.getId(), updateBookingDto);

        //Then (Assert)
        assertEquals(bookingDto, actual);

        verify(bookingsRepository).findById(booking.getId());
        verify(bookingMapper).toDto(booking);
        verify(bookingsRepository).save(booking);
    }

    @Test
    @DisplayName("Verify exception thrown if booking not found")
    public void updateBooking_WithNullBooking_ThrowsException() {
        //Given (Arrange)
        Long wrongId = 1L;
        UpdateBookingStatusDto updateBookingStatusDto =
                BookingSupplier.getUpdateBookingStatusDto();

        when(bookingsRepository.findById(wrongId))
                .thenReturn(Optional.empty());

        //When (Act)
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.updateBookingStatus(wrongId,
                        updateBookingStatusDto));

        //Then (Assert)
        String expectedMessage = "Booking with id " + wrongId + " not found";
        assertEquals(expectedMessage, exception.getMessage());

        verify(bookingsRepository).findById(wrongId);
    }

    @Test
    @DisplayName("Verify correct total number returned")
    public void markExpiredBookings_ReturnsBigDecimal() {
        //Given (Arrange)
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Booking> preExpiredList = BookingSupplier.getPreExpiredList();
        List<Booking> expiredList = BookingSupplier.getExpiredList();

        when(bookingsRepository.findBookBeforeDateByStatus(
                tomorrow, BookingStatus.PENDING))
                .thenReturn(preExpiredList);
        when(bookingsRepository.saveAll(anyList()))
                .thenReturn(expiredList);

        //When (Act)
        bookingService.markExpiredBookings();

        //Then (Assert)
        verify(bookingsRepository).findBookBeforeDateByStatus(
                tomorrow, BookingStatus.PENDING);
        verify(bookingsRepository).saveAll(anyList());
    }
}
