package rental.project.repository.telegram;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import rental.project.model.TelegramUser;

public interface TelegramUserDataRepository
        extends JpaRepository<TelegramUser, Long> {
    Optional<TelegramUser> findFirstByChatId(String chatId);

    Optional<TelegramUser> findFirstByUserId(Long userId);
}
