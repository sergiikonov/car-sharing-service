package mate.academy.carsharingservice.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import mate.academy.carsharingservice.model.RoleName;

@Data
public class UpdateUserRoleRequestDto {
    @NotNull
    private RoleName role;
}
