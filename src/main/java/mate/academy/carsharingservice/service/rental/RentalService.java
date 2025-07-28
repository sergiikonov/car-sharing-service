package mate.academy.carsharingservice.service.rental;

import mate.academy.carsharingservice.dto.rental.RentalRequestDto;
import mate.academy.carsharingservice.dto.rental.RentalResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface RentalService {
    RentalResponseDto createRental(RentalRequestDto dto, Authentication authentication);

    Page<RentalResponseDto> getRentals(Long userId, Boolean isActive,
                                       Pageable pageable, Authentication authentication);

    RentalResponseDto getRentalById(Long id, Authentication authentication);

    RentalResponseDto returnCar(Long id, Authentication authentication);
}
