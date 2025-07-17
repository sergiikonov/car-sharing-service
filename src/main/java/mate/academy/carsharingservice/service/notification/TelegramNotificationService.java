package mate.academy.carsharingservice.service.notification;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
@Transactional(readOnly = true)
public class TelegramNotificationService implements NotificationService {
    private final TelegramClient telegramClient;

    public TelegramNotificationService() {
        this.telegramClient = new OkHttpTelegramClient(
                Dotenv.load().get("TELEGRAM_BOT_TOKEN")
        );
    }

    @SneakyThrows
    @Override
    public void sendNotification(String message) {
        SendMessage sendMessage = SendMessage
                .builder()
                .text(message)
                .chatId(Dotenv.load().get("TELEGRAM_CHAT_ID"))
                .build();
        telegramClient.execute(sendMessage);
    }
}
