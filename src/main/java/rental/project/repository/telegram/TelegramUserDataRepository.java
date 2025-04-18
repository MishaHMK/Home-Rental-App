package rental.project.repository.telegram;

import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rental.project.model.TelegramUser;

public interface TelegramUserDataRepository
        extends JpaRepository<TelegramUser, Long> {
    Optional<TelegramUser> findFirstByChatId(String chatId);

    Optional<TelegramUser> findFirstByUserId(Long userId);

    @Query("SELECT tu.chatId FROM TelegramUser tu ")
    Set<String> findAllTelegramChatIds();
}
