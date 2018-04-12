package ie.moses.horowhich;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WriteHoroscopeActivity extends AppCompatActivity implements TextWatcher {

    public static final String FACEBOOK_FRIEND_ID = "FACEBOOK_FRIEND_ID";

    @BindView(R.id.write_horoscope) EditText _writeHoroscope;
    @BindView(R.id.character_counter) TextView _characterCounter;

    private String _facebookFriendId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_horoscope_activity);
        ButterKnife.bind(this);
        _facebookFriendId = getIntent().getStringExtra(FACEBOOK_FRIEND_ID);
        _writeHoroscope.addTextChangedListener(this);
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

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        /**
         * TODO: If the text was less long before the change no need to set the colour on the counter. (Would be more efficient).
         * */
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        _characterCounter.setText(String.valueOf(charSequence.length()));
        if(charSequence.length() == 140) {
            _characterCounter.setTextColor(Color.rgb(223,7,7));
        }else {
            _characterCounter.setTextColor(Color.rgb(134,129,129));
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

}
