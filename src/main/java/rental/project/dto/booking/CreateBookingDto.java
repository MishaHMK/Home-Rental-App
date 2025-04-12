package rental.project.dto.booking;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class CreateBookingDto {
    @NotNull(message = "Check in date is required")
    private LocalDate checkinDate;
    @NotNull(message = "Check out date is required")
    private LocalDate checkoutDate;
    @NotNull(message = "Accommodation id is required")
    private Long accommodationId;
    private Long userId;
    private String status;
}
