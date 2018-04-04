package ie.moses.horowhich;

import android.os.Parcel;
import android.os.Parcelable;
import org.joda.time.DateTime;

public class Horoscope implements Parcelable {

    private long _creationTimeMillis;
    private String _text;

    public Horoscope(final long creationTimeMillis, final String text) {
        _creationTimeMillis = creationTimeMillis;
        _text = text;
    }

    private Horoscope(Parcel in) {
        _creationTimeMillis = in.readLong();
        _text = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(_creationTimeMillis);
        dest.writeString(_text);
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

    public long getCreationTimeMillis() {
        return _creationTimeMillis;
    }

    public String getText() {
        return _text;
    }

    @Override
    public String toString() {
        return new DateTime(_creationTimeMillis).toString() + ": " + _text;
    }
}
