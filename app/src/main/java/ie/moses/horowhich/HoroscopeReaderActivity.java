package ie.moses.horowhich;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.facebook.Profile;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class HoroscopeReaderActivity extends AppCompatActivity {

    private static final String TAG = HoroscopeReaderActivity.class.getSimpleName();

    @BindView(R.id.read_horoscope_view) View _readHoroscopeView;
    @BindView(R.id.horoscope_text) TextView _horoscopeText;
    @BindView(R.id.list) RecyclerView _recyclerView;

    private HoroscopesListAdapter _listAdapter;
    private final List<Horoscope> _horoscopes = new ArrayList<>();
    @Nullable private Horoscope _currentHoroscope;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.horoscope_reader_activity);
        ButterKnife.bind(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        _recyclerView.setLayoutManager(layoutManager);
        _recyclerView.setAdapter(_listAdapter = new HoroscopesListAdapter(this, position -> {
            openHoroscope(_horoscopes.get(position));
        }));

        DatabaseReference horoscopesRef = FirebaseUtils.getHoroscopesDatabaseReference(Profile.getCurrentProfile().getId());
        horoscopesRef.addValueEventListener(new HoroscopesValueEventListener() {
            @Override
            public void onDataChanged(List<Horoscope> horoscopes) {
                _horoscopes.clear();
                _horoscopes.addAll(horoscopes);
                _listAdapter.setHoroscopes(horoscopes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "retrieving horoscopes failed", databaseError.toException());
            }
        });
    }

    @OnClick(R.id.done_button)
    public void done() {
        if (_currentHoroscope != null) {
            FirebaseUtils.deleteHoroscope(Profile.getCurrentProfile().getId(), _currentHoroscope);
        }
        _readHoroscopeView.setVisibility(View.GONE);
    }

    private void openHoroscope(Horoscope horoscope) {
        _currentHoroscope = horoscope;
        _horoscopeText.setText(horoscope._text);
        _readHoroscopeView.setVisibility(View.VISIBLE);
    }

}
