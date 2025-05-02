package rental.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import rental.project.model.embedded.Address;
import rental.project.utils.StringArrayConverter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@SQLDelete(sql = "UPDATE accommodations "
        + "SET is_deleted = true WHERE id = ?")
@SQLRestriction(value = "is_deleted = false")
@Table(name = "accommodations")
public class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccommodationType type;

    @Column(nullable = false)
    private String size;

    @Embedded
    private Address address;

    @Convert(converter = StringArrayConverter.class)
    @Column(length = 10)
    private String[] amenities;

    @Column(nullable = false)
    private BigDecimal dailyRate;

    @Column(nullable = false)
    private Integer availability;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isDeleted = false;

    public enum AccommodationType {
        HOUSE,
        APARTMENT,
        CONDO,
        VACATION_HOME
    }
}
