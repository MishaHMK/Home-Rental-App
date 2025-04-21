package rental.project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rental.project.model.TelegramUser;
import rental.project.model.User;
import rental.project.repository.telegram.TelegramUserDataRepository;
import rental.project.security.UserDetailsServiceImpl;
import rental.project.service.telegramuser.TelegramUserDataServiceImpl;
import rental.project.supplier.TelegramUserSupplier;
import rental.project.supplier.UserSupplier;

@ExtendWith(MockitoExtension.class)
public class TelegramUserDataServiceTest {
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private TelegramUserDataRepository telegramUserDataRepository;

    @InjectMocks
    private TelegramUserDataServiceImpl tgUserDataService;

    @Test
    @DisplayName("Verify telegram user by using correct data")
    public void save_WithValidData_ReturnsTelegramUser() {
        //Given (Arrange)
        TelegramUser expected = TelegramUserSupplier.getTelegramUser();
        User user = UserSupplier.getUser();
        String email = user.getEmail();
        String chatId = expected.getChatId();

        when(userDetailsService.loadUserByUsername(email))
                .thenReturn(user);
        when(telegramUserDataRepository.save(any(TelegramUser.class)))
                .thenReturn(expected);

        //When (Act)
        TelegramUser actual = tgUserDataService.save(email, chatId);

        //Then (Assert)
        assertNotNull(actual);
        assertEquals(actual, expected);
        verify(userDetailsService).loadUserByUsername(email);
        verify(telegramUserDataRepository).save(any(TelegramUser.class));
    }

    @Test
    @DisplayName("Verify string set")
    public void getTelegramUserChatIds_WithValidData_ReturnsTelegramUser() {
        //Given (Arrange)
        Set<String> expected = Set.of("123456780", "123456789", "223456739");

        when(telegramUserDataRepository.findAllTelegramChatIds())
                .thenReturn(expected);

        //When (Act)
        Set<String> actual = tgUserDataService.getTelegramUserChatIds();

        //Then (Assert)
        assertNotNull(actual);
        assertEquals(actual, expected);
        verify(telegramUserDataRepository).findAllTelegramChatIds();
    }

    @Test
    @DisplayName("Verify telegram user by using correct chat id")
    public void getTelegramUserByChatId_WithValidData_ReturnsTelegramUser() {
        //Given (Arrange)
        TelegramUser expected = TelegramUserSupplier.getTelegramUser();
        String chatId = expected.getChatId();

        when(telegramUserDataRepository.findFirstByChatId(chatId))
                .thenReturn(Optional.of(expected));

        //When (Act)
        TelegramUser actual = tgUserDataService.getTelegramUserByChatId(chatId);

        //Then (Assert)
        assertNotNull(actual);
        assertEquals(actual, expected);
        verify(telegramUserDataRepository).findFirstByChatId(chatId);
    }

    @Test
    @DisplayName("Exception thrown by using wrong chat id")
    public void getTelegramUserByChatId_WithInvalidData_ThrowsException() {
        //Given (Arrange)
        String wrongChatId = "1212432432";

        when(telegramUserDataRepository.findFirstByChatId(wrongChatId))
                .thenReturn(Optional.empty());

        //When (Act)
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> tgUserDataService.getTelegramUserByChatId(wrongChatId));

        //Then (Assert)
        String expectedMessage = "There is no telegram user with chatId: " + wrongChatId;
        assertEquals(exception.getMessage(), expectedMessage);
        verify(telegramUserDataRepository).findFirstByChatId(wrongChatId);
    }

    @Test
    @DisplayName("Verify telegram user by using correct user id")
    public void getTelegramUserByUserId_WithValidData_ReturnsTelegramUser() {
        //Given (Arrange)
        TelegramUser expected = TelegramUserSupplier.getTelegramUser();
        Long userId = expected.getUser().getId();

        when(telegramUserDataRepository.findFirstByUserId(userId))
                .thenReturn(Optional.of(expected));

        //When (Act)
        TelegramUser actual = tgUserDataService.getTelegramUserByUserId(userId);

        //Then (Assert)
        assertNotNull(actual);
        assertEquals(actual, expected);
        verify(telegramUserDataRepository).findFirstByUserId(userId);
    }

    @Test
    @DisplayName("Exception thrown by using wrong user id")
    public void getTelegramUserByUserId_WithInvalidData_ThrowsException() {
        //Given (Arrange)
        Long wrongUserId = 1L;

        when(telegramUserDataRepository.findFirstByUserId(wrongUserId))
                .thenReturn(Optional.empty());

        //When (Act)
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> tgUserDataService.getTelegramUserByUserId(wrongUserId));

        //Then (Assert)
        String expectedMessage = "There is no telegram user with userId: " + wrongUserId;
        assertEquals(exception.getMessage(), expectedMessage);
        verify(telegramUserDataRepository).findFirstByUserId(wrongUserId);
    }
}
