package mate.academy.carsharingservice.mapper;

import mate.academy.carsharingservice.config.MapperConfig;
import mate.academy.carsharingservice.dto.rental.RentalRequestDto;
import mate.academy.carsharingservice.dto.rental.RentalResponseDto;
import mate.academy.carsharingservice.model.car.Car;
import mate.academy.carsharingservice.model.rental.Rental;
import mate.academy.carsharingservice.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface RentalMapper {
    @Mapping(source = "car.id", target = "carId")
    @Mapping(source = "car.model", target = "carModel")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.email", target = "userEmail")
    RentalResponseDto toDto(Rental rental);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "actualReturnDate", ignore = true)
    @Mapping(source = "requestDto.rentalDate", target = "rentalDate")
    @Mapping(source = "requestDto.returnDate", target = "returnDate")
    @Mapping(source = "car", target = "car")
    @Mapping(source = "user", target = "user")
    Rental toModel(RentalRequestDto requestDto, Car car, User user);
}
