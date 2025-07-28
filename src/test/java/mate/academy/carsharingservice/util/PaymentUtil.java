package mate.academy.carsharingservice.util;

import static mate.academy.carsharingservice.util.RentalUtil.createCar;
import static mate.academy.carsharingservice.util.RentalUtil.createRental;
import static mate.academy.carsharingservice.util.UserUtil.createUser;

import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import mate.academy.carsharingservice.dto.payment.CreatePaymentResponseDto;
import mate.academy.carsharingservice.dto.payment.PaymentDto;
import mate.academy.carsharingservice.model.payment.Payment;
import mate.academy.carsharingservice.model.payment.PaymentType;
import mate.academy.carsharingservice.model.payment.Status;
import org.springframework.web.util.UriComponentsBuilder;

public class PaymentUtil {
    private static final Long ID = 1L;
    private static final Long USER_ID = 1L;
    private static final String SESSION_ID = "session_id";
    private static final String PAYMENT_SUCCESS = "/payments/success";
    private static final String BASE_URL = "Base url";
    private static final String URL = "url";

    public static Payment createPayment() {
        Payment payment = new Payment();
        payment.setType(PaymentType.PAYMENT);
        payment.setRental(createRental(createUser(), createCar()));
        payment.setStatus(Status.PENDING);
        payment.setSessionUrl(UriComponentsBuilder.fromUriString(BASE_URL)
                .path(PAYMENT_SUCCESS)
                .queryParam(SESSION_ID, "{CHECKOUT_SESSION_ID}")
                .build()
                .toUriString());
        payment.setSessionId(String.valueOf(new Session()));
        payment.setId(ID);
        payment.setAmountToPay(BigDecimal.ONE);
        return payment;
    }

    public static PaymentDto createPaymentDto() {
        return new PaymentDto(ID, Status.PENDING, SESSION_ID, BigDecimal.ONE, USER_ID);
    }

    public static CreatePaymentResponseDto createPaymentResponseDto() {
        return new CreatePaymentResponseDto(URL, SESSION_ID);
    }
}
