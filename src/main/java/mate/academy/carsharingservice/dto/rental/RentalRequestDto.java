package mate.academy.carsharingservice.dto.rental;

import java.time.LocalDate;

public record RentalRequestDto(
        Long carId,
        LocalDate rentalDate,
        LocalDate returnDate
) {}
