package rental.project.dto.booking;

import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BookingDto {
    private Long id;
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private Long accommodationId;
    private Long userId;
    private String status;
}
