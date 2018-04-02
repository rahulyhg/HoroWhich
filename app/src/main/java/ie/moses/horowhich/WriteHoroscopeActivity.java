package ie.moses.horowhich;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WriteHoroscopeActivity extends AppCompatActivity {

    public static final String FACEBOOK_FRIEND_ID = "FACEBOOK_FRIEND_ID";

    @BindView(R.id.write_horoscope) TextView _writeHoroscope;

    private String _facebookFriendId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_horoscope_activity);
        ButterKnife.bind(this);
        _facebookFriendId = getIntent().getStringExtra(FACEBOOK_FRIEND_ID);
    }

    @OnClick(R.id.cancel_button)
    public void cancel() {
        finish();
    }

    @OnClick(R.id.done_button)
    public void done() {
        FirebaseUtils.sendHoroscope(_facebookFriendId, _writeHoroscope.getText().toString());
        finish();
    }

}
