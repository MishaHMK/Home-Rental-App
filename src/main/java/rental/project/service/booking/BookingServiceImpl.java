package rental.project.service.booking;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import rental.project.model.Payment;
import rental.project.model.User;
import rental.project.notification.NotificationService;
import rental.project.repository.accommodation.AccommodationRepository;
import rental.project.repository.booking.BookingsRepository;
import rental.project.repository.payment.PaymentsRepository;
import rental.project.security.SecurityUtil;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingsRepository bookingsRepository;
    private final AccommodationRepository accommodationRepository;
    private final BookingMapper bookingMapper;
    private final PaymentsRepository paymentsRepository;
    private final NotificationService notificationService;

    @Override
    public BookingDto save(CreateBookingDto createBookingDto) {
        Long loggedInUserId = SecurityUtil.getLoggedInUserId();
        if (!paymentsRepository.findAllByStatus(
                Payment.PaymentStatus.PENDING, loggedInUserId).isEmpty()) {
            throw new AccessException("You have already pending payment to pay first");
        }
        Accommodation accommodation = getAccommodationById(
                createBookingDto.getAccommodationId());
        if (checkAccommodationAvailability(accommodation, createBookingDto)) {
            throw new AccessException("This accommodation is not available.");
        }
        createBookingDto.setUserId(loggedInUserId);
        createBookingDto.setStatus("PENDING");
        Booking booking = bookingMapper.toEntity(createBookingDto);
        BookingDto dto = bookingMapper.toDto(bookingsRepository.save(booking));
        notificationService.onBookingCreation(dto);
        return dto;
    }

    @Override
    public List<BookingDto> findByUserIdAndStatus(
            Pageable pageable,
            Long userId,
            Booking.BookingStatus[] statuses) {
        return bookingsRepository.findByUserIdAndStatus(userId, statuses, pageable)
                .stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public List<BookingDto> getAllMyBookings(Pageable pageable) {
        Long loggedInUserId = SecurityUtil.getLoggedInUserId();
        return bookingsRepository.findByUserId(loggedInUserId, pageable)
                .stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public BookingWithAccommodationInfoDto getBookingDetailsById(Long bookingId) {
        User loggedInUser = SecurityUtil.getLoggedInUser();
        Booking booking = getBookingById(bookingId);
        if (!booking.getUser().getId().equals(loggedInUser.getId())
                && loggedInUser.getRole() != User.Role.ADMIN) {
            throw new AccessException("You can't access this booking data");
        }
        return bookingMapper.toDetailedDto(booking);
    }

    @Override
    public BookingDto setBookingStatusCancelled(Long bookingId) {
        User loggedInUser = SecurityUtil.getLoggedInUser();
        Booking booking = getBookingById(bookingId);
        if (booking.getStatus().equals(Booking.BookingStatus.EXPIRED)) {
            throw new BookingException("You can't cancel expired booking");
        }
        if (!checkAccess(loggedInUser, booking)) {
            throw new AccessException("You can't access this booking");
        }
        booking.setStatus(Booking.BookingStatus.CANCELED);
        BookingDto dto = bookingMapper.toDto(bookingsRepository.save(booking));
        notificationService.onAccommodationRelease(booking.getAccommodation());
        notificationService.onBookingCancellation(dto);
        return dto;
    }

    @Override
    public BookingDto updateBookingStatus(Long bookingId, UpdateBookingStatusDto updateDto) {
        Booking booking = getBookingById(bookingId);
        if (booking.getStatus() == Booking.BookingStatus.CANCELED) {
            throw new AccessException("This booking is cancelled");
        }
        booking.setStatus(updateDto.getStatus());
        return bookingMapper.toDto(bookingsRepository.save(booking));
    }

    @Override
    public BookingDto updateBooking(Long bookingId, UpdateBookingDto updateDto) {
        Booking booking = getBookingById(bookingId);
        bookingMapper.updateFromDto(updateDto, booking);
        return bookingMapper.toDto(bookingsRepository.save(booking));
    }

    @Override
    public BigDecimal countTotalAmount(Long bookingId) {
        BookingWithAccommodationInfoDto bookingData = getBookingDetailsById(bookingId);
        long dayDifference = ChronoUnit.DAYS.between(
                bookingData.getCheckinDate(), bookingData.getCheckoutDate());
        return bookingData.getAccommodation().getDailyRate()
                .multiply(BigDecimal.valueOf(dayDifference));
    }

    @Scheduled(cron = "0 12 18 * * ?")
    public void markExpiredBookings() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Booking> expired = bookingsRepository
                .findBookBeforeDateByStatus(tomorrow,
                        Booking.BookingStatus.PENDING);
        for (Booking booking : expired) {
            booking.setStatus(Booking.BookingStatus.EXPIRED);
            notificationService.onAccommodationRelease(booking.getAccommodation());
        }
        bookingsRepository.saveAll(expired);
    }

    private boolean checkAccess(User user, Booking booking) {
        return booking.getUser().getId().equals(user.getId())
                || user.getRole() == User.Role.ADMIN;
    }

    private Booking getBookingById(Long bookingId) {
        return bookingsRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException("Booking with id "
                        + bookingId + " not found"));
    }

    private Accommodation getAccommodationById(Long id) {
        return accommodationRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Accommodation with id "
                                + id + " not found")
                );
    }

    private List<Booking> getBookingsByDateRange(
            Long accommodationId, LocalDate first, LocalDate second) {
        return bookingsRepository.findByDateRange(accommodationId, first, second);
    }

    private boolean checkAccommodationAvailability(Accommodation accommodation,
                                                   CreateBookingDto createBookingDto) {
        List<Booking> bookingsByDateRange = getBookingsByDateRange(accommodation.getId(),
                createBookingDto.getCheckinDate(),
                createBookingDto.getCheckoutDate());

        return accommodation.getAvailability() - 1 < bookingsByDateRange.size();
    }
}
