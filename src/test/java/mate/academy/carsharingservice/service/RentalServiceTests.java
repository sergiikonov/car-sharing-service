package mate.academy.carsharingservice.service;

import static mate.academy.carsharingservice.util.RentalUtil.createCar;
import static mate.academy.carsharingservice.util.RentalUtil.createRental;
import static mate.academy.carsharingservice.util.RentalUtil.createRentalRequestDto;
import static mate.academy.carsharingservice.util.RentalUtil.createRentalResponseDto;
import static mate.academy.carsharingservice.util.UserUtil.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import mate.academy.carsharingservice.dto.rental.RentalRequestDto;
import mate.academy.carsharingservice.dto.rental.RentalResponseDto;
import mate.academy.carsharingservice.exception.EntityNotFoundException;
import mate.academy.carsharingservice.exception.UnavailableCarException;
import mate.academy.carsharingservice.mapper.RentalMapper;
import mate.academy.carsharingservice.model.car.Car;
import mate.academy.carsharingservice.model.rental.Rental;
import mate.academy.carsharingservice.model.user.User;
import mate.academy.carsharingservice.repository.car.CarRepository;
import mate.academy.carsharingservice.repository.rental.RentalRepository;
import mate.academy.carsharingservice.service.notification.NotificationService;
import mate.academy.carsharingservice.service.rental.RentalServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTests {
    private static final Long ID = 1L;
    private static final Long CAR_ID = 1L;
    private static final int INVENTORY = 1;

    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private CarRepository carRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private Authentication authentication;
    @Mock
    private Pageable pageable;

    @InjectMocks
    private RentalServiceImpl rentalService;

    @Test
    @DisplayName("Should create rental for current user")
    void createRental_whenCreateRentalRequestIsValidForUser_shouldReturnRentalResponseDto() {
        RentalRequestDto requestDto = createRentalRequestDto();
        User user = createUser();
        Car car = createCar();
        Rental rental = createRental(user, car);
        Rental savedRental = createRental(user, car);
        RentalResponseDto expected = createRentalResponseDto(user);

        when(carRepository.decrementInventory(requestDto.carId())).thenReturn(INVENTORY);
        when(carRepository.findById(ID)).thenReturn(Optional.of(car));
        when(authentication.getPrincipal()).thenReturn(user);
        when(rentalMapper.toModel(requestDto, car, user)).thenReturn(rental);
        when(rentalRepository.save(rental)).thenReturn(savedRental);
        doNothing().when(notificationService).sendNotification(anyString());
        when(rentalMapper.toDto(savedRental)).thenReturn(expected);
        RentalResponseDto actual = rentalService.createRental(requestDto, authentication);
        assertEquals(expected, actual);
        verify(notificationService).sendNotification(anyString());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if rental date in the past")
    void createRental_whenRentalDateInPast_shouldThrowIllegalArgumentException() {
        RentalRequestDto requestDto = new RentalRequestDto(
                1L,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(1)
        );

        assertThrows(IllegalArgumentException.class,
                () -> rentalService.createRental(requestDto, authentication));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if return date is before rental date")
    void createRental_whenReturnDateIsBeforeRentalDate_shouldThrowIllegalArgumentException() {
        RentalRequestDto requestDto = new RentalRequestDto(
                CAR_ID,
                LocalDate.now(),
                LocalDate.now().minusDays(1)
        );

        assertThrows(IllegalArgumentException.class,
                () -> rentalService.createRental(requestDto, authentication));
    }

    @Test
    @DisplayName("Should throw UnavailableCarException if car inventory is 0")
    public void createRental_whenCarInventoryIsZero_shouldThrowUnavailableCarException() {
        RentalRequestDto requestDto = createRentalRequestDto();

        when(carRepository.decrementInventory(requestDto.carId())).thenReturn(0);
        assertThrows(UnavailableCarException.class,
                () -> rentalService.createRental(requestDto, authentication));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException if car doesn't exist")
    public void createRental_whenCarDoesntExist_shouldThrowEntityNotFoundException() {
        RentalRequestDto requestDto = createRentalRequestDto();

        when(carRepository.decrementInventory(requestDto.carId())).thenReturn(INVENTORY);
        when(carRepository.findById(requestDto.carId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> rentalService.createRental(requestDto, authentication));
    }

    @Test
    @DisplayName("Should return all rentals for manager")
    void getRentals_whenUserIsManager_shouldReturnAllRentals() {
        Rental rental = createRental(createUser(), createCar());
        RentalResponseDto dto = createRentalResponseDto(createUser());
        Page<Rental> rentalPage = new PageImpl<>(List.of(rental));

        mockManagerRole();
        when(rentalRepository.findAll(pageable)).thenReturn(rentalPage);
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(dto);

        Page<RentalResponseDto> result = rentalService.getRentals(
                null, null, pageable, authentication
        );

        assertEquals(1, result.getContent().size());
        assertEquals(dto, result.getContent().get(0));
    }

    @Test
    @DisplayName("Should return user rentals for manager with userId filter")
    void getRentals_whenUserIsManagerWithUserId_shouldReturnUserRentals() {
        User user = createUser();
        user.setId(ID);
        Rental rental = createRental(user, createCar());
        RentalResponseDto dto = createRentalResponseDto(user);
        Page<Rental> rentalPage = new PageImpl<>(List.of(rental));

        mockManagerRole();
        when(rentalRepository.findByUserId(eq(user.getId()),
                any(Pageable.class))).thenReturn(rentalPage);
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(dto);

        Page<RentalResponseDto> result = rentalService.getRentals(
                user.getId(), null, pageable, authentication
        );

        assertEquals(1, result.getContent().size());
        assertEquals(dto, result.getContent().get(0));
    }

    @Test
    @DisplayName("Should return current user rentals for regular user")
    void getRentals_whenRegularUser_shouldReturnCurrentUserRentals() {
        User user = createUser();
        user.setId(ID);
        Rental rental = createRental(user, createCar());
        RentalResponseDto dto = createRentalResponseDto(user);
        Page<Rental> rentalPage = new PageImpl<>(List.of(rental));

        mockRegularUserRole(user);
        when(rentalRepository.findByUserId(eq(user.getId()),
                any(Pageable.class))).thenReturn(rentalPage);
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(dto);

        Page<RentalResponseDto> result = rentalService.getRentals(
                null, null, pageable, authentication);

        assertEquals(1, result.getContent().size());
        assertEquals(dto, result.getContent().get(0));
    }

    @Test
    @DisplayName("Should return active rentals when isActive=true")
    void getRentals_whenIsActiveTrue_shouldReturnActiveRentals() {
        Rental rental = createRental(createUser(), createCar());
        RentalResponseDto dto = createRentalResponseDto(createUser());
        Page<Rental> rentalPage = new PageImpl<>(List.of(rental));

        mockManagerRole();
        when(rentalRepository.findByActualReturnDateIsNull(any(Pageable.class)))
                .thenReturn(rentalPage);
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(dto);

        Page<RentalResponseDto> result = rentalService.getRentals(
                null, true, pageable, authentication
        );

        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("Should return inactive rentals when isActive=false")
    void getRentals_whenIsActiveFalse_shouldReturnInactiveRentals() {
        Rental rental = createRental(createUser(), createCar());
        RentalResponseDto dto = createRentalResponseDto(createUser());
        Page<Rental> rentalPage = new PageImpl<>(List.of(rental));

        mockManagerRole();
        when(rentalRepository.findByActualReturnDateIsNotNull(any(
                Pageable.class))).thenReturn(rentalPage);
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(dto);

        Page<RentalResponseDto> result = rentalService.getRentals(
                null, false, pageable, authentication
        );

        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("Should return all rentals when isActive=null")
    void getRentals_whenIsActiveNull_shouldReturnAllRentals() {
        Rental rental = createRental(createUser(), createCar());
        RentalResponseDto dto = createRentalResponseDto(createUser());
        Page<Rental> rentalPage = new PageImpl<>(List.of(rental));

        mockManagerRole();
        when(rentalRepository.findAll(any(Pageable.class))).thenReturn(rentalPage);
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(dto);

        Page<RentalResponseDto> result = rentalService.getRentals(
                null, null, pageable, authentication);

        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("Should return active rentals for specific user")
    void getRentals_whenUserIdAndIsActive_shouldReturnFilteredRentals() {
        User user = createUser();
        user.setId(ID);
        Rental rental = createRental(user, createCar());
        RentalResponseDto dto = createRentalResponseDto(user);
        Page<Rental> rentalPage = new PageImpl<>(List.of(rental));

        mockManagerRole();
        when(rentalRepository.findByUserIdAndActualReturnDateIsNull(
                eq(user.getId()), any(Pageable.class)))
                .thenReturn(rentalPage);
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(dto);

        Page<RentalResponseDto> result = rentalService.getRentals(
                user.getId(), true, pageable, authentication
        );

        assertEquals(1, result.getContent().size());
        assertEquals(dto, result.getContent().get(0));
    }

    private void mockManagerRole() {
        when(authentication.getAuthorities()).thenAnswer(invocation ->
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_MANAGER"))
        );
    }

    private void mockRegularUserRole(User user) {
        when(authentication.getAuthorities()).thenAnswer(invocation ->
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );
        when(authentication.getPrincipal()).thenReturn(user);
    }
}
