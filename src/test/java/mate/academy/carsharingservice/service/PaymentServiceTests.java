package mate.academy.carsharingservice.service;

import static mate.academy.carsharingservice.util.PaymentUtil.createPayment;
import static mate.academy.carsharingservice.util.PaymentUtil.createPaymentDto;
import static mate.academy.carsharingservice.util.PaymentUtil.createPaymentResponseDto;
import static mate.academy.carsharingservice.util.RentalUtil.createCar;
import static mate.academy.carsharingservice.util.RentalUtil.createRental;
import static mate.academy.carsharingservice.util.UserUtil.createUser;
import static mate.academy.carsharingservice.util.UserUtil.createUserWithRoles;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import mate.academy.carsharingservice.dto.payment.CreatePaymentRequestDto;
import mate.academy.carsharingservice.dto.payment.CreatePaymentResponseDto;
import mate.academy.carsharingservice.dto.payment.PaymentDto;
import mate.academy.carsharingservice.exception.AccessDeniedException;
import mate.academy.carsharingservice.mapper.PaymentMapper;
import mate.academy.carsharingservice.model.payment.Payment;
import mate.academy.carsharingservice.model.payment.PaymentType;
import mate.academy.carsharingservice.repository.payment.PaymentRepository;
import mate.academy.carsharingservice.repository.rental.RentalRepository;
import mate.academy.carsharingservice.service.payment.PaymentServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTests {
    private static final Long ID = 1L;
    private static final Long USER_ID = 1L;

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private Pageable pageable;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    @DisplayName("Should return all payments for manager")
    public void getPaymentsById_whenUserIsManager_shouldReturnPageWithPaymentDto() {
        Payment payment = createPayment();
        PaymentDto paymentDto = createPaymentDto();
        Page<Payment> paymentPage = new PageImpl<>(List.of(payment));
        when(paymentRepository.findByRentalUserId(USER_ID, pageable)).thenReturn(paymentPage);
        when(paymentMapper.paymentToPaymentDto(payment)).thenReturn(paymentDto);

        var manager = createUserWithRoles("ROLE_MANAGER");

        Page<PaymentDto> result = paymentService.getPaymentsById(USER_ID, manager, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(paymentDto, result.getContent().get(0));
    }

    @Test
    @DisplayName("""
            Should throw AccessDeniedException when user is
            not manager and tries to get foreign payments
            """)
    public void getPaymentsById_whenUserNotManager_shouldThrowAccessDeniedException() {
        var customer = createUser();
        customer.setId(ID);
        customer.setRoles(Collections.emptySet());
        Long otherUserId = 2L;

        assertThrows(
                AccessDeniedException.class,
                () -> paymentService.getPaymentsById(otherUserId, customer, pageable)
        );
    }

    @Test
    @DisplayName("Should create new payment")
    public void createPayment_whenRentalExistsWithCurrentUser_shouldReturnCreatePaymentResponse() {
        var customer = createUser();
        customer.setId(ID);
        var rental = createRental(customer, createCar());
        when(rentalRepository.findById(ID)).thenReturn(Optional.of(rental));
        var requestDto = new CreatePaymentRequestDto(ID, PaymentType.PAYMENT);

        Session session = mock(Session.class);
        when(session.getUrl()).thenReturn("url");
        when(session.getId()).thenReturn("session_id");

        Stripe.apiKey = "sk_test_dummyKey";

        try (var mocked = mockStatic(Session.class)) {
            mocked.when(() -> Session
                    .create(any(SessionCreateParams.class))).thenReturn(session);

            CreatePaymentResponseDto actual = paymentService.createPayment(
                    requestDto,
                    customer,
                    "http://localhost"
            );

            CreatePaymentResponseDto expected = createPaymentResponseDto();
            assertEquals(expected.url(), actual.url());
            assertEquals(expected.sessionId(), actual.sessionId());
        }
    }
}
