package rental.project.telegram;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import rental.project.repository.telegram.TelegramUserDataRepository;
import rental.project.service.telegramuser.TelegramUserDataServiceImpl;

@Component
public class BotRegister {
    @Value("${telegram.bot.name}")
    private String botName;
    @Value("${telegram.bot.token}")
    private String botToken;

    private final TelegramUserDataServiceImpl userDataService;
    private final TelegramUserDataRepository userDataRepository;

    private TelegramBot telegramBot;

    public BotRegister(TelegramUserDataServiceImpl userDataService,
                                TelegramUserDataRepository userDataRepository) {
        this.userDataService = userDataService;
        this.userDataRepository = userDataRepository;
    }

    @PostConstruct
    public void registerBot() throws TelegramApiException {
        try {
            telegramBot = new TelegramBot(botToken, botName, userDataService, userDataRepository);
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            System.err.println("Failed to register Telegram bot: " + e.getMessage());
        }
    }

    @Bean
    public TelegramBot telegramBot() {
        return telegramBot;
    }
}
