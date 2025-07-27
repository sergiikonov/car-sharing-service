package mate.academy.carsharingservice.mapper;

import mate.academy.carsharingservice.config.MapperConfig;
import mate.academy.carsharingservice.dto.car.CarDto;
import mate.academy.carsharingservice.dto.car.CreateCarRequestDto;
import mate.academy.carsharingservice.model.car.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface CarMapper {
    CarDto toDto(Car car);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Car toModel(CreateCarRequestDto requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateCarFromDto(CreateCarRequestDto dto, @MappingTarget Car car);
}
