package mate.academy.carsharingservice.service.payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharingservice.dto.payment.CreatePaymentRequestDto;
import mate.academy.carsharingservice.dto.payment.CreatePaymentResponseDto;
import mate.academy.carsharingservice.dto.payment.PaymentDto;
import mate.academy.carsharingservice.exception.AccessDeniedException;
import mate.academy.carsharingservice.exception.EntityNotFoundException;
import mate.academy.carsharingservice.exception.StripeProcessingException;
import mate.academy.carsharingservice.mapper.PaymentMapper;
import mate.academy.carsharingservice.model.payment.Payment;
import mate.academy.carsharingservice.model.payment.PaymentType;
import mate.academy.carsharingservice.model.payment.Status;
import mate.academy.carsharingservice.model.rental.Rental;
import mate.academy.carsharingservice.model.user.RoleName;
import mate.academy.carsharingservice.model.user.User;
import mate.academy.carsharingservice.repository.payment.PaymentRepository;
import mate.academy.carsharingservice.repository.rental.RentalRepository;
import mate.academy.carsharingservice.service.notification.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final BigDecimal FINE_MULTIPLIER = BigDecimal.valueOf(1.5);
    private static final String PAID = "paid";
    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String PAYMENT_SUCCESS = "/payments/success";
    private static final String PAYMENT_CANCEL = "/payments/cancel";
    private static final String SESSION_ID = "session_id";

    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final PaymentMapper paymentMapper;
    private final NotificationService notificationService;

    private final String stripeApiKey = Dotenv.load().get("STRIPE_API_KEY");

    @Transactional(readOnly = true)
    @Override
    public Page<PaymentDto> getPaymentsById(Long userId, User currentUser, Pageable pageable) {
        if (!currentUser.getId().equals(userId) && !isManager(currentUser)) {
            throw new AccessDeniedException("Access denied");
        }

        return paymentRepository.findByRentalUserId(userId, pageable)
                .map(paymentMapper::paymentToPaymentDto);
    }

    @Override
    public CreatePaymentResponseDto createPayment(CreatePaymentRequestDto requestDto,
                                                  User user, String baseUrl) {
        Rental rental = rentalRepository.findById(requestDto.rentalId())
                .orElseThrow(() -> new EntityNotFoundException("Rental not found"));

        if (!rental.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You don't have access to this rental");
        }

        BigDecimal amount = calculateAmount(rental, requestDto.paymentType());
        Session session = createStripeSession(amount, baseUrl);
        createPaymentEntity(rental, session, amount, requestDto.paymentType());

        return new CreatePaymentResponseDto(session.getUrl(), session.getId());
    }

    @Override
    public ResponseEntity<Map<String, String>> handleSuccess(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));

        try {
            Session session = Session.retrieve(sessionId);

            if (PAID.equals(session.getPaymentStatus())) {
                payment.setStatus(Status.PAID);
                paymentRepository.save(payment);
                notificationService.sendNotification("Payed successful, status: "
                        + payment.getStatus());
                return ResponseEntity.ok(Map.of(STATUS, "Payment successful!"));
            }

            return ResponseEntity.badRequest().body(Map.of(ERROR, "Payment not completed"));
        } catch (StripeException e) {
            throw new StripeProcessingException("Error verifying payment: " + e.getMessage());
        }
    }

    private Session createStripeSession(BigDecimal amount, String baseUrl) {
        Stripe.apiKey = stripeApiKey;

        try {
            String successUrl = UriComponentsBuilder.fromUriString(baseUrl)
                    .path(PAYMENT_SUCCESS)
                    .queryParam(SESSION_ID, "{CHECKOUT_SESSION_ID}")
                    .build()
                    .toUriString();

            String cancelUrl = UriComponentsBuilder.fromUriString(baseUrl)
                    .path(PAYMENT_CANCEL)
                    .build()
                    .toUriString();

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("usd")
                                                    .setUnitAmount(amount.multiply(
                                                            BigDecimal.valueOf(100)).longValue()
                                                    )
                                                    .setProductData(
                                                            SessionCreateParams.LineItem
                                                                    .PriceData.ProductData
                                                                    .builder()
                                                                    .setName("Car Rental Payment")
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            return Session.create(params);

        } catch (StripeException e) {
            throw new StripeProcessingException("Stripe session creation failed: "
                    + e.getMessage());
        }
    }

    private void createPaymentEntity(Rental rental, Session session,
                                     BigDecimal amount, PaymentType paymentType) {
        Payment payment = new Payment();
        payment.setRental(rental);
        payment.setAmountToPay(amount);
        payment.setType(paymentType);
        payment.setSessionId(session.getId());
        payment.setSessionUrl(session.getUrl());
        payment.setStatus(Status.PENDING);

        paymentRepository.save(payment);
    }

    private BigDecimal calculateAmount(Rental rental, PaymentType paymentType) {
        LocalDate endDate = rental.getActualReturnDate() != null
                ? rental.getActualReturnDate() :
                LocalDate.now();

        if (paymentType == PaymentType.FINE) {
            long overdueDays = ChronoUnit.DAYS.between(rental.getReturnDate(), endDate);
            if (overdueDays <= 0) {
                overdueDays = 0;
            }
            return rental.getCar().getDailyFee()
                    .multiply(BigDecimal.valueOf(overdueDays))
                    .multiply(FINE_MULTIPLIER);
        } else {
            long rentalDays = ChronoUnit.DAYS.between(rental.getRentalDate(),
                    rental.getReturnDate());
            return rental.getCar().getDailyFee()
                    .multiply(BigDecimal.valueOf(rentalDays));
        }
    }

    private boolean isManager(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(RoleName.ROLE_MANAGER));
    }
}
