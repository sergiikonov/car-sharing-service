package mate.academy.carsharingservice.repository.car;

import mate.academy.carsharingservice.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CarRepository extends JpaRepository<Car, Long> {
    @Modifying
    @Query("UPDATE Car c SET c.inventory = c.inventory - 1 WHERE c.id = :id AND c.inventory > 0")
    int decrementInventory(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Car c SET c.inventory = c.inventory + 1 WHERE c.id = :id")
    void incrementInventory(@Param("id") Long id);
}
