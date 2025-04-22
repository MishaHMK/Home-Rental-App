package rental.project.supplier;

import rental.project.model.TelegramUser;

public class TelegramUserSupplier {
    public static TelegramUser getTelegramUser() {
        return new TelegramUser()
                .setId(1L)
                .setUser(UserSupplier.getUser())
                .setChatId("123456789");
    }
}
