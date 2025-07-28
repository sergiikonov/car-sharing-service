package mate.academy.carsharingservice.service.notification;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
@Transactional(readOnly = true)
public class TelegramNotificationService implements NotificationService {
    private TelegramClient telegramClient;

    @Value("${telegram.bot.token}")
    private String telegramBotToken;

    @Value("${telegram.chat.id}")
    private String chatId;

    @PostConstruct
    public void init() {
        this.telegramClient = new OkHttpTelegramClient(telegramBotToken);
    }

    @SneakyThrows
    @Override
    public void sendNotification(String message) {
        SendMessage sendMessage = SendMessage.builder()
                .text(message)
                .chatId(chatId)
                .build();
        telegramClient.execute(sendMessage);
    }
}
