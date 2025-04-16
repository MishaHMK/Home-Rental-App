package rental.project.service.booking;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Pageable;
import rental.project.dto.booking.BookingDto;
import rental.project.dto.booking.BookingWithAccommodationInfoDto;
import rental.project.dto.booking.CreateBookingDto;
import rental.project.dto.booking.UpdateBookingDto;
import rental.project.dto.booking.UpdateBookingStatusDto;
import rental.project.model.Booking;

public interface BookingService {
    BookingDto save(CreateBookingDto createBookingDto);

    List<BookingDto> findByUserIdAndStatus(
            Pageable pageable, Long userId,
            Booking.BookingStatus[] statuses);

    List<BookingDto> getAllMyBookings(Pageable pageable);

    BookingWithAccommodationInfoDto getBookingDetailsById(Long bookingId);

    BookingDto setBookingStatusCancelled(Long bookingId);

    BookingDto updateBookingStatus(
            Long bookingId, UpdateBookingStatusDto updateBookingStatusDto);

    BookingDto updateBooking(Long bookingId, UpdateBookingDto updateDto);

    BigDecimal countTotalAmount(Long bookingId);
}
