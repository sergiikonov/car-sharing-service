package mate.academy.carsharingservice.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.carsharingservice.dto.user.UpdateUserProfileRequestDto;
import mate.academy.carsharingservice.dto.user.UserRegistrationRequestDto;
import mate.academy.carsharingservice.dto.user.UserResponseDto;
import mate.academy.carsharingservice.model.user.Role;
import mate.academy.carsharingservice.model.user.RoleName;
import mate.academy.carsharingservice.model.user.User;

public class UserUtil {
    private static final Long ID = 1L;
    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "password";
    private static final String FIRST_NAME = "First";
    private static final String LAST_NAME = "Last";

    public static UserRegistrationRequestDto createValidRequest() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail(EMAIL);
        requestDto.setPassword(PASSWORD);
        requestDto.setRepeatPassword(PASSWORD);
        requestDto.setFirstName(FIRST_NAME);
        requestDto.setLastName(LAST_NAME);
        return requestDto;
    }

    public static User createUser() {
        User user = new User();
        user.setEmail(EMAIL);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        return user;
    }

    public static UserResponseDto createValidUserResponseDto() {
        var responseDto = new UserResponseDto();
        responseDto.setId(ID);
        responseDto.setEmail(EMAIL);
        responseDto.setFirstName(FIRST_NAME);
        responseDto.setLastName(LAST_NAME);
        return responseDto;
    }

    public static UpdateUserProfileRequestDto createUpdateUserProfileRequestDto() {
        var requestDto = new UpdateUserProfileRequestDto();
        requestDto.setEmail("new@example.com");
        requestDto.setPassword("password");
        requestDto.setFirstName("First1");
        requestDto.setLastName("Last2");
        return requestDto;
    }

    public static User createUserWithRoles(String... roles) {
        User user = createUser();
        user.setId(ID);
        Set<Role> roleSet = Arrays.stream(roles)
                .map(role -> {
                    Role r = new Role();
                    r.setName(RoleName.valueOf(role));
                    return r;
                })
                .collect(Collectors.toSet());
        user.setRoles(roleSet);
        return user;
    }

    public static UserResponseDto createUserResponseDto() {
        var userResponseDto = new UserResponseDto();
        userResponseDto.setId(10L);
        userResponseDto.setEmail("user1@example.com");
        userResponseDto.setFirstName(FIRST_NAME);
        userResponseDto.setLastName("LastOne");
        return userResponseDto;
    }
}
