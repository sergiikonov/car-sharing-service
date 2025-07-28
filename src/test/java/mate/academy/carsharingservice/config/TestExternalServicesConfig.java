package mate.academy.carsharingservice.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import mate.academy.carsharingservice.dto.payment.CreatePaymentResponseDto;
import mate.academy.carsharingservice.dto.payment.PaymentDto;
import mate.academy.carsharingservice.model.payment.Status;
import mate.academy.carsharingservice.service.notification.TelegramNotificationService;
import mate.academy.carsharingservice.service.payment.PaymentServiceImpl;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageImpl;

@TestConfiguration
public class TestExternalServicesConfig {

    @Bean
    public TelegramNotificationService telegramNotificationService() {
        return Mockito.mock(TelegramNotificationService.class);
    }

    @Bean
    public PaymentServiceImpl paymentService() {
        PaymentServiceImpl mock = Mockito.mock(PaymentServiceImpl.class);

        when(mock.createPayment(any(), any(), any())).thenAnswer(invocation ->
                new CreatePaymentResponseDto("test_session", "https://stripe.test/session")
        );

        PaymentDto payment = new PaymentDto(
                1L,
                Status.PAID,
                "session_11111",
                BigDecimal.valueOf(50.0),
                10L
        );

        when(mock.getPaymentsById(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(payment)));

        return mock;
    }
}
