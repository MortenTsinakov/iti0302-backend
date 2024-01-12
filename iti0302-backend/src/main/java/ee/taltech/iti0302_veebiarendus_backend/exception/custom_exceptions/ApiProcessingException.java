package ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions;

public class ApiProcessingException extends RuntimeException{
    public ApiProcessingException(String message) {
        super(message);
    }
}
