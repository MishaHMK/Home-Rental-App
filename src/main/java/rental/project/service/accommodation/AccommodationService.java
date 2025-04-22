package rental.project.service.accommodation;

import java.util.List;
import org.springframework.data.domain.Pageable;
import rental.project.dto.accommodation.AccommodationDto;
import rental.project.dto.accommodation.CreateAccommodationDto;
import rental.project.dto.accommodation.UpdateAccommodationDto;

public interface AccommodationService {
    AccommodationDto save(CreateAccommodationDto createAccommodationDto);

    List<AccommodationDto> findAll(Pageable pageable);

    AccommodationDto findByAccommodationId(Long id);

    AccommodationDto updateAccommodation(Long id, UpdateAccommodationDto updateAccommodationDto);

    void deleteById(Long id);
}
