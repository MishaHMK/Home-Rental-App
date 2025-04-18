package rental.project.service.telegramuser;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rental.project.model.TelegramUser;
import rental.project.model.User;
import rental.project.repository.telegram.TelegramUserDataRepository;
import rental.project.security.UserDetailsServiceImpl;

@Service
@Transactional
@RequiredArgsConstructor
public class TelegramUserDataServiceImpl
        implements TelegramUserDataService {
    private final UserDetailsServiceImpl userDetailsService;
    private final TelegramUserDataRepository telegramUserDataRepository;

    @Override
    public TelegramUser save(String email, String chatId) {
        User user = (User)userDetailsService.loadUserByUsername(email);
        TelegramUser telegramUser = new TelegramUser()
                .setChatId(chatId)
                .setUser((user));
        return telegramUserDataRepository.save(telegramUser);
    }

    @Override
    public TelegramUser getTelegramUserByChatId(String chatId) {
        return telegramUserDataRepository.findFirstByChatId(chatId)
               .orElseThrow(() -> new EntityNotFoundException(
                       "There is no telegram user with chatId: " + chatId
               ));
    }

    @Override
    public TelegramUser getTelegramUserByUserId(Long userId) {
        return telegramUserDataRepository.findFirstByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "There is no telegram user with userId: " + userId
                ));
    }
}
