package mate.academy.carsharingservice.service;

import static mate.academy.carsharingservice.util.UserUtil.createUpdateUserProfileRequestDto;
import static mate.academy.carsharingservice.util.UserUtil.createUser;
import static mate.academy.carsharingservice.util.UserUtil.createValidRequest;
import static mate.academy.carsharingservice.util.UserUtil.createValidUserResponseDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import mate.academy.carsharingservice.dto.user.UpdateUserProfileRequestDto;
import mate.academy.carsharingservice.dto.user.UpdateUserRoleRequestDto;
import mate.academy.carsharingservice.dto.user.UserRegistrationRequestDto;
import mate.academy.carsharingservice.dto.user.UserResponseDto;
import mate.academy.carsharingservice.exception.EntityNotFoundException;
import mate.academy.carsharingservice.exception.RegistrationException;
import mate.academy.carsharingservice.mapper.UserMapper;
import mate.academy.carsharingservice.model.user.Role;
import mate.academy.carsharingservice.model.user.RoleName;
import mate.academy.carsharingservice.model.user.User;
import mate.academy.carsharingservice.repository.role.RoleRepository;
import mate.academy.carsharingservice.repository.user.UserRepository;
import mate.academy.carsharingservice.service.user.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    private static final Long ID = 1L;
    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "password";
    private static final String ENCODED_PASSWORD = "encodedPassword";

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Should register user with valid params and return UserResponseDto")
    void register_whenUserRegistrationRequestIsValid_shouldReturnUserResponseDto() {
        Role role = new Role();
        role.setName(RoleName.ROLE_CUSTOMER);

        User user = createUser();

        User savedUser = createUser();
        savedUser.setId(ID);

        UserResponseDto expected = createValidUserResponseDto();
        UserRegistrationRequestDto requestDto = createValidRequest();

        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(userMapper.toUser(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(roleRepository.findByName(RoleName.ROLE_CUSTOMER)).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(savedUser);
        doReturn(expected).when(userMapper).toUserResponse(any(User.class));

        UserResponseDto actual = userService.register(requestDto);

        assertEquals(actual, expected);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw registration exception when register user with invalid email")
    void register_whenUserRegistrationRequestIsInValid_shouldThrowRegistrationException() {
        UserRegistrationRequestDto requestDto = createValidRequest();
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);
        assertThrows(RegistrationException.class, () -> userService.register(requestDto));
    }

    @Test
    @DisplayName("Should throw entity not found exception when register user")
    void register_whenInValidRole_shouldThrowEntityNotFoundEx() {
        UserRegistrationRequestDto requestDto = createValidRequest();
        User user = createUser();
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(userMapper.toUser(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(roleRepository.findByName(RoleName.ROLE_CUSTOMER)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.register(requestDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should update user role when user and role exist")
    void updateUserRole_whenIdAndUpdateUserRoleRequestDtoIsValid_shouldReturnUserResponseDto() {
        Role role = new Role();
        role.setName(RoleName.ROLE_MANAGER);

        UpdateUserRoleRequestDto requestDto = new UpdateUserRoleRequestDto();
        requestDto.setRole(role.getName());

        User user = createUser();
        user.setId(ID);

        User savedUser = createUser();
        savedUser.setId(ID);

        UserResponseDto expected = createValidUserResponseDto();

        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toUserResponse(any(User.class))).thenReturn(expected);

        UserResponseDto actual = userService.updateUserRole(ID, requestDto);

        assertEquals(actual, expected);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw entity not found exception when user's id is invalid")
    void updateUserRole_whenUserIdIsInValid_shouldThrowEntityNotFoundEx() {
        UpdateUserRoleRequestDto requestDto = new UpdateUserRoleRequestDto();
        requestDto.setRole(RoleName.ROLE_MANAGER);

        when(userRepository.findById(995L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> userService.updateUserRole(995L, requestDto));
        verify(roleRepository, never()).findByName(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw entity not found exception when role name is invalid")
    void updateUserRole_whenRoleNameIsInValid_shouldThrowEntityNotFoundEx() {
        UpdateUserRoleRequestDto requestDto = new UpdateUserRoleRequestDto();
        requestDto.setRole(RoleName.ROLE_MANAGER);

        User user = createUser();
        user.setId(ID);

        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(RoleName.ROLE_MANAGER)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.updateUserRole(ID, requestDto));
        verify(userRepository).findById(ID);
        verify(roleRepository).findByName(RoleName.ROLE_MANAGER);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return current user profile")
    void getCurrentUserProfile_whenAuthenticatedUserIsValid_shouldReturnUserResponseDto() {
        User user = createUser();
        UserResponseDto expected = createValidUserResponseDto();

        when(authentication.getPrincipal()).thenReturn(user);

        when(userMapper.toUserResponse(user)).thenReturn(expected);
        UserResponseDto actual = userService.getCurrentUserProfile(authentication);
        assertEquals(actual, expected);
        verify(userMapper).toUserResponse(user);
    }

    @Test
    @DisplayName("Should update current user's profile")
    void updateMyProfile_whenRequestDataAndUserIsValid_shouldReturnUserResponseDto() {
        User user = createUser();
        UserResponseDto expected = createValidUserResponseDto();
        UpdateUserProfileRequestDto requestDto = createUpdateUserProfileRequestDto();

        when(authentication.getPrincipal()).thenReturn(user);
        doNothing().when(userMapper).updateUserFromRequestDto(requestDto, user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(expected);

        UserResponseDto actual = userService.updateMyProfile(requestDto, authentication);
        assertEquals(expected, actual);
        verify(userMapper).updateUserFromRequestDto(requestDto, user);
        verify(userRepository).save(user);
        verify(userMapper).toUserResponse(user);
    }
}
