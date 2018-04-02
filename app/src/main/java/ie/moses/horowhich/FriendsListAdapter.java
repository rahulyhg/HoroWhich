package ie.moses.horowhich;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

class FriendsListAdapter extends RecyclerView.Adapter {

    private final Context _context;
    private final List<String> _userNames;
    private final List<String> _userProfilePics;

    public FriendsListAdapter(final Context context, final List<String> userNames, final List<String> userProfilePics) {
        _context = context;
        _userNames = new ArrayList<>(userNames);
        _userProfilePics = new ArrayList<>(userProfilePics);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return new ViewHolder(new FriendView(_context));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        ((FriendView) holder.itemView).setName(_userNames.get(position));
        ((FriendView) holder.itemView).setProfilePic(_userProfilePics.get(position));
    }

    @Override
    public int getItemCount() {
        return _userProfilePics.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(final FriendView itemView) {
            super(itemView);
        }
    }

}
