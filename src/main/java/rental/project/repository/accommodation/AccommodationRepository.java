package rental.project.repository.accommodation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rental.project.model.Accommodation;

public interface AccommodationRepository
        extends JpaRepository<Accommodation, Long> {
    Page<Accommodation> findAll(Pageable pageable);
}
