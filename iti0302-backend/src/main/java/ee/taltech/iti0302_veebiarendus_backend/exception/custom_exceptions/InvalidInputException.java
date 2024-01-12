package ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}
