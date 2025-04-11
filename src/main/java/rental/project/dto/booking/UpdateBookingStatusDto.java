package rental.project.dto.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import rental.project.model.Booking;

@Data
public class UpdateBookingStatusDto {
    @NotNull(message = "New status is required")
    private Booking.BookingStatus status;
}
