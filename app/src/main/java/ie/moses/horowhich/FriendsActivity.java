package ie.moses.horowhich;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.facebook.*;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import static ie.moses.horowhich.ToastUtils.toast;

public class FriendsActivity extends AppCompatActivity {

    private static final String TAG = FriendsActivity.class.getSimpleName();

    private CallbackManager _callbackManager;

    @BindView(R.id.splash_view) View _splashView;
    @BindView(R.id.login_button) LoginButton _facebookLoginButton;

    @BindView(R.id.recycler_view) RecyclerView _recyclerView;

    @BindView(R.id.new_horoscopes_counter) TextView _newHoroscopesCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, NewHoroscopeNotificationService.class));
        setContentView(R.layout.friends_activity);
        ButterKnife.bind(this);

        hideSplashScreen();

        _callbackManager = CallbackManager.Factory.create();
        _facebookLoginButton.setReadPermissions("public_profile", "user_friends");
        _facebookLoginButton.registerCallback(_callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                Log.i("mo", "facebook login successful " + loginResult.getAccessToken());
                Log.i("mo", "logged in as  " + Profile.getCurrentProfile());
                Toast.makeText(FriendsActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                loadFriendsList();
            }

            @Override
            public void onCancel() {
                Log.i("mo", "facebook login cancelled");
                Toast.makeText(FriendsActivity.this, "cancelled...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(final FacebookException error) {
                Toast.makeText(FriendsActivity.this, "Error :(", Toast.LENGTH_SHORT).show();
                Log.e("mo", "facebook login error", error);
            }
        });

        if (FacebookUtils.isLoggedIn()) {
            loadFriendsList();
        }

        if (Profile.getCurrentProfile() != null) {
            Profile currentProfile = Profile.getCurrentProfile();
            DatabaseReference horoscopesRef = FirebaseUtils.getHoroscopesDatabaseReference(currentProfile.getId());
            horoscopesRef.addValueEventListener(new HoroscopesValueEventListener() {
                @Override
                public void onDataChanged(List<Horoscope> horoscopes) {
                    _newHoroscopesCounter.setText(String.valueOf(horoscopes.size()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "failed to retrieve updated horoscopes, database error", databaseError.toException());
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        _callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.new_horoscopes_counter)
    public void launchHoroscopeReaderActivity() {
        Intent intent = new Intent(this, HoroscopeReaderActivity.class);
        startActivity(intent);
    }

    private void hideSplashScreen() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Log.e(TAG, "failed to sleep before hiding splash screen");
                }
                runOnUiThread(() -> _splashView.setVisibility(View.GONE));
            }
        }.start();
    }

    private void loadFriendsList() {
        GraphUtils.getFacebookFriends(AccessToken.getCurrentAccessToken(), new TryFailCallback<List<FacebookFriend>>() {
            @Override
            public void call(List<FacebookFriend> facebookFriends) {
                LinearLayoutManager layoutManager = new LinearLayoutManager(FriendsActivity.this);
                _recyclerView.setLayoutManager(layoutManager);
                FriendsListAdapter adapter = new FriendsListAdapter(FriendsActivity.this, facebookFriends, position -> {
                    FacebookFriend facebookFriend = facebookFriends.get(position);
                    Intent intent = new Intent(FriendsActivity.this, WriteHoroscopeActivity.class);
                    intent.putExtra(WriteHoroscopeActivity.FACEBOOK_FRIEND_ID, facebookFriend.getId());
                    startActivity(intent);
                });
                _recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(@Nullable Throwable error) {
                toast(FriendsActivity.this, "Failed to load facebook friends :(");
                Log.e(TAG, "failed to load facebook friends", error);
            }
        });
    }

}