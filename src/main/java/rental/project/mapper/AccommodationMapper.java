package rental.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import rental.project.config.MapperConfig;
import rental.project.dto.accommodation.AccommodationDto;
import rental.project.dto.accommodation.AddressDto;
import rental.project.dto.accommodation.CreateAccommodationDto;
import rental.project.dto.accommodation.UpdateAccommodationDto;
import rental.project.model.Accommodation;
import rental.project.model.embedded.Address;

@Mapper(config = MapperConfig.class)
public interface AccommodationMapper {
    @Mapping(source = "addressDto", target = "address", qualifiedByName = "mapToAddress")
    Accommodation toEntity(CreateAccommodationDto createDto);

    @Mapping(source = "address", target = "addressDto", qualifiedByName = "mapToAddressDto")
    AccommodationDto toDto(Accommodation accommodation);

    @Named("mapToAddress")
    Address toAddress(AddressDto createDto);

    @Named("mapToAddressDto")
    AddressDto toAddressDto(Address createDto);

    @Mapping(source = "addressDto", target = "address", qualifiedByName = "mapToAddress")
    void updateFromDto(UpdateAccommodationDto dto,
                       @MappingTarget Accommodation accommodation);
}
