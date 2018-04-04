package ie.moses.horowhich;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public abstract class HoroscopesValueEventListener implements ValueEventListener {

    @Override
    public final void onDataChange(DataSnapshot dataSnapshot) {
        List<Horoscope> horoscopes = new ArrayList<>((int) dataSnapshot.getChildrenCount());

        for (DataSnapshot snap : dataSnapshot.getChildren()) {
            Horoscope horoscope = new Horoscope(Long.valueOf(snap.getKey()), snap.getValue(String.class));
            horoscopes.add(horoscope);
        }

        onDataChanged(horoscopes);
    }

    public abstract void onDataChanged(List<Horoscope> horoscopes);

}
