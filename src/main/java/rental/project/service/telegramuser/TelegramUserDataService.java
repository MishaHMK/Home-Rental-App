package rental.project.service.telegramuser;

import rental.project.model.TelegramUser;

public interface TelegramUserDataService {
    TelegramUser save(String email, String chatId);

    TelegramUser getTelegramUserByChatId(String chatId);

    TelegramUser getTelegramUserByUserId(Long userId);
}
