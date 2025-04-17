package rental.project.exception;

public class TelegramBotMessageException extends RuntimeException {
    public TelegramBotMessageException(String message) {
        super(message);
    }

    public TelegramBotMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
