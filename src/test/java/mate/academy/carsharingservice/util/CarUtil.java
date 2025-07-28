package mate.academy.carsharingservice.util;

import java.math.BigDecimal;
import mate.academy.carsharingservice.dto.car.CarDto;
import mate.academy.carsharingservice.dto.car.CreateCarRequestDto;
import mate.academy.carsharingservice.model.car.Type;

public class CarUtil {
    private static final String BRAND = "Toyota";
    private static final String MODEL = "Corolla";
    private static final int INVENTORY = 5;
    private static final BigDecimal DAILY_FEE = BigDecimal.valueOf(40);

    public static CarDto createCarDto(Long id, String brand,
                                      String model, Type type,
                                      int inventory, BigDecimal dailyFee) {
        CarDto carDto = new CarDto();
        carDto.setId(id);
        carDto.setBrand(brand);
        carDto.setModel(model);
        carDto.setType(type);
        carDto.setInventory(inventory);
        carDto.setDailyFee(dailyFee);
        return carDto;
    }

    public static CreateCarRequestDto createCarRequestDto() {
        CreateCarRequestDto requestDto = new CreateCarRequestDto();
        requestDto.setBrand(BRAND);
        requestDto.setModel(MODEL);
        requestDto.setType(Type.SEDAN);
        requestDto.setInventory(INVENTORY);
        requestDto.setDailyFee(DAILY_FEE);
        return requestDto;
    }
}
