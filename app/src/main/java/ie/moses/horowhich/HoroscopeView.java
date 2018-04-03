package ie.moses.horowhich;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HoroscopeView extends FrameLayout {

    @BindView(R.id.horoscope_text) TextView _horoscopeText;

    public HoroscopeView(Context context) {
        super(context);
        inflate(context, R.layout.horoscope_view, this);
        ButterKnife.bind(this);
    }

    public void setText(CharSequence horoscopeText) {
        _horoscopeText.setText(horoscopeText);
    }

}
