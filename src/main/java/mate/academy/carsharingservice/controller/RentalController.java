package mate.academy.carsharingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharingservice.dto.rental.RentalRequestDto;
import mate.academy.carsharingservice.dto.rental.RentalResponseDto;
import mate.academy.carsharingservice.service.rental.RentalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Rental Controller", description = "Operations with rentals")
@RestController
@RequiredArgsConstructor
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;

    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    @Operation(summary = "Create rental with params method",
            description = "Create rental")
    @PostMapping
    public RentalResponseDto createRental(@RequestBody @Valid RentalRequestDto requestDto,
                                          Authentication authentication) {
        return rentalService.createRental(requestDto, authentication);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    @Operation(summary = "Get rentals by user ID and active status",
            description = "Get rentals for a specific user, optionally filtering by active status")
    @GetMapping
    public Page<RentalResponseDto> getRentals(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Boolean isActive,
            Pageable pageable,
            Authentication authentication) {
        return rentalService.getRentals(userId, isActive, pageable, authentication);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    @Operation(summary = "Get rentals by ID",
            description = "Get rentals by ID")
    @GetMapping("/{id}")
    public RentalResponseDto getRentalsById(@PathVariable Long id, Authentication authentication) {
        return rentalService.getRentalById(id, authentication);

    }

    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    @Operation(summary = "Return car with ID",
            description = "Mark a car as returned and update its availability")
    @PostMapping("/{id}/return")
    public RentalResponseDto returnCar(@PathVariable Long id, Authentication authentication) {
        return rentalService.returnCar(id, authentication);
    }
}
