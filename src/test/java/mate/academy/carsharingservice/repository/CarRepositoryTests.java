package mate.academy.carsharingservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.persistence.EntityManager;
import mate.academy.carsharingservice.model.car.Car;
import mate.academy.carsharingservice.repository.car.CarRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CarRepositoryTests {
    private static final Long ID = 1L;

    @Autowired
    private CarRepository carRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Should decrement car's inventory")
    @Sql(scripts = "classpath:database/cars/add-three-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void decrementInventory_whenInventoryIsPositive_shouldDecrementInventory() {
        Car carBefore = carRepository.findById(ID).orElseThrow();
        int inventoryBefore = carBefore.getInventory();
        assertEquals(5, inventoryBefore);

        int updated = carRepository.decrementInventory(ID);
        assertEquals(1, updated);
        entityManager.flush();
        entityManager.clear();
        Car carAfter = carRepository.findById(ID).orElseThrow();
        assertEquals(inventoryBefore - 1, carAfter.getInventory());
    }

    @Test
    @DisplayName("Should increment car's inventory")
    @Sql(scripts = "classpath:database/cars/add-three-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void incrementInventory_whenInventoryIsPositive_shouldIncrementInventory() {
        Car carBefore = carRepository.findById(ID).orElseThrow();
        int inventoryBefore = carBefore.getInventory();
        assertEquals(5, inventoryBefore);

        carRepository.incrementInventory(ID);
        entityManager.flush();
        entityManager.clear();
        Car carAfter = carRepository.findById(ID).orElseThrow();
        assertEquals(inventoryBefore + 1, carAfter.getInventory());
    }
}
