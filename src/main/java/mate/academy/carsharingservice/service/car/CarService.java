package mate.academy.carsharingservice.service.car;

import mate.academy.carsharingservice.dto.car.CarDto;
import mate.academy.carsharingservice.dto.car.CreateCarRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarService {
    CarDto save(CreateCarRequestDto requestDto);

    Page<CarDto> findAll(Pageable pageable);

    CarDto findById(Long id);

    CarDto updateById(CreateCarRequestDto requestDto, Long id);

    void deleteById(Long id);
}
