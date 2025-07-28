package mate.academy.carsharingservice.dto.car;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.Data;
import mate.academy.carsharingservice.model.car.Type;

@Data
public class CreateCarRequestDto {
    @NotBlank
    private String model;
    @NotBlank
    private String brand;
    private Type type;
    @NotNull
    @PositiveOrZero
    private int inventory;
    @NotNull
    @PositiveOrZero
    private BigDecimal dailyFee;
}
