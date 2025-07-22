package mate.academy.carsharingservice.repository.payment;

import java.util.Optional;
import mate.academy.carsharingservice.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @EntityGraph(attributePaths = {"rental.user"})
    Page<Payment> findByRentalUserId(Long userId, Pageable pageable);

    Optional<Payment> findBySessionId(String id);
}
