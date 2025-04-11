package rental.project.dto.booking;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BookingDto {
    private Long id;
    private LocalDateTime checkinDate;
    private LocalDateTime checkoutDate;
    private Long accommodationId;
    private Long userId;
    private String status;
}
