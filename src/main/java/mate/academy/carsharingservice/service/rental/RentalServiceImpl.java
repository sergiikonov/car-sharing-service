package mate.academy.carsharingservice.service.rental;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharingservice.dto.rental.RentalRequestDto;
import mate.academy.carsharingservice.dto.rental.RentalResponseDto;
import mate.academy.carsharingservice.exception.EntityNotFoundException;
import mate.academy.carsharingservice.exception.NoRentalsFoundException;
import mate.academy.carsharingservice.exception.UnavailableCarException;
import mate.academy.carsharingservice.mapper.RentalMapper;
import mate.academy.carsharingservice.model.car.Car;
import mate.academy.carsharingservice.model.rental.Rental;
import mate.academy.carsharingservice.model.user.User;
import mate.academy.carsharingservice.repository.car.CarRepository;
import mate.academy.carsharingservice.repository.rental.RentalRepository;
import mate.academy.carsharingservice.service.notification.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    private final NotificationService notificationService;

    @Override
    public RentalResponseDto createRental(RentalRequestDto requestDto,
                                          Authentication authentication) {
        if (requestDto.rentalDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Rental date cannot be in the past");
        }

        if (requestDto.returnDate().isBefore(requestDto.rentalDate())) {
            throw new IllegalArgumentException("Return date must be after rental date");
        }

        int updated = carRepository.decrementInventory(requestDto.carId());
        if (updated == 0) {
            throw new UnavailableCarException("Car is not available");
        }

        Car car = carRepository.findById(requestDto.carId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find car with id: "
                                + requestDto.carId())
                );
        User user = (User) authentication.getPrincipal();
        Rental rental = rentalMapper.toModel(requestDto, car, user);

        Rental saved = rentalRepository.save(rental);
        String message = String.format(
                """
                        New rental created!ID: #%d
                        User: %s
                        Email: %s
                        Car: %s %s
                        Rental date: %s
                        Return date: %s
                        """,
                saved.getId(),
                saved.getUser().getFirstName(),
                saved.getUser().getEmail(),
                saved.getCar().getModel(),
                saved.getCar().getBrand(),
                saved.getRentalDate(),
                saved.getReturnDate()
        );
        notificationService.sendNotification(message);
        return rentalMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<RentalResponseDto> getRentals(Long userId, Boolean isActive,
                                              Pageable pageable, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        if (isManager(authentication)) {
            if (userId != null) {
                return getRentalsByUserId(userId, isActive, pageable);
            }
            return getAllRentals(isActive, pageable);
        }
        return getRentalsByUserId(currentUser.getId(), isActive, pageable);
    }

    @Transactional(readOnly = true)
    public Page<RentalResponseDto> getAllRentals(Boolean isActive, Pageable pageable) {
        Page<Rental> rentals = (isActive == null)
                ? rentalRepository.findAll(pageable)
                : (isActive
                ? rentalRepository.findByActualReturnDateIsNull(pageable)
                : rentalRepository.findByActualReturnDateIsNotNull(pageable)
        );

        return rentals.map(rentalMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<RentalResponseDto> getRentalsByUserId(Long userId, Boolean isActive,
                                                      Pageable pageable) {
        Page<Rental> rentals = (isActive == null)
                ? rentalRepository.findByUserId(userId, pageable)
                : (isActive
                ? rentalRepository.findByUserIdAndActualReturnDateIsNull(userId, pageable)
                : rentalRepository.findByUserIdAndActualReturnDateIsNotNull(userId, pageable)
        );

        return rentals.map(rentalMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public RentalResponseDto getRentalById(Long id, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        Rental rental = rentalRepository.findByIdWithUser(id)
                .orElseThrow(() -> new NoRentalsFoundException("No rental found with id: " + id));

        if (!isManager(authentication) && !rental.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have access to this rental");
        }

        return rentalMapper.toDto(rental);
    }

    @Override
    public RentalResponseDto returnCar(Long id, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        Rental rental = rentalRepository.findByIdWithCar(id)
                .orElseThrow(() -> new NoRentalsFoundException("No rental found with id: " + id));

        if (rental.getActualReturnDate() != null) {
            throw new IllegalStateException("Car has already been returned");
        }

        if (!isManager(authentication) && !rental.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to return this car");
        }

        rental.setActualReturnDate(LocalDate.now());
        carRepository.incrementInventory(rental.getCar().getId());
        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    private boolean isManager(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(auth ->
                        auth.getAuthority().equals("ROLE_MANAGER"));
    }
}
