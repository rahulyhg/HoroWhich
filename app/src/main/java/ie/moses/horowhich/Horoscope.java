package ie.moses.horowhich;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Horoscope {

    public String _text;
    public String _timestamp;

    public Horoscope() {

    }

    public Horoscope(String text, String timestamp) {
        _text = text;
        _timestamp = timestamp;
    }

}
