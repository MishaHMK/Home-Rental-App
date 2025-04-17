package rental.project.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePaymentDto {
    @NotNull(message = "Booking id is required")
    private Long bookingId;
}
