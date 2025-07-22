package mate.academy.carsharingservice.service.payment;

import java.util.Map;
import mate.academy.carsharingservice.dto.payment.CreatePaymentRequestDto;
import mate.academy.carsharingservice.dto.payment.CreatePaymentResponseDto;
import mate.academy.carsharingservice.dto.payment.PaymentDto;
import mate.academy.carsharingservice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface PaymentService {
    Page<PaymentDto> getPaymentsById(Long id, User user, Pageable pageable);

    CreatePaymentResponseDto createPayment(
            CreatePaymentRequestDto requestDto,
            User user,
            String baseUrl
    );

    ResponseEntity<Map<String, String>> handleSuccess(String sessionId);
}
