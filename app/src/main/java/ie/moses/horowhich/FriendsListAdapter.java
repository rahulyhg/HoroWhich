package ie.moses.horowhich;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

class FriendsListAdapter extends RecyclerView.Adapter {

    private static final String TAG = FriendsListAdapter.class.getSimpleName();

    private final Context _context;
    private final List<FacebookFriend> _facebookFriends;
    @Nullable private OnItemClickListener _onItemClickListener;

    public FriendsListAdapter(final Context context, final List<FacebookFriend> friends, @Nullable OnItemClickListener listener) {
        _context = context;
        _facebookFriends = new ArrayList<>(friends);
        _onItemClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return new ViewHolder(new FriendView(_context));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        FacebookFriend facebookFriend = _facebookFriends.get(position);
        ((FriendView) holder.itemView).setName(facebookFriend.getName());
        holder.itemView.setOnClickListener(view -> {
            if (_onItemClickListener != null) {
                _onItemClickListener.onItemClicked(position);
            }
        });

        GraphUtils.makeUserProfilePicGraphRequest(facebookFriend.getId(), new TryFailCallback<String>() {
            @Override
            public void call(String profilePicUrl) {
                ((FriendView) holder.itemView).setProfilePic(profilePicUrl);
            }

            @Override
            public void onFailure(@Nullable Throwable error) {
                Log.e(TAG, "failed to set profile pic", error);
            }
        }).executeAsync();
    }

    @Override
    public int getItemCount() {
        return _facebookFriends.size();
    }

//    public void setOnItemClickListener(@Nullable OnItemClickListener onItemClickListener) {
//        _onItemClickListener = onItemClickListener;
//        notifyDataSetChanged();
//    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(final FriendView itemView) {
            super(itemView);
        }
    }

}
