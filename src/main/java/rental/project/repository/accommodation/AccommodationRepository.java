package rental.project.repository.accommodation;

import org.springframework.data.jpa.repository.JpaRepository;
import rental.project.model.Accommodation;

public interface AccommodationRepository
        extends JpaRepository<Accommodation, Long> {

}
