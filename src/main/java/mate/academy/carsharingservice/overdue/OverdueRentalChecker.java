package mate.academy.carsharingservice.overdue;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharingservice.model.rental.Rental;
import mate.academy.carsharingservice.repository.rental.RentalRepository;
import mate.academy.carsharingservice.service.notification.TelegramNotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OverdueRentalChecker {
    private final RentalRepository rentalRepository;
    private final TelegramNotificationService notificationService;

    @Scheduled(cron = "0 0 9 * * *")
    public void checkOverdueRentals() {
        LocalDate today = LocalDate.now();
        List<Rental> rentals = rentalRepository.findAll();
        for (Rental rental : rentals) {
            if (rental.getActualReturnDate() == null
                    && rental.getReturnDate().isBefore(today)) {
                String message = "Rental " + rental.getId()
                        + " overdue! Please, return a car";
                notificationService.sendNotification(message);
            } else if (rental.getActualReturnDate() == null
                    && rental.getReturnDate().isAfter(today)) {
                String message = "Rental " + rental.getId()
                        + " is still active, but not overdue yet.";
                notificationService.sendNotification(message);
            } else if (rental.getActualReturnDate() == null
                    && rental.getReturnDate().isEqual(today)) {
                String message = "Rental " + rental.getId()
                        + " is still active, please don't forget to return car today.";
                notificationService.sendNotification(message);
            }
        }
    }
}
