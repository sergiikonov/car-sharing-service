package mate.academy.carsharingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharingservice.dto.user.UpdateUserProfileRequestDto;
import mate.academy.carsharingservice.dto.user.UpdateUserRoleRequestDto;
import mate.academy.carsharingservice.dto.user.UserResponseDto;
import mate.academy.carsharingservice.service.user.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Controller", description = "Operations with user")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update user's role method",
            description = "Update role")
    @PutMapping("/{id}/role")
    public UserResponseDto updateUserRole(@PathVariable Long id,
                                          @RequestBody @Valid UpdateUserRoleRequestDto requestDto) {
        return userService.updateUserRole(id, requestDto);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user's profile method",
            description = "Show data about user")
    @GetMapping("/me")
    public UserResponseDto getCurrentUserProfile(Authentication authentication) {
        return userService.getCurrentUserProfile(authentication);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update user's profile method",
            description = "Update user profile")
    @PutMapping("/me")
    public UserResponseDto updateMyProfile(
            @RequestBody @Valid UpdateUserProfileRequestDto requestDto,
                                           Authentication authentication) {
        return userService.updateMyProfile(requestDto, authentication);
    }
}
