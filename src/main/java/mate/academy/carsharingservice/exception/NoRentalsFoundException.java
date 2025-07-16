package mate.academy.carsharingservice.exception;

public class NoRentalsFoundException extends RuntimeException {
    public NoRentalsFoundException(String message) {
        super(message);
    }
}
