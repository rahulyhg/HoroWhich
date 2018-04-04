package ie.moses.horowhich;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class HoroscopesListAdapter extends RecyclerView.Adapter {

    private final Context _context;
    private final List<Horoscope> _horoscopes;
    @Nullable private OnItemClickListener _onItemClickListener;

    public HoroscopesListAdapter(final Context context, @Nullable OnItemClickListener listener) {
        _context = context;
        _horoscopes = new ArrayList<>();
        _onItemClickListener = listener;
    }

    public void setHoroscopes(List<Horoscope> horoscopes) {
        _horoscopes.clear();
        _horoscopes.addAll(horoscopes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(new HoroscopeView(_context));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String horoscopeText = _horoscopes.get(position).getText();
        ((HoroscopeView) holder.itemView).setText(horoscopeText);
        holder.itemView.setOnClickListener(view -> {
            if (_onItemClickListener != null) {
                _onItemClickListener.onItemClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _horoscopes.size();
    }

    private static final class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

}
