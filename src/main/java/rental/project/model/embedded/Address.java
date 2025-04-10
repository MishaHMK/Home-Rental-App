package rental.project.model.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Embeddable
public class Address {
    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String country;

    private String state;

    private String postalCode;

    private Double latitude;

    private Double longitude;
}
