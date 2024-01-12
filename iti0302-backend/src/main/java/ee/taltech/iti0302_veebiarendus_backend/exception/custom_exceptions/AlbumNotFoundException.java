package ee.taltech.iti0302_veebiarendus_backend.exception.custom_exceptions;

public class AlbumNotFoundException extends RuntimeException {
    public AlbumNotFoundException(String message) {
        super(message);
    }
}
