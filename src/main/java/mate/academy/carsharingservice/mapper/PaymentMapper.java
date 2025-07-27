package mate.academy.carsharingservice.mapper;

import mate.academy.carsharingservice.dto.payment.PaymentDto;
import mate.academy.carsharingservice.model.payment.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @Mapping(target = "userId", source = "rental.user.id")
    PaymentDto paymentToPaymentDto(Payment payment);
}
