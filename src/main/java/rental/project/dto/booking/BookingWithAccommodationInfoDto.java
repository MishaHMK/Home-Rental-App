package rental.project.dto.booking;

import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;
import rental.project.dto.accommodation.AccommodationDto;

@Data
@Accessors(chain = true)
public class BookingWithAccommodationInfoDto {
    private Long id;
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private AccommodationDto accommodation;
    private Long userId;
    private String status;
}
