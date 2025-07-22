package mate.academy.carsharingservice.dto.payment;

import java.math.BigDecimal;
import mate.academy.carsharingservice.model.Status;

public record PaymentDto(
        Long id,
        Status status,
        String sessionId,
        BigDecimal amountToPay,
        Long userId
){}
