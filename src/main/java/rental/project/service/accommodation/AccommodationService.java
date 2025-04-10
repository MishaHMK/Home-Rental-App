package rental.project.service.accommodation;

import java.util.List;
import rental.project.dto.accommodation.AccommodationDto;
import rental.project.dto.accommodation.CreateAccommodationDto;
import rental.project.dto.accommodation.UpdateAccommodationDto;

public interface AccommodationService {
    AccommodationDto save(CreateAccommodationDto createAccommodationDto);

    List<AccommodationDto> findAll();

    AccommodationDto findByAccommodationId(Long id);

    AccommodationDto updateAccommodation(Long id, UpdateAccommodationDto updateAccommodationDto);

    void deleteById(Long id);
}
