package mate.academy.carsharingservice.dto.payment;

import jakarta.validation.constraints.NotNull;
import mate.academy.carsharingservice.model.PaymentType;

public record CreatePaymentRequestDto(
        @NotNull
        Long rentalId,
        @NotNull
        PaymentType paymentType
) {
}
