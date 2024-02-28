package ee.taltech.iti0302_veebiarendus_backend.constants;

public class AppConstants {

    private AppConstants() {}
    // Last.fm API constants
    public static final String URL_BASE = "http://ws.audioscrobbler.com/2.0";

    public static final String ALBUM_SEARCH_METHOD = "album.search";
    public static final String GET_ALBUM_INFO_METHOD = "album.getinfo";

    public static final String JSON_FORMAT = "json";
    public static final String LIMIT = "15";
    public static final String AUTOCORRECT = "1";

    public static final Integer JWT_EXPIRY_TIME = 60000 * 60 * 24 * 7;

    public static final Integer REQUIRED_PASSWORD_LENGTH = 8;

    public static final Integer LATEST_ALBUMS_LIMIT = 5;
    public static final Integer FRIENDS_PAGE_SIZE = 5;
}
