package ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
