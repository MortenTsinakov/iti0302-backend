package ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions;

public class SignUpValidationException extends RuntimeException {
    public SignUpValidationException(String message) {
        super(message);
    }
}
