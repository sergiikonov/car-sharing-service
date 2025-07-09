package mate.academy.carsharingservice.service.user;

import lombok.RequiredArgsConstructor;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

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

        Role userRole = roleRepository.findByName(RoleName.CUSTOMER)
                .orElseThrow(() -> new EntityNotFoundException("Role " + RoleName.CUSTOMER
                        + " not found"));
        user.setRoles(Set.of(userRole));
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }
}
