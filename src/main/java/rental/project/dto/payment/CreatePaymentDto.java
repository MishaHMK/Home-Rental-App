package rental.project.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreatePaymentDto {
    @NotNull(message = "Booking id is required")
    private Long bookingId;
}
