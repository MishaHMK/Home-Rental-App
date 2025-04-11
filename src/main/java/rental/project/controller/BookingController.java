package rental.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rental.project.dto.booking.BookingDto;
import rental.project.dto.booking.CreateBookingDto;
import rental.project.dto.booking.UpdateBookingDto;
import rental.project.dto.booking.UpdateBookingStatusDto;
import rental.project.model.Booking;
import rental.project.service.booking.BookingService;

@Tag(name = "Booking controller",
        description = "Bookings management endpoint")
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @PostMapping
    @Operation(summary = "Create booking",
            description = "Create new booking for authorized user"
                    + " in system with provided data")
    public BookingDto createBooking(
            @RequestBody @Valid CreateBookingDto createBookingDto) {
        return bookingService.save(createBookingDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    @Operation(summary = "Search booking",
            description = "Search booking in system by user and status")
    public List<BookingDto> searchByUserIdAndStatus(
            @RequestParam Long userId,
            @RequestParam(required = false) Booking.BookingStatus[] statuses,
            @ParameterObject Pageable pageable) {
        return bookingService.findByUserIdAndStatus(pageable, userId, statuses);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @GetMapping("/my")
    @Operation(summary = "Get all my bookings",
            description = "Get all bookings for authorized user")
    public List<BookingDto> getAllMyBookings(@ParameterObject Pageable pageable) {
        return bookingService.getAllMyBookings(pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @GetMapping("/{bookingId}")
    @Operation(summary = "Get booking by id",
            description = "Get booking data by given id")
    public BookingDto getAllMyBookings(@PathVariable Long bookingId) {
        return bookingService.getBookingDetailsById(bookingId);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PatchMapping("/update-status/{bookingId}")
    @Operation(summary = "Update booking status",
            description = "Update booking status by given id")
    public BookingDto updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestBody @Valid UpdateBookingStatusDto updateBookingStatusDto) {
        return bookingService.updateBookingStatus(bookingId, updateBookingStatusDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @PutMapping("/{bookingId}")
    @Operation(summary = "Update booking data",
            description = "Update booking data by given id")
    public BookingDto updateBooking(
            @PathVariable Long bookingId,
            @RequestBody @Valid UpdateBookingDto updateDto) {
        return bookingService.updateBooking(bookingId, updateDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{bookingId}")
    @Operation(summary = "Cancel booking by id",
            description = "Set booking status cancelled")
    public BookingDto cancelBooking(@PathVariable Long bookingId) {
        return bookingService.setBookingStatusCancelled(bookingId);
    }
}
