package rental.project.service.notificaiton;

import rental.project.dto.accommodation.AccommodationDto;
import rental.project.dto.booking.BookingDto;
import rental.project.dto.payment.PaymentDto;
import rental.project.model.Accommodation;

public interface NotificationService {
    void onAccommodationCreation(AccommodationDto accommodationDto);

    void onAccommodationRelease(Accommodation accommodation);

    void onBookingCreation(BookingDto bookingDto);

    void onBookingCancellation(BookingDto bookingDto);

    void onSuccessfulPayment(PaymentDto paymentDto);
}
