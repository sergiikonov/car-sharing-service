package mate.academy.carsharingservice.service.user;

import mate.academy.carsharingservice.dto.user.UpdateUserProfileRequestDto;
import mate.academy.carsharingservice.dto.user.UpdateUserRoleRequestDto;
import mate.academy.carsharingservice.dto.user.UserRegistrationRequestDto;
import mate.academy.carsharingservice.dto.user.UserResponseDto;
import mate.academy.carsharingservice.exception.RegistrationException;
import org.springframework.security.core.Authentication;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    UserResponseDto updateUserRole(Long id, UpdateUserRoleRequestDto requestDto);

    UserResponseDto getCurrentUserProfile(Authentication authentication);

    UserResponseDto updateMyProfile(UpdateUserProfileRequestDto requestDto,
                                    Authentication authentication);
}
