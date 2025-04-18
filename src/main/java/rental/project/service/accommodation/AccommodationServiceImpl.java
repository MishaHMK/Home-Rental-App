package rental.project.service.accommodation;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rental.project.dto.accommodation.AccommodationDto;
import rental.project.dto.accommodation.CreateAccommodationDto;
import rental.project.dto.accommodation.UpdateAccommodationDto;
import rental.project.mapper.AccommodationMapper;
import rental.project.model.Accommodation;
import rental.project.repository.accommodation.AccommodationRepository;
import rental.project.service.notificaiton.NotificationService;

@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;
    private final NotificationService notificationService;

    @Override
    public AccommodationDto save(CreateAccommodationDto createAccommodationDto) {
        Accommodation toCreate = accommodationMapper.toEntity(createAccommodationDto);
        AccommodationDto dto = accommodationMapper.toDto(accommodationRepository.save(toCreate));
        notificationService.onAccommodationCreation(dto);
        return dto;
    }

    @Override
    public List<AccommodationDto> findAll() {
        return accommodationRepository.findAll().stream()
                .map(accommodationMapper::toDto)
                .toList();
    }

    @Override
    public AccommodationDto findByAccommodationId(Long id) {
        return accommodationMapper.toDto(accommodationRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Accommodation with id "
                                + id + " not found")
                ));
    }

    @Override
    public AccommodationDto updateAccommodation(
            Long id,
            UpdateAccommodationDto updateAccommodationDto) {
        Accommodation toUpdate = accommodationRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Accommodation with id "
                                + id + " not found")
                );
        accommodationMapper.updateFromDto(updateAccommodationDto, toUpdate);
        return accommodationMapper.toDto(accommodationRepository.save(toUpdate));
    }

    @Override
    public void deleteById(Long id) {
        accommodationRepository.deleteById(id);
    }
}
