package ie.moses.horowhich;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

class FriendsListAdapter extends RecyclerView.Adapter {

    private final Context _context;
    private final List<String> _users;

    public FriendsListAdapter(final Context context, final List<String> users) {
        _context = context;
        _users = new ArrayList<>(users);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return new ViewHolder(new ImageView(_context));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        Glide.with(_context)
                .load(_users.get(position))
                .into(((ImageView) holder.itemView));
    }

    @Override
    public int getItemCount() {
        return _users.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(final View itemView) {
            super(itemView);
        }
    }

}
