package rental.project.supplier;

import java.math.BigDecimal;
import java.util.List;
import rental.project.dto.accommodation.AccommodationDto;
import rental.project.dto.accommodation.AddressDto;
import rental.project.dto.accommodation.CreateAccommodationDto;
import rental.project.dto.accommodation.UpdateAccommodationDto;
import rental.project.model.Accommodation;
import rental.project.model.embedded.Address;

public class AccommodationSupplier {
    public static List<Accommodation> getAccommodationList() {
        Address firstAddress = new Address()
                .setStreet("Test Street, 22")
                .setCity("Test City")
                .setState("Test Region")
                .setPostalCode("80000")
                .setCountry("Test Country")
                .setLatitude(50.0)
                .setLongitude(15.0);

        Address secondAddress = new Address()
                .setStreet("Test Street, 52")
                .setCity("Test City 2")
                .setState("Test Region 2")
                .setPostalCode("80001")
                .setCountry("Test Country 2")
                .setLatitude(18.5)
                .setLongitude(14.32);

        Accommodation firstAccommodation = new Accommodation()
                .setId(1L)
                .setType(Accommodation.AccommodationType.HOUSE)
                .setAddress(firstAddress)
                .setSize("Test size info")
                .setAmenities(new String[] {"amenity 1",
                        "amenity 2", "amenity 3"})
                .setDailyRate(BigDecimal.valueOf(89.99))
                .setAvailability(2);

        Accommodation secondAccommodation = new Accommodation()
                .setId(2L)
                .setType(Accommodation.AccommodationType.VACATION_HOME)
                .setAddress(secondAddress)
                .setSize("Test size info 2")
                .setAmenities(new String[] {"amenity 1",
                        "amenity 2", "amenity 3"})
                .setDailyRate(BigDecimal.valueOf(119.99))
                .setAvailability(2);

        Accommodation thirdAccommodation = new Accommodation()
                .setId(3L)
                .setType(Accommodation.AccommodationType.APARTMENT)
                .setAddress(secondAddress)
                .setSize("Test size info 3")
                .setAmenities(new String[] {"amenity 1",
                        "amenity 2", "amenity 3"})
                .setDailyRate(BigDecimal.valueOf(49.99))
                .setAvailability(3);

        return List.of(firstAccommodation, secondAccommodation,
                thirdAccommodation);
    }

    public static Accommodation getAccommodation() {
        Address address = new Address()
                .setStreet("Test Street, 22")
                .setCity("Test City")
                .setState("Test Region")
                .setPostalCode("80000")
                .setCountry("Test Country")
                .setLatitude(50.0)
                .setLongitude(15.0);

        return new Accommodation()
                .setId(1L)
                .setType(Accommodation.AccommodationType.HOUSE)
                .setAddress(address)
                .setSize("Test size info")
                .setAmenities(new String[] {"amenity 1",
                        "amenity 2", "amenity 3"})
                .setDailyRate(BigDecimal.valueOf(89.99))
                .setAvailability(2);
    }

    public static CreateAccommodationDto getCreateDto() {
        AddressDto addressDto = new AddressDto()
                .setStreet("Test Street, 22")
                .setCity("Test City")
                .setState("Test Region")
                .setPostalCode("80000")
                .setCountry("Test Country")
                .setLatitude(50.0)
                .setLongitude(15.0);

        return new CreateAccommodationDto()
                .setType("HOUSE")
                .setAddressDto(addressDto)
                .setSize("Test size info")
                .setAmenities(new String[] {"amenity 1",
                        "amenity 2", "amenity 3"})
                .setDailyRate(BigDecimal.valueOf(89.99))
                .setAvailability(2);
    }

    public static AccommodationDto getAccommodationDto() {
        AddressDto addressDto = new AddressDto()
                .setStreet("Test Street, 22")
                .setCity("Test City")
                .setState("Test Region")
                .setPostalCode("80000")
                .setCountry("Test Country")
                .setLatitude(50.0)
                .setLongitude(15.0);

        return new AccommodationDto()
                .setType("HOUSE")
                .setAddressDto(addressDto)
                .setSize("Test size info")
                .setAmenities(new String[] {"amenity 1",
                        "amenity 2", "amenity 3"})
                .setDailyRate(BigDecimal.valueOf(89.99))
                .setAvailability(2);
    }

    public static UpdateAccommodationDto getUpdateDto() {
        AddressDto addressDto = new AddressDto()
                .setStreet("Test Street, 22")
                .setCity("Test City")
                .setState("Test Region")
                .setPostalCode("80000")
                .setCountry("Test Country")
                .setLatitude(50.0)
                .setLongitude(15.0);

        return new UpdateAccommodationDto()
                .setType("HOUSE")
                .setAddressDto(addressDto)
                .setSize("Test size info")
                .setAmenities(new String[] {"amenity 1",
                        "amenity 2", "amenity 3"})
                .setDailyRate(BigDecimal.valueOf(89.99))
                .setAvailability(2);
    }
}
