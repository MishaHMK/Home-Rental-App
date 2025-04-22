package rental.project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import rental.project.dto.accommodation.AccommodationDto;
import rental.project.dto.accommodation.CreateAccommodationDto;
import rental.project.dto.accommodation.UpdateAccommodationDto;
import rental.project.exception.EntityNotFoundException;
import rental.project.mapper.AccommodationMapper;
import rental.project.model.Accommodation;
import rental.project.repository.accommodation.AccommodationRepository;
import rental.project.service.accommodation.AccommodationServiceImpl;
import rental.project.service.notificaiton.NotificationService;
import rental.project.supplier.AccommodationSupplier;

@ExtendWith(MockitoExtension.class)
public class AccommodationServiceTest {
    @Mock
    private AccommodationRepository accommodationRepository;
    @Mock
    private AccommodationMapper accommodationMapper;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AccommodationServiceImpl accommodationService;

    @Test
    @DisplayName("Verify correct accommodation dto list by using any Pageable")
    public void findAll_WithAnyPageable_ShouldReturnAccommodationDtoList() {
        //Given (Arrange)
        List<Accommodation> accommodationList = AccommodationSupplier.getAccommodationList();
        Page<Accommodation> accommodationPage = new PageImpl<>(accommodationList);

        when(accommodationRepository.findAll(any(Pageable.class)))
                .thenReturn(accommodationPage);
        when(accommodationMapper.toDto(any(Accommodation.class)))
                .thenReturn(new AccommodationDto());

        List<AccommodationDto> expectedList = accommodationList.stream()
                .map(accommodationMapper::toDto)
                .toList();

        //When (Act)
        List<AccommodationDto> actualList = accommodationService
                .findAll(PageRequest.of(0, 10));

        //Then (Assert)
        assertFalse(actualList.isEmpty());
        assertEquals(expectedList.size(), actualList.size());
        assertEquals(expectedList, actualList);

        verify(accommodationRepository).findAll(any(Pageable.class));
        verify(accommodationMapper, times(
                accommodationList.size() + expectedList.size()))
                .toDto(any(Accommodation.class));
    }

    @Test
    @DisplayName("Verify correct accommodation by using any valid id")
    public void findById_WithValidId_ShouldReturnAccommodationDto() {
        //Given (Arrange)
        Accommodation accommodation = AccommodationSupplier.getAccommodation();
        Long validId = accommodation.getId();

        when(accommodationRepository.findById(validId)).thenReturn(Optional.of(accommodation));
        when(accommodationMapper.toDto(accommodation)).thenReturn(new AccommodationDto());

        AccommodationDto expectedDto = accommodationMapper.toDto(accommodation);

        //When (Act)
        AccommodationDto actualDto = accommodationService
                .findByAccommodationId(validId);

        //Then (Assert)
        assertEquals(expectedDto, actualDto);

        verify(accommodationRepository).findById(validId);
        verify(accommodationMapper, times(2)).toDto(any(Accommodation.class));
    }

    @Test
    @DisplayName("Verify exception thrown by using invalid id")
    public void findById_WithInvalidId_ShouldThrowException() {
        //Given (Arrange)
        Long invalidId = 5L;

        when(accommodationRepository.findById(invalidId)).thenReturn(Optional.empty());

        //When (Act)
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> accommodationService.findByAccommodationId(invalidId)
        );

        //Then (Assert)
        String expectedMessage = "Accommodation with id "
                + invalidId + " not found";
        assertEquals(expectedMessage, exception.getMessage());
        verify(accommodationRepository, times(1))
                .findById(invalidId);
    }

    @Test
    @DisplayName("Verify correct accommodation by using valid data")
    public void save_WithValidData_ShouldReturnAccommodationDto() {
        //Given (Arrange)
        CreateAccommodationDto createAccommodationDto =
                AccommodationSupplier.getCreateDto();

        Accommodation accommodation = AccommodationSupplier.getAccommodation();

        AccommodationDto expectedDto = AccommodationSupplier.getAccommodationDto();

        when(accommodationMapper.toEntity(createAccommodationDto)).thenReturn(accommodation);
        when(accommodationMapper.toDto(accommodation)).thenReturn(expectedDto);
        when(accommodationRepository.save(accommodation)).thenReturn(accommodation);

        //When (Act)
        AccommodationDto actualDto = accommodationService.save(createAccommodationDto);

        //Then (Assert)
        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        verify(accommodationMapper).toEntity(createAccommodationDto);
        verify(accommodationMapper).toDto(accommodation);
        verify(accommodationRepository).save(accommodation);
        verify(notificationService).onAccommodationCreation(expectedDto);
    }

    @Test
    @DisplayName("Verify delete calls repository method")
    public void deleteById_WithValidId_ShouldCallRepositoryDeleteById() {
        //Given (Arrange)
        Long validAccommodationId = 1L;

        //When (Act)
        accommodationService.deleteById(validAccommodationId);

        //Then (Assert)
        verify(accommodationRepository).deleteById(validAccommodationId);
    }

    @Test
    @DisplayName("Verify correct accommodation by using valid data")
    public void updateAccommodation_WithValidDataAndId_ShouldReturnAccommodationDto() {
        //Given (Arrange)
        Long validId = 1L;
        UpdateAccommodationDto updateAccommodationDto = AccommodationSupplier.getUpdateDto();
        Accommodation accommodation = AccommodationSupplier.getAccommodation();
        AccommodationDto expectedDto = AccommodationSupplier.getAccommodationDto();

        when(accommodationRepository.findById(validId)).thenReturn(Optional.of(accommodation));
        when(accommodationRepository.save(accommodation)).thenReturn(accommodation);
        when(accommodationMapper.toDto(accommodation)).thenReturn(expectedDto);

        //When (Act)
        AccommodationDto actualDto = accommodationService
                .updateAccommodation(validId, updateAccommodationDto);

        //Then (Assert)
        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        verify(accommodationMapper, times(1))
                .updateFromDto(updateAccommodationDto, accommodation);
        verify(accommodationRepository, times(1)).save(accommodation);
        verify(accommodationMapper, times(1)).toDto(accommodation);
    }

    @Test
    @DisplayName("Verify exception thrown by using invalid id")
    public void updateAccommodation_WithInvalidId_ShouldThrowException() {
        //Given (Arrange)
        Long invalidId = 5L;
        UpdateAccommodationDto updateAccommodationDto = AccommodationSupplier.getUpdateDto();
        Accommodation accommodation = AccommodationSupplier.getAccommodation();
        AccommodationDto expectedDto = AccommodationSupplier.getAccommodationDto();

        when(accommodationRepository.findById(invalidId)).thenReturn(Optional.empty());

        //When (Act)
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> accommodationService.updateAccommodation(invalidId, updateAccommodationDto)
        );

        //Then (Assert)
        String expectedMessage = "Accommodation with id "
                + invalidId + " not found";
        assertEquals(expectedMessage, exception.getMessage());
        verify(accommodationRepository, times(1))
                .findById(invalidId);
    }
}
