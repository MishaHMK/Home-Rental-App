package rental.project.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<Map<String, Object>> handleRegistrationException(
            RegistrationException ex) {
        return new ResponseEntity<>(buildExceptionResponse(ex),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentException(
            PaymentException ex) {
        return new ResponseEntity<>(buildExceptionResponse(ex),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(
            EntityNotFoundException ex) {
        return new ResponseEntity<>(buildExceptionResponse(ex),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessException.class)
    public ResponseEntity<Map<String, Object>> handleAccessException(
            AccessException ex) {
        return new ResponseEntity<>(buildExceptionResponse(ex),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TelegramBotMessageException.class)
    public ResponseEntity<Map<String, Object>> handleTelegramBotMessageException(
            TelegramBotMessageException ex) {
        return new ResponseEntity<>(buildExceptionResponse(ex),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookingException.class)
    public ResponseEntity<Map<String, Object>> handleBookingException(
            BookingException ex) {
        return new ResponseEntity<>(buildExceptionResponse(ex),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TelegramApiException.class)
    public ResponseEntity<Map<String, Object>> handleTelegramApiException(
            TelegramApiException ex) {
        return new ResponseEntity<>(buildExceptionResponse(ex),
                HttpStatus.BAD_REQUEST);
    }

    private Map<String, Object> buildExceptionResponse(Throwable ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("error", ex.getMessage());
        return body;
    }
}
