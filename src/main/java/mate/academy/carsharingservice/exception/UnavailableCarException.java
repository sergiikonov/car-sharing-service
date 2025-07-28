package mate.academy.carsharingservice.exception;

public class UnavailableCarException extends RuntimeException {
    public UnavailableCarException(String message) {
        super(message);
    }
}
