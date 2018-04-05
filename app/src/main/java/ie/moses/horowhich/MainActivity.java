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

    @BindView(R.id.no_horoscopes_view) View _noHoroscopesMessage;
    @BindView(R.id.star_sign_background) ImageView _starSignBackground;
    @BindView(R.id.todays_horoscope) TextView _todaysHoroscope;
    @BindView(R.id.login_button) LoginButton _facebookLoginButton;
    @BindView(R.id.not_logged_in_warning) TextView _notLoggedInWarning;

    private CallbackManager _callbackManager;

    /**
     * TODO: Sloppy, needs redesign.
     */
//    private DatabaseReference horoscopesRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);

        if (FacebookUtils.isLoggedIn()) {
            /**
             * TODO: Need to have a check for profile so
             * this method can run the tracker for itself and wait.
             * But not sure weather this should be inside the method or
             * outside it.
             * */
            loadTodaysHoroscope();
        } else {
            _notLoggedInWarning.setVisibility(View.VISIBLE);
            _callbackManager = CallbackManager.Factory.create();
            _facebookLoginButton.setReadPermissions("public_profile", "user_friends");
            _facebookLoginButton.registerCallback(_callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {
                    new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            _notLoggedInWarning.setVisibility(View.GONE);
                            loadTodaysHoroscope();
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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        _callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * TODO: Keeps registering identical listeners that all get called every time.
     */
    private void loadTodaysHoroscope() {
        Profile currentProfile = Profile.getCurrentProfile();
        if (currentProfile == null) throw new RuntimeException();

        if (currentProfile != null) {
            DatabaseReference horoscopesRef = FirebaseUtils.getHoroscopesDatabaseReference(currentProfile.getId());
            horoscopesRef.addValueEventListener(new HoroscopesValueEventListener() {
                @Override
                public void onDataChanged(List<Horoscope> horoscopes) {
                    Horoscope savedHoroscope = SharedPreferencesUtils.getTodaysHoroscope(MainActivity.this);
                    if (savedHoroscope != null) {
                        _todaysHoroscope.setText(savedHoroscope.getText());
                        return;
                    }

                    /**
                     * TODO: Sloppy, needs rethinking.
                     * */
                    boolean noHoroscopes = horoscopes.isEmpty() && savedHoroscope == null;
                    _noHoroscopesMessage.setVisibility(noHoroscopes ? View.VISIBLE : View.GONE);

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
                        SharedPreferencesUtils.setTodaysHoroscope(MainActivity.this, todaysHoroscope);
                        Log.i("mo", "today's horoscope saved = " + SharedPreferencesUtils.getTodaysHoroscope(MainActivity.this));
                        _todaysHoroscope.setText(todaysHoroscope.getText());
                        FirebaseUtils.deleteHoroscope(Profile.getCurrentProfile().getId(), todaysHoroscope);
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

}
