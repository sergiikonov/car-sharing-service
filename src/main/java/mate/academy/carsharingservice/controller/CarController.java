package mate.academy.carsharingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharingservice.dto.car.CarDto;
import mate.academy.carsharingservice.dto.car.CreateCarRequestDto;
import mate.academy.carsharingservice.service.car.CarService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Car Controller", description = "Operations with car")
@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Create car with params method",
            description = "Create car")
    @PostMapping
    public CarDto createCar(@RequestBody @Valid CreateCarRequestDto requestDto) {
        return carService.save(requestDto);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    @Operation(summary = "Get all cars method",
            description = "Get all cars")
    @GetMapping
    public Page<CarDto> getAll(Pageable pageable) {
        return carService.findAll(pageable);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    @Operation(summary = "Get car by id method",
            description = "Get car by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the car"),
            @ApiResponse(responseCode = "404", description = "Car not found")
    })
    @GetMapping("/{id}")
    public CarDto getCarById(@PathVariable Long id) {
        return carService.findById(id);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update car method",
            description = "Update car by id")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public CarDto update(@PathVariable Long id,
            @RequestBody @Valid CreateCarRequestDto requestDto) {
        return carService.updateById(requestDto, id);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Delete car by id method",
            description = "Delete car by id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        carService.deleteById(id);
    }
}
