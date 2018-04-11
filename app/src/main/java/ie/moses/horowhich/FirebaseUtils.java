package ie.moses.horowhich;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class FirebaseUtils {

    public static void sendHoroscope(String facebookFriendId, String horoscope) {
        DatabaseReference horoscopesRef = getHoroscopesDatabaseReference(facebookFriendId);
        horoscopesRef.child(String.valueOf(System.currentTimeMillis())).setValue(horoscope);
    }

    public static DatabaseReference getHoroscopesDatabaseReference(String userId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");
        return usersRef.child(userId).child("horoscopes");
    }

    /**
     * TODO: Add no such horoscope exception.
     * */
    public static void deleteHoroscope(String userId, Horoscope horoscope) {
        getHoroscopesDatabaseReference(userId).child(String.valueOf(horoscope.getCreationTimeMillis())).removeValue();
    }

}
