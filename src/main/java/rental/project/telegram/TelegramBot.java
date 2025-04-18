package rental.project.telegram;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import rental.project.exception.TelegramBotMessageException;
import rental.project.repository.telegram.TelegramUserDataRepository;
import rental.project.service.telegramuser.TelegramUserDataServiceImpl;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Value("${telegram.bot.name}")
    private String botName;
    private final TelegramUserDataServiceImpl telegramUserDataInfoService;
    private final TelegramUserDataRepository telegramUserDataRepository;

    public TelegramBot(@Value("${telegram.bot.token}") String botToken,
                       TelegramUserDataRepository repository,
                       TelegramUserDataServiceImpl service) {
        super(botToken);
        this.telegramUserDataInfoService = service;
        this.telegramUserDataRepository = repository;
    }

    @Override
    public void onUpdateReceived(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        if (update.hasMessage() && update.getMessage().hasText()
                && telegramUserDataRepository.findFirstByChatId(chatId).isEmpty()) {
            String message = update.getMessage().getText();
            if (message.equals("/start")) {
                introMessage(chatId);
            } else {
                addTelegramUser(message, chatId);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    public void sendMessage(String chatId, String messageText) {
        if (chatId == null) {
            throw new TelegramBotMessageException("There is no chat id");
        }
        SendMessage newMessage = new SendMessage(chatId, messageText);
        try {
            execute(newMessage);
        } catch (TelegramApiException e) {
            if (e.getMessage().contains("bot was blocked by the user")) {
                System.out.println("Bot was blocked by the user. Chat ID: " + chatId);
            }
            else if (e.getMessage().contains("chat not found")) {
                System.out.println("The chat was removed or the user deleted their account. Chat ID: " + chatId);
            }
            else {
                throw new TelegramBotMessageException("Couldn't send a message: " + messageText, e);
            }
        }
    }

    private void introMessage(String chatId) {
        String message = """
                 Welcome! \n
                This bot will notify you about:
                    - Bookings creation / cancellation / expiration in system
                    - Accommodation creation / release in system
                    - Details about successful payments in system \n
                Write down your manager account login to proceed
                """;
        sendMessage(chatId, message);
    }

    private void addTelegramUser(String email, String chatId) {
        try {
            telegramUserDataInfoService.save(email, chatId);
            sendMessage(chatId, "Successfully authorized: " + email);
        } catch (Exception e) {
            throw new EntityNotFoundException("User with given email does not exist or"
                    + " this account does not have manager permissions! Given permission: "
                    + email);
        }
    }
}
