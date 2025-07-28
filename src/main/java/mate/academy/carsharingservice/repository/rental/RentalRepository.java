package mate.academy.carsharingservice.repository.rental;

import java.util.Optional;
import mate.academy.carsharingservice.model.rental.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    @EntityGraph(attributePaths = {"car", "user"})
    Page<Rental> findByUserIdAndActualReturnDateIsNull(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"car", "user"})
    Page<Rental> findByUserIdAndActualReturnDateIsNotNull(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"car", "user"})
    Page<Rental> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"car", "user"})
    Page<Rental> findByActualReturnDateIsNull(Pageable pageable);

    @EntityGraph(attributePaths = {"car", "user"})
    Page<Rental> findByActualReturnDateIsNotNull(Pageable pageable);

    @Query("SELECT r FROM Rental r JOIN FETCH r.car WHERE r.id = :id")
    Optional<Rental> findByIdWithCar(@Param("id") Long id);

    @Query("SELECT r FROM Rental r JOIN FETCH r.user WHERE r.id = :id")
    Optional<Rental> findByIdWithUser(@Param("id") Long id);
}
