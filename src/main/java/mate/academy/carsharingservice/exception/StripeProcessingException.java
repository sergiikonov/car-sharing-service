package mate.academy.carsharingservice.exception;

public class StripeProcessingException extends RuntimeException {
    public StripeProcessingException(String message) {
        super(message);
    }
}
