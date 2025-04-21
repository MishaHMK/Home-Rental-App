package rental.project.dto.accommodation;

import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AccommodationDto {
    private Long id;
    private String type;
    private String size;
    private AddressDto addressDto;
    private String[] amenities;
    private BigDecimal dailyRate;
    private Integer availability;
}
