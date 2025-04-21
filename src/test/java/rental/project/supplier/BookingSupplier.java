package rental.project.supplier;

import java.time.LocalDate;
import java.util.List;
import rental.project.dto.booking.BookingDto;
import rental.project.dto.booking.BookingWithAccommodationInfoDto;
import rental.project.dto.booking.CreateBookingDto;
import rental.project.dto.booking.UpdateBookingDto;
import rental.project.dto.booking.UpdateBookingStatusDto;
import rental.project.model.Booking;
import rental.project.model.Booking.BookingStatus;

public class BookingSupplier {
    public static Booking getBooking() {
        return new Booking()
                .setId(1L)
                .setCheckinDate(LocalDate.now())
                .setCheckoutDate(LocalDate.now().plusDays(5))
                .setAccommodation(AccommodationSupplier.getAccommodation())
                .setUser(UserSupplier.getUser())
                .setStatus(BookingStatus.PENDING);
    }

    public static Booking getExpiredBooking() {
        return new Booking()
                .setId(1L)
                .setCheckinDate(LocalDate.now())
                .setCheckoutDate(LocalDate.now().plusDays(5))
                .setAccommodation(AccommodationSupplier.getAccommodation())
                .setUser(UserSupplier.getUser())
                .setStatus(BookingStatus.EXPIRED);
    }

    public static Booking getCancelledBooking() {
        return new Booking()
                .setId(1L)
                .setCheckinDate(LocalDate.now())
                .setCheckoutDate(LocalDate.now().plusDays(5))
                .setAccommodation(AccommodationSupplier.getAccommodation())
                .setUser(UserSupplier.getUser())
                .setStatus(BookingStatus.CANCELED);
    }

    public static Booking getBookingOfCustomer() {
        return new Booking()
                .setId(1L)
                .setCheckinDate(LocalDate.now())
                .setCheckoutDate(LocalDate.now().plusDays(5))
                .setAccommodation(AccommodationSupplier.getAccommodation())
                .setUser(UserSupplier.getCustomerUser())
                .setStatus(BookingStatus.PENDING);
    }

    public static List<Booking> getBookingList() {
        Booking booking = new Booking()
                .setId(2L)
                .setCheckinDate(LocalDate.now())
                .setCheckoutDate(LocalDate.now().plusDays(3))
                .setAccommodation(AccommodationSupplier.getAccommodation())
                .setUser(UserSupplier.getUser())
                .setStatus(BookingStatus.EXPIRED);

        return List.of(getBooking(), booking);
    }

    public static List<Booking> getPreExpiredList() {
        Booking booking = new Booking()
                .setId(2L)
                .setCheckinDate(LocalDate.now())
                .setCheckoutDate(LocalDate.now().plusDays(1))
                .setAccommodation(AccommodationSupplier.getAccommodation())
                .setUser(UserSupplier.getUser())
                .setStatus(BookingStatus.PENDING);

        return List.of(getBooking().setCheckoutDate(LocalDate.now().plusDays(1)),
                booking);
    }

    public static List<Booking> getExpiredList() {
        Booking booking = new Booking()
                .setId(2L)
                .setCheckinDate(LocalDate.now())
                .setCheckoutDate(LocalDate.now().plusDays(1))
                .setAccommodation(AccommodationSupplier.getAccommodation())
                .setUser(UserSupplier.getUser())
                .setStatus(BookingStatus.EXPIRED);

        return List.of(getBooking()
                        .setCheckoutDate(LocalDate.now().plusDays(1))
                        .setStatus(BookingStatus.EXPIRED),
                booking);
    }

    public static BookingDto getBookingDto() {
        return new BookingDto()
                .setId(1L)
                .setCheckinDate(LocalDate.now())
                .setCheckoutDate(LocalDate.now().plusDays(3))
                .setAccommodationId(AccommodationSupplier.getAccommodation().getId())
                .setUserId(UserSupplier.getUser().getId())
                .setStatus("PENDING");
    }

    public static List<BookingDto> getBookingDtoList() {
        BookingDto booking = new BookingDto()
                .setId(2L)
                .setCheckinDate(LocalDate.now())
                .setCheckoutDate(LocalDate.now().plusDays(5))
                .setAccommodationId(AccommodationSupplier.getAccommodation().getId())
                .setUserId(UserSupplier.getUser().getId())
                .setStatus("PENDING");

        return List.of(getBookingDto(), booking);
    }

    public static CreateBookingDto getBookingCreateDto() {
        return new CreateBookingDto()
                .setCheckinDate(LocalDate.now())
                .setCheckoutDate(LocalDate.now().plusDays(5))
                .setAccommodationId(AccommodationSupplier.getAccommodation().getId())
                .setUserId(UserSupplier.getUser().getId())
                .setStatus("PENDING");
    }

    public static BookingWithAccommodationInfoDto getBookingWithAccommodationInfoDto() {
        return new BookingWithAccommodationInfoDto()
                .setId(1L)
                .setCheckinDate(LocalDate.now())
                .setCheckoutDate(LocalDate.now().plusDays(3))
                .setAccommodation(AccommodationSupplier.getAccommodationDto())
                .setUserId(UserSupplier.getUser().getId())
                .setStatus("PENDING");
    }

    public static UpdateBookingStatusDto getUpdateBookingStatusDto() {
        return new UpdateBookingStatusDto()
                .setStatus(BookingStatus.PENDING);
    }

    public static UpdateBookingDto getUpdateBookingDto() {
        return new UpdateBookingDto()
                .setCheckinDate(LocalDate.now())
                .setCheckoutDate(LocalDate.now().plusDays(3))
                .setAccommodationId(AccommodationSupplier
                        .getAccommodationDto().getId());
    }
}
