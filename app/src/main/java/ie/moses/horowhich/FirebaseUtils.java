package ie.moses.horowhich;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class FirebaseUtils {

    public static void sendHoroscope(String facebookFriendId, String horoscope) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        DatabaseReference horoscopesRef = myRef.child(facebookFriendId).child("horoscopes");
        horoscopesRef.child(String.valueOf(System.currentTimeMillis())).setValue(horoscope);
    }

}
