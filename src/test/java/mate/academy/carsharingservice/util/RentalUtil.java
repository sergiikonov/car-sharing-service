package mate.academy.carsharingservice.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import mate.academy.carsharingservice.dto.rental.RentalRequestDto;
import mate.academy.carsharingservice.dto.rental.RentalResponseDto;
import mate.academy.carsharingservice.model.car.Car;
import mate.academy.carsharingservice.model.car.Type;
import mate.academy.carsharingservice.model.rental.Rental;
import mate.academy.carsharingservice.model.user.User;

public class RentalUtil {
    private static final Long CAR_ID = 1L;
    private static final Long ID = 1L;
    private static final LocalDate RENTAL_DATE = LocalDate.now();
    private static final LocalDate RETURN_DATE = LocalDate.now().plusDays(1);
    private static final int INVENTORY = 1;
    private static final String MODEL = "Model";
    private static final String BRAND = "Brand";
    private static final BigDecimal DAILY_FEE = BigDecimal.valueOf(1.5);

    public static RentalRequestDto createRentalRequestDto() {
        return new RentalRequestDto(CAR_ID, RENTAL_DATE, RETURN_DATE);
    }

    public static Car createCar() {
        Car car = new Car();
        car.setId(CAR_ID);
        car.setType(Type.UNIVERSAL);
        car.setInventory(INVENTORY);
        car.setModel(MODEL);
        car.setBrand(BRAND);
        car.setDailyFee(DAILY_FEE);
        return car;
    }

    public static Rental createRental(User user, Car car) {
        Rental rental = new Rental();
        rental.setRentalDate(RENTAL_DATE);
        rental.setReturnDate(RETURN_DATE);
        rental.setId(ID);
        rental.setUser(user);
        rental.setCar(car);
        rental.setActualReturnDate(null);
        return rental;
    }

    public static RentalResponseDto createRentalResponseDto(User user) {
        return new RentalResponseDto(ID, CAR_ID, MODEL, user.getId(),
                user.getEmail(), RENTAL_DATE, RETURN_DATE, null);
    }
}
