package mate.academy.carsharingservice.dto.rental;

import java.time.LocalDate;

public record RentalResponseDto(
        Long id,
        Long carId,
        String carModel,
        Long userId,
        String userEmail,
        LocalDate rentalDate,
        LocalDate returnDate,
        LocalDate actualReturnDate
) {}
