package ie.moses.horowhich;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Can be extrapolated out into a generic recycler grid view.
 */
class FriendsListAdapter extends RecyclerView.Adapter {

    private static final String TAG = FriendsListAdapter.class.getSimpleName();

    private final Context _context;
    private final List<FacebookFriend> _facebookFriends;

    @Nullable private OnItemClickListener _onItemClickListener;

    private int _rowSize = 3;

    public FriendsListAdapter(final Context context, final List<FacebookFriend> friends, @Nullable OnItemClickListener listener) {
        _context = context;
        _facebookFriends = new ArrayList<>(friends);
        _onItemClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater inflater = LayoutInflater.from(_context);
        View layout = inflater.inflate(R.layout.friends_list_row, parent, false);
        return new ViewHolder(layout);
    }

    /**
     * TODO: Don't forget to set visibility.
     */
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final int row = position * _rowSize;
        final int leftIndex = row;
        final int centerIndex = row + 1;
        final int rightIndex = row + 2;

        Log.i(TAG, "binding view holder for row " + row);

        ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder._left.setOnClickListener(view -> {
            if (_onItemClickListener != null) {
                _onItemClickListener.onItemClicked(leftIndex);
            }
        });

        viewHolder._center.setOnClickListener(view -> {
            if (_onItemClickListener != null) {
                _onItemClickListener.onItemClicked(centerIndex);
            }
        });

        viewHolder._right.setOnClickListener(view -> {
            if (_onItemClickListener != null) {
                _onItemClickListener.onItemClicked(rightIndex);
            }
        });

        FacebookFriend leftFacebookFriend = _facebookFriends.get(leftIndex);
        bindViewHolder(viewHolder._left, leftFacebookFriend);

        if (_facebookFriends.size() > centerIndex) {
            viewHolder._center.setVisibility(View.VISIBLE);
            FacebookFriend centerFriend = _facebookFriends.get(centerIndex);
            bindViewHolder(viewHolder._center, centerFriend);
        } else {
            viewHolder._center.setVisibility(View.INVISIBLE);
            Log.i(TAG, "no image for position " + centerIndex);
        }

        if (_facebookFriends.size() > rightIndex) {
            viewHolder._right.setVisibility(View.VISIBLE);
            FacebookFriend rightFriend = _facebookFriends.get(rightIndex);
            bindViewHolder(viewHolder._right, rightFriend);
        } else {
            viewHolder._right.setVisibility(View.INVISIBLE);
            Log.i(TAG, "no image for position " + rightIndex);
        }
    }

    /**
     * TODO: Needs a better name.
     */
    private static void bindViewHolder(FriendView friendView, FacebookFriend facebookFriend) {
        friendView.setName(facebookFriend.getName());
        GraphUtils.makeUserProfilePicGraphRequest(facebookFriend.getId(), new TryFailCallback<String>() {
            @Override
            public void call(String profilePicUrl) {
                Log.i(TAG, "bound profile pic " + profilePicUrl + " to image view");
                friendView.setProfilePic(profilePicUrl);
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

        private FriendView _left;
        private FriendView _center;
        private FriendView _right;

        public ViewHolder(final View itemView) {
            super(itemView);
            _left = itemView.findViewById(R.id.left);
            _center = itemView.findViewById(R.id.center);
            _right = itemView.findViewById(R.id.right);
        }
    }

}
