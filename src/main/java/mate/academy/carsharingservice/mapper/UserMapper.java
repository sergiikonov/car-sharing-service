package mate.academy.carsharingservice.mapper;

import mate.academy.carsharingservice.dto.user.UserRegistrationRequestDto;
import mate.academy.carsharingservice.dto.user.UserResponseDto;
import mate.academy.carsharingservice.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
        UserResponseDto toUserResponse(User user);

        User toUser(UserRegistrationRequestDto dto);
}
