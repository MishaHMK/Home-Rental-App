package rental.project.model.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
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
