package mate.academy.carsharingservice.dto.payment;

public record CreatePaymentResponseDto(
        String url,
        String sessionId
) {
}
