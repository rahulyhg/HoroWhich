package ie.moses.horowhich;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Horoscope implements Parcelable {

    public String _text;
    public String _timestamp;

    public Horoscope() {

    }

    public Horoscope(String timestamp, String text) {
        _text = text;
        _timestamp = timestamp;
    }

    protected Horoscope(Parcel in) {
        _text = in.readString();
        _timestamp = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_text);
        dest.writeString(_timestamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Horoscope> CREATOR = new Creator<Horoscope>() {
        @Override
        public Horoscope createFromParcel(Parcel in) {
            return new Horoscope(in);
        }

        @Override
        public Horoscope[] newArray(int size) {
            return new Horoscope[size];
        }
    };
}
