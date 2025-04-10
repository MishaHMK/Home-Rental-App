package rental.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import rental.project.dto.accommodation.AccommodationDto;
import rental.project.dto.accommodation.CreateAccommodationDto;
import rental.project.dto.accommodation.UpdateAccommodationDto;
import rental.project.service.accommodation.AccommodationService;

@Tag(name = "Accommodation controller",
        description = "Accommodations management endpoint")
@RestController
@RequiredArgsConstructor
@RequestMapping("/accommodations")
public class AccommodationController {
    private final AccommodationService accommodationService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    @Operation(summary = "Create accommodations",
            description = "Create new accommodations in system with provided data")
    public AccommodationDto createAccommodation(
            @RequestBody @Valid CreateAccommodationDto createAccommodationDto) {
        return accommodationService.save(createAccommodationDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @GetMapping()
    @Operation(summary = "Get all accommodations",
            description = "Get all accommodations in system")
    public List<AccommodationDto> getAllAccommodations() {
        return accommodationService.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @GetMapping("/{id}")
    @Operation(summary = "Get accommodation by id",
            description = "Get accommodation from system by given idm")
    public AccommodationDto getAccommodationById(@PathVariable Long id) {
        return accommodationService.findByAccommodationId(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Get accommodation by id",
            description = "Get accommodation from system by given idm")
    public AccommodationDto updateAccommodation(
             @PathVariable Long id,
             @Valid @RequestBody UpdateAccommodationDto updateDto) {
        return accommodationService.updateAccommodation(id, updateDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete accommodation",
            description = "Remove accommodation from system by given id")
    public void deleteAccommodation(@PathVariable Long id) {
        accommodationService.deleteById(id);
    }
}
