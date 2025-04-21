package rental.project.dto.accommodation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateAccommodationDto {
    @NotBlank(message = "Accommodation type is required")
    private String type;
    @NotBlank(message = "Accommodation room(s) description is required")
    private String size;
    @NotNull(message = "Address is required")
    private AddressDto addressDto;
    private String[] amenities;
    @NotNull(message = "Accommodation rent price per day is required")
    private BigDecimal dailyRate;
    @NotNull(message = "Number of available units is required")
    private Integer availability;
}
