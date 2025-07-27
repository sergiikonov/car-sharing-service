package mate.academy.carsharingservice.service.car;

import lombok.RequiredArgsConstructor;
import mate.academy.carsharingservice.dto.car.CarDto;
import mate.academy.carsharingservice.dto.car.CreateCarRequestDto;
import mate.academy.carsharingservice.exception.EntityNotFoundException;
import mate.academy.carsharingservice.mapper.CarMapper;
import mate.academy.carsharingservice.model.car.Car;
import mate.academy.carsharingservice.repository.car.CarRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarDto save(CreateCarRequestDto requestDto) {
        Car car = carMapper.toModel(requestDto);
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarDto> findAll(Pageable pageable) {
        return carRepository.findAll(pageable).map(carMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public CarDto findById(Long id) {
        return carMapper.toDto(carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find car by id: " + id)
        ));
    }

    @Override
    public CarDto updateById(CreateCarRequestDto requestDto, Long id) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find car by id: " + id)
        );
        carMapper.updateCarFromDto(requestDto, car);
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    public void deleteById(Long id) {
        if (!carRepository.existsById(id)) {
            throw new EntityNotFoundException("Can't find car by id: " + id);
        }
        carRepository.deleteById(id);
    }
}
