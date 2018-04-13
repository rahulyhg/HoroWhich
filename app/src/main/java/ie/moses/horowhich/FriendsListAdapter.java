package ie.moses.horowhich;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import static ie.moses.horowhich.CollectionUtils.list;

/**
 * TODO: Can be extrapolated out into a generic recycler grid view.
 * */
class FriendsListAdapter extends RecyclerView.Adapter {

    private static final String TAG = FriendsListAdapter.class.getSimpleName();

    private final Context _context;
    private final List<FacebookFriend> _facebookFriends;

    @Nullable private OnItemClickListener _onItemClickListener;

    private int _rowSize = 3;

    public FriendsListAdapter(final Context context, final List<FacebookFriend> friends, @Nullable OnItemClickListener listener) {
        _context = context;
        _facebookFriends =
//                list(friends.get(0), friends.get(1), friends.get(2));
                new ArrayList<>(friends);
        _onItemClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        Log.i("mo", "created view holder");
        LayoutInflater inflater = LayoutInflater.from(_context);
        View layout = inflater.inflate(R.layout.friends_list_row, parent, false);
        return new ViewHolder(layout);
    }

    /**
     * TODO: Don't forget to set visibility.
     */
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int row) {
//        ((FriendView) holder.itemView).setName(facebookFriend.getName());

        Log.i("mo", "bind view holder for row " + row);

        ((ViewHolder) holder)._leftImage.setOnClickListener(view -> {
            if (_onItemClickListener != null) {
                _onItemClickListener.onItemClicked(row * _rowSize);
            }
        });

        ((ViewHolder) holder)._centerImage.setOnClickListener(view -> {
            if (_onItemClickListener != null) {
                _onItemClickListener.onItemClicked((row * _rowSize) + 1);
            }
        });

        ((ViewHolder) holder)._rightImage.setOnClickListener(view -> {
            if (_onItemClickListener != null) {
                _onItemClickListener.onItemClicked((row * _rowSize) + 2);
            }
        });

        bindProfilePic(((ViewHolder) holder)._leftImage, _facebookFriends.get(row * _rowSize));

        if(_facebookFriends.size() > ((row * _rowSize) + 1)) {
            bindProfilePic(((ViewHolder) holder)._centerImage, _facebookFriends.get((row * _rowSize) + 1));
        }else {
            Log.i("mo", "no image for position " + ((row * _rowSize) + 1));
        }

        if(_facebookFriends.size() > ((row * _rowSize) + 2)) {
            bindProfilePic(((ViewHolder) holder)._rightImage, _facebookFriends.get((row * _rowSize) + 2));
        }else {
            Log.i("mo", "no image for position " + ((row * _rowSize) + 2));
        }
    }

    private void bindProfilePic(ImageView imageView, FacebookFriend facebookFriend) {
        GraphUtils.makeUserProfilePicGraphRequest(facebookFriend.getId(), new TryFailCallback<String>() {
            @Override
            public void call(String profilePicUrl) {
                Log.i("mo", "bound profile pic to image view");
                Glide.with(_context)
                        .load(profilePicUrl)
                        .into(imageView);
            }

            @Override
            public void onFailure(@Nullable Throwable error) {
                Log.e(TAG, "failed to set profile pic for " + facebookFriend, error);
            }
        }).executeAsync();
    }

    @Override
    public int getItemCount() {
        Log.i("mo", "number of rows = " + (int) Math.ceil((double) _facebookFriends.size() / 3));
        return (int) Math.ceil((double) _facebookFriends.size() / 3);
    }

//    public void setOnItemClickListener(@Nullable OnItemClickListener onItemClickListener) {
//        _onItemClickListener = onItemClickListener;
//        notifyDataSetChanged();
//    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView _leftImage;
        private TextView _leftText;

        private ImageView _centerImage;
        private TextView _centerTextView;

        private ImageView _rightImage;
        private TextView _rightTextView;

        public ViewHolder(final View itemView) {
            super(itemView);
            _leftImage = itemView.findViewById(R.id.left_image);
            _centerImage = itemView.findViewById(R.id.center_image);
            _rightImage = itemView.findViewById(R.id.right_image);
        }
    }

}
