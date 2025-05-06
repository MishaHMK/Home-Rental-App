package rental.project.notification;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import rental.project.dto.accommodation.AccommodationDto;
import rental.project.dto.booking.BookingDto;
import rental.project.dto.payment.PaymentDto;
import rental.project.model.Accommodation;
import rental.project.service.telegramuser.TelegramUserDataService;
import rental.project.telegram.TelegramBot;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final TelegramBot telegramBot;
    private final TelegramUserDataService telegramUserDataService;

    @Async
    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAccommodationCreation(AccommodationDto accommodationDto) {
        if (verifyConnection()) {
            StringBuilder messageData = new StringBuilder(
                    "Accommodation successfully created!")
                    .append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append("<=====================================>")
                    .append(System.lineSeparator())
                    .append("Accommodation #").append(accommodationDto.getId())
                    .append(System.lineSeparator())
                    .append("\t Type: ").append(accommodationDto.getType())
                    .append(System.lineSeparator())
                    .append("\t Structure: ").append(accommodationDto.getSize())
                    .append(System.lineSeparator())
                    .append("\t Daily Price: ").append(accommodationDto.getDailyRate())
                    .append(System.lineSeparator())
                    .append("\t Available units: ").append(accommodationDto.getAvailability())
                    .append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append("Address ")
                    .append(System.lineSeparator())
                    .append("\t Country: ").append(accommodationDto.getAddressDto().getCountry())
                    .append(System.lineSeparator())
                    .append("\t City: ").append(accommodationDto.getAddressDto().getCity())
                    .append(System.lineSeparator())
                    .append("\t Street: ").append(accommodationDto.getAddressDto().getStreet())
                    .append(System.lineSeparator())
                    .append("<=====================================>");

            sendMessagesToAllBots(messageData.toString());
        }
    }

    @Async
    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAccommodationRelease(Accommodation accommodation) {
        if (verifyConnection()) {
            StringBuilder messageData = new StringBuilder(
                    "Accommodation #")
                    .append(accommodation.getId())
                    .append(" released a spot")
                    .append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append("<=====================================>")
                    .append(System.lineSeparator())
                    .append("\t Type: ").append(accommodation.getType())
                    .append(System.lineSeparator())
                    .append("\t Structure: ").append(accommodation.getSize())
                    .append(System.lineSeparator())
                    .append("\t Daily Price: ").append(accommodation.getDailyRate())
                    .append(System.lineSeparator())
                    .append("Address ")
                    .append(System.lineSeparator())
                    .append("\t Country: ").append(accommodation.getAddress().getCountry())
                    .append(System.lineSeparator())
                    .append("\t City: ").append(accommodation.getAddress().getCity())
                    .append(System.lineSeparator())
                    .append("\t Street: ").append(accommodation.getAddress().getStreet())
                    .append(System.lineSeparator())
                    .append("<=====================================>");

            sendMessagesToAllBots(messageData.toString());
        }
    }

    @Async
    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookingCreation(BookingDto bookingDto) {
        if (verifyConnection()) {
            StringBuilder messageData = new StringBuilder(
                    "Booking successfully created!")
                    .append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append("<=====================================>")
                    .append(System.lineSeparator())
                    .append("Booking #").append(bookingDto.getId())
                    .append(System.lineSeparator())
                    .append("\t Status: ").append(bookingDto.getStatus())
                    .append(System.lineSeparator())
                    .append("\t Check in date: ").append(bookingDto.getCheckinDate())
                    .append(System.lineSeparator())
                    .append("\t Check out date: ").append(bookingDto.getCheckoutDate())
                    .append(System.lineSeparator())
                    .append("\t Accommodation #").append(bookingDto.getAccommodationId())
                    .append(System.lineSeparator())
                    .append("\t User #").append(bookingDto.getUserId())
                    .append(System.lineSeparator())
                    .append("<=====================================>");

            sendMessagesToAllBots(messageData.toString());
        }
    }

    @Async
    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookingCancellation(BookingDto bookingDto) {
        if (verifyConnection()) {
            StringBuilder messageData = new StringBuilder(
                    "Booking #").append(bookingDto.getId())
                    .append(" had been cancelled!")
                    .append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append("<=====================================>")
                    .append(System.lineSeparator())
                    .append("\t Status: ").append(bookingDto.getStatus())
                    .append(System.lineSeparator())
                    .append("\t Check in date: ").append(bookingDto.getCheckinDate())
                    .append(System.lineSeparator())
                    .append("\t Check out date: ").append(bookingDto.getCheckoutDate())
                    .append(System.lineSeparator())
                    .append("\t Accommodation #").append(bookingDto.getAccommodationId())
                    .append(System.lineSeparator())
                    .append("\t User #").append(bookingDto.getUserId())
                    .append(System.lineSeparator())
                    .append("<=====================================>");

            sendMessagesToAllBots(messageData.toString());
        }
    }

    @Async
    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSuccessfulPayment(PaymentDto paymentDto) {
        if (verifyConnection()) {
            StringBuilder messageData = new StringBuilder(
                    "Payment #").append(paymentDto.getId())
                    .append(" had been successfully paid!")
                    .append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append("<=====================================>")
                    .append(System.lineSeparator())
                    .append("\t Booking #").append(paymentDto.getBookingId())
                    .append(System.lineSeparator())
                    .append("\t Status: ").append(paymentDto.getStatus())
                    .append(System.lineSeparator())
                    .append("\t Total amount: $").append(paymentDto.getAmount())
                    .append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append("\t Session address: ")
                    .append(System.lineSeparator())
                    .append(paymentDto.getSessionUrl())
                    .append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append("\t Session id ")
                    .append(System.lineSeparator())
                    .append(paymentDto.getSessionId())
                    .append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append("<=====================================>");

            sendMessagesToAllBots(messageData.toString());
        }
    }

    @Async
    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCancelledPayment(PaymentDto paymentDto) {
        if (verifyConnection()) {
            StringBuilder messageData = new StringBuilder(
                    "Payment #").append(paymentDto.getId())
                    .append(" had been cancelled!")
                    .append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append("<=====================================>")
                    .append(System.lineSeparator())
                    .append("\t Booking #").append(paymentDto.getBookingId())
                    .append(System.lineSeparator())
                    .append("\t Status: ").append(paymentDto.getStatus())
                    .append(System.lineSeparator())
                    .append("\t Total amount: $").append(paymentDto.getAmount())
                    .append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append("\t Session address: ")
                    .append(System.lineSeparator())
                    .append(paymentDto.getSessionUrl())
                    .append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append("\t Session id ")
                    .append(System.lineSeparator())
                    .append(paymentDto.getSessionId())
                    .append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append("<=====================================>");

            sendMessagesToAllBots(messageData.toString());
        }
    }

    private void sendMessagesToAllBots(String message) {
        Set<String> telegramUserChatIds = telegramUserDataService.getTelegramUserChatIds();
        for (String telegramUserChatId : telegramUserChatIds) {
            telegramBot.sendMessage(telegramUserChatId, message);
        }
    }

    private boolean verifyConnection() {
        try {
            telegramBot.execute(new GetMe());
            return true;
        } catch (TelegramApiException e) {
            return false;
        }
    }
}
