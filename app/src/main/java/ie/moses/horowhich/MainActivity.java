package ie.moses.horowhich;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    CallbackManager _callbackManager;

    @BindView(R.id.splash_view) View _splashView;
    @BindView(R.id.login_button) LoginButton _facebookLoginButton;

    @BindView(R.id.recycler_view) RecyclerView _recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        hideSplashScreen();

        _callbackManager = CallbackManager.Factory.create();
        _facebookLoginButton.setReadPermissions("public_profile", "user_friends");
        _facebookLoginButton.registerCallback(_callbackManager, new FacebookCallback<LoginResult>() {
            @Override public void onSuccess(final LoginResult loginResult) {
                Log.i("mo", "facebook login successful " + loginResult.getAccessToken());
                Log.i("mo", "logged in as  " + Profile.getCurrentProfile());
                Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();
            }

            @Override public void onCancel() {
                Log.i("mo", "facebook login cancelled");
                Toast.makeText(MainActivity.this, "cancelled...", Toast.LENGTH_SHORT).show();
            }

            @Override public void onError(final FacebookException error) {
                Toast.makeText(MainActivity.this, "Error :(", Toast.LENGTH_SHORT).show();
                Log.e("mo", "facebook login error", error);
            }
        });

        Profile profile = Profile.getCurrentProfile();
        Log.i("mo", "logged in as  " + profile.getName());

        GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                response -> {
                    Log.i("mo", "completed graph request");
                    Log.i("mo", "response = " + response.getRawResponse());

                    try {
                        JSONObject root = response.getJSONObject();
                        JSONArray data = root.getJSONArray("data");
                        final List<String> friendNames = new ArrayList<>();
                        final List<String> friendProfilePics = new ArrayList<>();
                        for(int i = 0; i < data.length(); i++) {
                            JSONObject friend = data.getJSONObject(i);
                            friendNames.add(friend.getString("name"));
                            String userId = friend.getString("id");
                            Log.v("mo", "found friend " + userId);
                            getProfilePicUrl(userId, s -> {
                                Log.i("mo", "friend = " + s);
                                friendProfilePics.add(s);
                            });
                        }

//                        _recyclerView.setHasFixedSize(true);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                        _recyclerView.setLayoutManager(layoutManager);

                        new Thread(){
                            @Override public void run() {
                                while(friendProfilePics.size() < 2) {
                                    try {
                                        Thread.sleep(100);
                                    }catch(InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                                runOnUiThread(() -> {
                                    Log.i("mo", "friends = " + friendProfilePics);
                                    final FriendsListAdapter adapter = new FriendsListAdapter(
                                            MainActivity.this, friendNames, friendProfilePics);
                                    _recyclerView.setAdapter(adapter);
                                });

                            }
                        }.start();

                    }catch(JSONException e) {
                        e.printStackTrace();
                    }

                }).executeAsync();
    }

    private void getProfilePicUrl(final String userId, final Callback<String> callback) {
        Bundle params = new Bundle();
        params.putBoolean("redirect", false);
        params.putString("type", "square");

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                userId + "/picture",
                params,
                HttpMethod.GET,
                response -> {
                    try {
                        String profilePicUrl = (String) response.getJSONObject().getJSONObject("data").get("url");
                        callback.call(profilePicUrl);
                    }catch(JSONException e) {
                        Log.e("moo", "json exception", e);
                    }
                }
        ).executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        _callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void hideSplashScreen() {
        new Thread() {
            @Override public void run() {
                try {
                    Thread.sleep(3000);
                }catch(InterruptedException e) {
                    Log.e(TAG, "failed to sleep before hiding splash screen");
                }
                runOnUiThread(() -> _splashView.setVisibility(View.GONE));
            }
        }.start();
    }

}
