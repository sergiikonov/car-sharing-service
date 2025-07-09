package mate.academy.carsharingservice.dto.car;

import java.math.BigDecimal;
import lombok.Data;
import mate.academy.carsharingservice.model.Type;

@Data
public class CarDto {
    private Long id;
    private String model;
    private String brand;
    private Type type;
    private int inventory;
    private BigDecimal dailyFee;
}
