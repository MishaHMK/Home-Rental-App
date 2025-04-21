package rental.project.dto.booking;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateBookingDto {
    @NotNull(message = "Check in date is required")
    private LocalDate checkinDate;
    @NotNull(message = "Check out date is required")
    private LocalDate checkoutDate;
    @NotNull(message = "Accommodation id is required")
    private Long accommodationId;
}
