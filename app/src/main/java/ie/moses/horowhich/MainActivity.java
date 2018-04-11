package ie.moses.horowhich;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

    @BindView(R.id.toolbar) Toolbar _toolbar;
    @BindView(R.id.no_horoscopes_view) View _noHoroscopesMessage;
    @BindView(R.id.star_sign_background) ImageView _starSignBackground;
    @BindView(R.id.todays_horoscope) TextView _todaysHoroscope;
    @BindView(R.id.login_button) LoginButton _facebookLoginButton;
    @BindView(R.id.not_logged_in_warning) TextView _notLoggedInWarning;

    private CallbackManager _callbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(DebugUtils.DEBUG_MODE) {
            if(Profile.getCurrentProfile() != null) {
                Log.i("mo", "current profile = " + Profile.getCurrentProfile());
            }
        }

        if(!InternetUtils.isNetworkAvailable(this)) {
            toast(this, "Not connected to the internet.");
            finish();
        }

        startService(new Intent(this, NotificationService.class));
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
        setSupportActionBar(_toolbar);
        _toolbar.showOverflowMenu();

        if (FacebookUtils.isLoggedIn()) {
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
                            Log.i("mo", "user id = " + Profile.getCurrentProfile().getId());
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

        new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    toast(MainActivity.this, "Logged out");
                    SharedPreferencesUtils.clear(MainActivity.this);
                    recreate();
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        _callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.write_horoscope:
                startActivity(new Intent(this, FriendsActivity.class));
                break;
        }
        return false;
    }

    /**
     * TODO: Keeps registering identical listeners that all get called every time?
     */
    private void loadTodaysHoroscope() {
        Profile currentProfile = Profile.getCurrentProfile();

        if (currentProfile == null) {
            new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                    loadTodaysHoroscope();
                }
            };
        } else {
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
                    List<Horoscope> oldHoroscopes = new ArrayList<>();

                    // Reverse horoscopes so you're seeing most recent to least.
                    Collections.reverse(horoscopes);
                    for (int i = 0; i < horoscopes.size(); i++) {
                        Horoscope horoscope = horoscopes.get(i);
                        /**
                         * TODO: Doesn't account for horoscopes written today.
                         * Need to prefer horoscopes written yesterday? Or just select
                         * them completely at random so you never know when your horoscopes
                         * are going to be seen? That could take away from the social/messaging
                         * aspect of it but also make them like fun time bombs where people
                         * are like (oh yeah, I wrote that one for you ages ago!).
                         * */
                        /**
                         * TODO: Added "happenedToday" check to address the todo above but haven't tested it.
                         * */
                        long horoscopeCreationTimeMillis = horoscope.getCreationTimeMillis();
                        if (TimeUtils.happenedYesterday(horoscopeCreationTimeMillis) ||
                                TimeUtils.happenedToday(horoscopeCreationTimeMillis)) {
                            newHoroscopes.add(horoscope);
                        } else {
                            oldHoroscopes.add(horoscope);
                        }
                    }

                    Horoscope todaysHoroscope = null;

                    if (!newHoroscopes.isEmpty()) {
                        todaysHoroscope = CollectionUtils.getRandomElement(newHoroscopes);
                    } else if (!oldHoroscopes.isEmpty()) {
                        todaysHoroscope = CollectionUtils.getRandomElement(oldHoroscopes);
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
