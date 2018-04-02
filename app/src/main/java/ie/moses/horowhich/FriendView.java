package ie.moses.horowhich;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendView extends FrameLayout {

    @BindView(R.id.name) TextView _name;
    @BindView(R.id.profile_pic) ImageView _profilePic;

    public FriendView(final Context context) {
        this(context, null);
    }

    public FriendView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.friend_view, this);
        ButterKnife.bind(this);
    }

    public void setName(CharSequence name) {
        _name.setText(name);
    }

    public void setProfilePic(final String profilePicUrl) {
        Glide.with(getContext())
                .load(profilePicUrl)
                .into(_profilePic);
    }

}
