package ie.moses.horowhich;

import android.os.Parcel;
import android.os.Parcelable;

public class Horoscope implements Parcelable {

    private final long _creationTimeMillis;
    private final String _text;
//    private final String _senderId;

    public Horoscope(final long creationTimeMillis, final String text) { //, final String senderId) {
        _creationTimeMillis = creationTimeMillis;
        _text = text;
//        _senderId = senderId;
    }

    private Horoscope(Parcel in) {
        _creationTimeMillis = in.readLong();
        _text = in.readString();
//        _senderId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(_creationTimeMillis);
        dest.writeString(_text);
//        dest.writeString(_senderId);
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

//    public String getSenderId() {
//        return _senderId;
//    }

    @Override
    public String toString() {
        return "Horoscope{" +
                "_creationTimeMillis=" + _creationTimeMillis +
                ", _text='" + _text + '\'' +
//                ", _senderId='" + _senderId + '\'' +
                '}';
    }

}
