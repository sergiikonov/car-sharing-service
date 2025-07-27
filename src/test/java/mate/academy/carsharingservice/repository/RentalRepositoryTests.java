package mate.academy.carsharingservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import mate.academy.carsharingservice.model.rental.Rental;
import mate.academy.carsharingservice.repository.rental.RentalRepository;
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
public class RentalRepositoryTests {
    private static final Long FIRST_USER_ID = 10L;
    private static final Long SECOND_USER_ID = 20L;

    @Autowired
    private RentalRepository rentalRepository;

    @Test
    @DisplayName("Should return active rentals for user")
    @Sql(scripts = {
            "classpath:database/cars/add-three-cars.sql",
            "classpath:database/users/add-three-users.sql",
            "classpath:database/rentals/add-rentals-for-user-active.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByUserIdAndActualReturnDateIsNull_whenActualReturnDateIsNull_shouldReturnAcRentals() {
        PageRequest pageable = PageRequest.of(0, 10);

        Page<Rental> result = rentalRepository.findByUserIdAndActualReturnDateIsNull(
                FIRST_USER_ID, pageable
        );

        assertEquals(1, result.getTotalElements());
        result.forEach(rental -> {
            assertEquals(FIRST_USER_ID, rental.getUser().getId());
            assertNull(rental.getActualReturnDate());
        });
    }

    @Test
    @DisplayName("Should return completed rentals for user")
    @Sql(scripts = {
            "classpath:database/cars/add-three-cars.sql",
            "classpath:database/users/add-three-users.sql",
            "classpath:database/rentals/add-rentals-for-user-completed.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByUserIdAndActualReturnDateIsNotNull_whenAcRetDateNotNull_shouldCompletedRentals() {
        PageRequest pageable = PageRequest.of(0, 10);

        Page<Rental> result = rentalRepository
                .findByUserIdAndActualReturnDateIsNotNull(FIRST_USER_ID, pageable);

        assertEquals(1, result.getTotalElements());
        result.forEach(rental -> {
            assertEquals(FIRST_USER_ID, rental.getUser().getId());
            assertNotNull(rental.getActualReturnDate());
        });
    }

    @Test
    @DisplayName("Should return all rentals for user")
    @Sql(scripts = {
            "classpath:database/cars/add-three-cars.sql",
            "classpath:database/users/add-three-users.sql",
            "classpath:database/rentals/add-rentals-for-user-all.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByUserId_whenFindByUserId_shouldReturnAllRentalsForUser() {
        PageRequest pageable = PageRequest.of(0, 10);

        Page<Rental> result = rentalRepository.findByUserId(SECOND_USER_ID, pageable);

        assertEquals(2, result.getTotalElements());
        result.forEach(rental -> assertEquals(SECOND_USER_ID, rental.getUser().getId()));
    }

    @Test
    @DisplayName("Should return all active rentals")
    @Sql(scripts = {
            "classpath:database/cars/add-three-cars.sql",
            "classpath:database/users/add-three-users.sql",
            "classpath:database/rentals/add-rentals-active.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByActualReturnDateIsNull_whenActualReturnDateNull_shouldReturnActiveRentals() {
        PageRequest pageable = PageRequest.of(0, 10);

        Page<Rental> result = rentalRepository.findByActualReturnDateIsNull(pageable);

        assertEquals(3, result.getTotalElements());
        result.forEach(rental -> assertNull(rental.getActualReturnDate()));
    }

    @Test
    @DisplayName("Should return all completed rentals")
    @Sql(scripts = {
            "classpath:database/cars/add-three-cars.sql",
            "classpath:database/users/add-three-users.sql",
            "classpath:database/rentals/add-rentals-completed.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByActualReturnDateIsNotNull_whenActReturnDateNotNull_shouldReturnCompletedRentals() {
        PageRequest pageable = PageRequest.of(0, 10);

        Page<Rental> result = rentalRepository.findByActualReturnDateIsNotNull(pageable);

        assertEquals(2, result.getTotalElements());
        result.forEach(rental -> assertNotNull(rental.getActualReturnDate()));
    }

    @Test
    @DisplayName("Should return rental with car loaded")
    @Sql(scripts = {
            "classpath:database/cars/add-three-cars.sql",
            "classpath:database/users/add-three-users.sql",
            "classpath:database/rentals/add-single-rental.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByIdWithCar_whenWithCarId_shouldReturnRentalWithCar() {
        Optional<Rental> result = rentalRepository.findByIdWithCar(100L);

        assertTrue(result.isPresent());
        assertNotNull(result.get().getCar());
    }

    @Test
    @DisplayName("Should return rental with user loaded")
    @Sql(scripts = {
            "classpath:database/cars/add-three-cars.sql",
            "classpath:database/users/add-three-users.sql",
            "classpath:database/rentals/add-single-rental.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByIdWithUser_whenFindByIdWithCar_shouldReturnRentalWithCar() {
        Optional<Rental> result = rentalRepository.findByIdWithUser(100L);

        assertTrue(result.isPresent());
        assertNotNull(result.get().getUser());
    }
}
