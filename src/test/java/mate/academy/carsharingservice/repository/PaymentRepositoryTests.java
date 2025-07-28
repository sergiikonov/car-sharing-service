package mate.academy.carsharingservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import mate.academy.carsharingservice.model.payment.Payment;
import mate.academy.carsharingservice.repository.payment.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PaymentRepositoryTests {
    private static final Long USER_ID = 10L;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("Should return payments for specific user")
    @Sql(scripts = {
            "classpath:database/cars/add-three-cars.sql",
            "classpath:database/users/add-three-users.sql",
            "classpath:database/rentals/add-rentals-for-payments.sql",
            "classpath:database/payments/add-payments.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findByRentalUserId_whenRentalExists_shouldReturnPaymentsForUser() {
        PageRequest pageable = PageRequest.of(0, 10);

        Page<Payment> result = paymentRepository.findByRentalUserId(USER_ID, pageable);

        assertEquals(1, result.getTotalElements());
        result.forEach(payment ->
                assertEquals(USER_ID, payment.getRental().getUser().getId()));
    }

    @Test
    @DisplayName("Should return payment with matching session ID")
    @Sql(scripts = {
            "classpath:database/cars/add-three-cars.sql",
            "classpath:database/users/add-three-users.sql",
            "classpath:database/rentals/add-rentals-for-payments.sql",
            "classpath:database/payments/add-single-payment.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findBySessionId_whenSessionIdCorrect_shouldReturnPayment() {
        String sessionId = "session_12345";

        Optional<Payment> result = paymentRepository.findBySessionId(sessionId);

        assertTrue(result.isPresent());
        assertEquals(sessionId, result.get().getSessionId());
        assertNotNull(result.get().getRental());
    }
}
