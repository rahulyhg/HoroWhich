package ie.moses.horowhich;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.facebook.*;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ie.moses.horowhich.ToastUtils.toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.no_horoscopes_view) View _noHoroscopesView;
    @BindView(R.id.star_sign_background) ImageView _starSignBackground;
    @BindView(R.id.todays_horoscope) TextView _todaysHoroscope;
    @BindView(R.id.login_button) LoginButton _facebookLoginButton;

    private CallbackManager _callbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);

        _callbackManager = CallbackManager.Factory.create();
        _facebookLoginButton.setReadPermissions("public_profile", "user_friends");
        _facebookLoginButton.registerCallback(_callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                        _todaysHoroscope.setText("");
                        init();
                    }
                };
            }

            @Override
            public void onCancel() {
                toast(MainActivity.this, "Login cancelled");
            }

            @Override
            public void onError(final FacebookException error) {
                toast(MainActivity.this, "Login error :( Please try again.");
                Log.e(TAG, "facebook login failed", error);
            }
        });

        init();
    }

    /**
     * TODO: Needs a better name.
     */
    private void init() {
        if (FacebookUtils.isLoggedIn()) {
            Horoscope savedHoroscope = SharedPreferencesUtils.getTodaysHoroscope(this);
            if (savedHoroscope != null) {
                setTodaysHoroscope(savedHoroscope);
            } else {
                loadTodaysHoroscope();
            }
        } else {
            _todaysHoroscope.setText(R.string.not_logged_in_warning);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        _callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setTodaysHoroscope(Horoscope horoscope) {
        _todaysHoroscope.setText(horoscope.getText());
    }

    private void loadTodaysHoroscope() {
        Profile currentProfile = Profile.getCurrentProfile();
        DatabaseReference horoscopesRef = FirebaseUtils.getHoroscopesDatabaseReference(currentProfile.getId());
        horoscopesRef.addValueEventListener(new HoroscopesValueEventListener() {
            @Override
            public void onDataChanged(List<Horoscope> horoscopes) {
                _noHoroscopesView.setVisibility(horoscopes.size() > 0 ? View.GONE : View.VISIBLE);

                List<Horoscope> newHoroscopes = new ArrayList<>();
                // If there are no new horoscopes try to find
                // the most recent old horoscope to use instead.
                Horoscope oldHoroscope = null;

                // Reverse horoscopes so you're seeing most recent to least.
                Collections.reverse(horoscopes);
                for (int i = 0; i < horoscopes.size(); i++) {
                    Horoscope horoscope = horoscopes.get(i);
                    if (TimeUtils.happenedYesterday(horoscope.getCreationTimeMillis())) {
                        newHoroscopes.add(horoscope);
                    } else {
                        if (oldHoroscope == null) {
                            oldHoroscope = horoscope;
                        }
                    }
                }

                Horoscope todaysHoroscope = null;

                if (!newHoroscopes.isEmpty()) {
                    todaysHoroscope = CollectionUtils.getRandomElement(newHoroscopes);
                } else if (oldHoroscope != null) {
                    todaysHoroscope = oldHoroscope;
                }

                if (todaysHoroscope != null) {
                    FirebaseUtils.deleteHoroscope(Profile.getCurrentProfile().getId(), todaysHoroscope);
                    SharedPreferencesUtils.setTodaysHoroscope(MainActivity.this, todaysHoroscope);
                    setTodaysHoroscope(todaysHoroscope);
                } else {
                    Log.e(TAG, "today's horoscope is null");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "failed to retrieve horoscopes, database error");
                toast(MainActivity.this, "Couldn't load horoscopes :(");
            }
        });
    }

}
