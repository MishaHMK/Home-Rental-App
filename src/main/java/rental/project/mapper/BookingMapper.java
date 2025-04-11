package rental.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import rental.project.config.MapperConfig;
import rental.project.dto.booking.BookingDto;
import rental.project.dto.booking.CreateBookingDto;
import rental.project.dto.booking.UpdateBookingDto;
import rental.project.model.Booking;

@Mapper(config = MapperConfig.class,
        uses = {AccommodationMapper.class, UserMapper.class})
public interface BookingMapper {
    @Mapping(source = "userId", target = "user", qualifiedByName = "userFromId")
    @Mapping(source = "accommodationId", target = "accommodation",
            qualifiedByName = "accommodationFromId")
    Booking toEntity(CreateBookingDto dto);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "accommodation.id", target = "accommodationId")
    BookingDto toDto(Booking booking);

    @Mapping(source = "accommodationId", target = "accommodation",
            qualifiedByName = "accommodationFromId")
    void updateFromDto(UpdateBookingDto dto,
                       @MappingTarget Booking booking);
}
