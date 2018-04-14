package ie.moses.horowhich;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

    @BindView(R.id.toolbar) Toolbar _toolbar;
    @BindView(R.id.recycler_view) RecyclerView _recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_activity);
        ButterKnife.bind(this);
        setSupportActionBar(_toolbar);
        _toolbar.showOverflowMenu();

        if (FacebookUtils.isLoggedIn()) {
            loadFriendsList();
        }else {
            /**
             * TODO: Should replace this by showing the login button.
             * */
            toast(this, "Not logged in.");
            finish();
        }
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
