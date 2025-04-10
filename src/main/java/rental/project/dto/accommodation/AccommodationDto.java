package rental.project.dto.accommodation;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class AccommodationDto {
    private Long id;
    private String type;
    private String size;
    private AddressDto addressDto;
    private String[] amenities;
    private BigDecimal dailyRate;
    private Integer availability;
}
