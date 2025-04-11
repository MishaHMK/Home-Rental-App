package rental.project.service.booking;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rental.project.dto.booking.BookingDto;
import rental.project.dto.booking.CreateBookingDto;
import rental.project.dto.booking.UpdateBookingDto;
import rental.project.dto.booking.UpdateBookingStatusDto;
import rental.project.exception.AccessException;
import rental.project.mapper.BookingMapper;
import rental.project.model.Booking;
import rental.project.model.User;
import rental.project.repository.booking.BookingsRepository;
import rental.project.security.SecurityUtil;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingsRepository bookingsRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto save(CreateBookingDto createBookingDto) {
        createBookingDto.setUserId(SecurityUtil.getLoggedInUserId());
        createBookingDto.setStatus("PENDING");
        Booking booking = bookingMapper.toEntity(createBookingDto);
        return bookingMapper.toDto(bookingsRepository.save(booking));
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
    public BookingDto getBookingDetailsById(Long bookingId) {
        User loggedInUser = SecurityUtil.getLoggedInUser();
        Booking booking = getBookingById(bookingId);
        if (!booking.getUser().getId().equals(loggedInUser.getId())
                && loggedInUser.getRole() != User.Role.ADMIN) {
            throw new AccessException("You can't access this booking data");
        }
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingDto setBookingStatusCancelled(Long bookingId) {
        User loggedInUser = SecurityUtil.getLoggedInUser();
        Booking booking = getBookingById(bookingId);
        if (!checkAccess(loggedInUser, booking)) {
            throw new AccessException("You can't access this booking");
        }
        booking.setStatus(Booking.BookingStatus.CANCELED);
        return bookingMapper.toDto(bookingsRepository.save(booking));
    }

    @Override
    public BookingDto updateBookingStatus(Long bookingId, UpdateBookingStatusDto updateDto) {
        Booking booking = getBookingById(bookingId);
        booking.setStatus(updateDto.getStatus());
        return bookingMapper.toDto(bookingsRepository.save(booking));
    }

    @Override
    public BookingDto updateBooking(Long bookingId, UpdateBookingDto updateDto) {
        Booking booking = getBookingById(bookingId);
        bookingMapper.updateFromDto(updateDto, booking);
        return bookingMapper.toDto(bookingsRepository.save(booking));
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
}
