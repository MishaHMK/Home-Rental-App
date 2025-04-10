package rental.project.dto.accommodation;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AddressDto {
    private String street;
    private String city;
    private String country;
    private String state;
    private String postalCode;
    private Double latitude;
    private Double longitude;
}
