package ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions;

public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message)  {
        super(message);
    }
}
