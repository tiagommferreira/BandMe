package ui.band.me.extras;

/**
 * Created by Tiago on 05/05/2015.
 */
public interface Keys {
    public interface BandKeys {
        public static final String KEY_EXTERNAL_URLS = "external_urls";
        public static final String KEY_FOLLOWERS = "followers";
        public static final String KEY_GENRES = "genres";
        public static final String KEY_ID = "id";
        public static final String KEY_IMAGES = "images";
        public static final String KEY_NAME = "name";
        public static final String KEY_POPULARITY = "popularity";
        public static final String KEY_TYPE = "type";
        public static final String KEY_URI = "uri";
        public static final String KEY_ARTISTS = "artists";
        public static final String KEY_ITEMS = "items";
    }
    public interface TrackKeys {
        public static final String KEY_IMAGES = "images";
        public static final String KEY_NAME = "name";
        public static final String KEY_TRACKS = "tracks";

    }
    public interface APIUrls {
        public static final String URL_SPOTIFY_SEARCH =  "https://api.spotify.com/v1/search";
        public static final String URL_SPOTIFY_ARTISTS =  "https://api.spotify.com/v1/artists/";
    }
}
