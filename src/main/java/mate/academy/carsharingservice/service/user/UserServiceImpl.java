package mate.academy.carsharingservice.service.user;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharingservice.dto.user.UpdateUserProfileRequestDto;
import mate.academy.carsharingservice.dto.user.UpdateUserRoleRequestDto;
import mate.academy.carsharingservice.dto.user.UserRegistrationRequestDto;
import mate.academy.carsharingservice.dto.user.UserResponseDto;
import mate.academy.carsharingservice.exception.EntityNotFoundException;
import mate.academy.carsharingservice.exception.RegistrationException;
import mate.academy.carsharingservice.mapper.UserMapper;
import mate.academy.carsharingservice.model.Role;
import mate.academy.carsharingservice.model.RoleName;
import mate.academy.carsharingservice.model.User;
import mate.academy.carsharingservice.repository.role.RoleRepository;
import mate.academy.carsharingservice.repository.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("User with email "
                    + requestDto.getEmail() + " already exists");
        }

        User user = userMapper.toUser(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        Role userRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                .orElseThrow(() -> new EntityNotFoundException("Role " + RoleName.ROLE_CUSTOMER
                        + " not found"));
        user.setRoles(Set.of(userRole));
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponseDto updateUserRole(Long id, UpdateUserRoleRequestDto requestDto) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find user with id: " + id)
        );

        Role userRole = roleRepository.findByName(requestDto.getRole())
                .orElseThrow(() -> new EntityNotFoundException("Role "
                        + requestDto.getRole() + " not found"));
        user.setRoles(new HashSet<>(List.of(userRole)));
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponseDto getCurrentUserProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponseDto updateMyProfile(UpdateUserProfileRequestDto requestDto,
                                           Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        userMapper.updateUserFromRequestDto(requestDto, user);
        return userMapper.toUserResponse(userRepository.save(user));
    }
}
