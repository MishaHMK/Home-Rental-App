package rental.project.dto.booking;

import java.time.LocalDate;
import lombok.Data;

@Data
public class BookingDto {
    private Long id;
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private Long accommodationId;
    private Long userId;
    private String status;
}
