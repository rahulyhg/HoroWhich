package ie.moses.horowhich;

import com.google.android.gms.tasks.Task;
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

    public static Task<Void> deleteHoroscope(String userId, Horoscope horoscope) {
        String horoscopeKey = String.valueOf(horoscope.getCreationTimeMillis());
        DatabaseReference horoscopeRef = getHoroscopesDatabaseReference(userId).child(horoscopeKey);
        return horoscopeRef.removeValue();
    }

}
