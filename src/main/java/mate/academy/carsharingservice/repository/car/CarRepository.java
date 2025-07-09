package mate.academy.carsharingservice.repository.car;

import mate.academy.carsharingservice.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}
