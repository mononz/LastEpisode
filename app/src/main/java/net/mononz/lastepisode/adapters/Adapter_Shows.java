package net.mononz.lastepisode.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.mononz.lastepisode.library.Constants;
import net.mononz.lastepisode.library.CursorRecAdapter;
import net.mononz.lastepisode.database.Database;
import net.mononz.lastepisode.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Adapter_Shows extends CursorRecAdapter<Adapter_Shows.ShowViewHolder> {

    private Context mContext;
    private Callback mCallback;

    public Adapter_Shows(Context mContext, Cursor cursor, Callback callback) {
        super(cursor);
        this.mContext = mContext;
        this.mCallback = callback;
    }

    @Override
    public void onBindViewHolder(final ShowViewHolder view_holder, Cursor cursor) {

        int idx_id = cursor.getColumnIndex(Database.shows._id);
        int idx_name = cursor.getColumnIndex(Database.shows.name);
        int idx_season = cursor.getColumnIndex(Database.shows.season);
        int idx_episode = cursor.getColumnIndex(Database.shows.episode);
        int idx_active = cursor.getColumnIndex(Database.shows.active);

        view_holder.vName.setText(cursor.getString(idx_name));
        view_holder.vSeason.setText(Constants.makeDoubleDigit(cursor.getInt(idx_season)));
        view_holder.vEpisode.setText(Constants.makeDoubleDigit(cursor.getInt(idx_episode)));

        final long show_id = cursor.getLong(idx_id);
        view_holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.openHero(show_id);
            }
        });

        view_holder.vCard.setBackgroundColor(Color.TRANSPARENT);
        if (cursor.getInt(idx_active) == 0) {
            view_holder.vCard.setBackgroundColor(ContextCompat.getColor(mContext, R.color.end_season));
        }
    }

    @Override
    public ShowViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.element_show, viewGroup, false);
        return new ShowViewHolder(itemView);
    }

    public class ShowViewHolder extends RecyclerView.ViewHolder {

        protected View mView;
        @Bind(R.id.card_view) protected LinearLayout vCard;
        @Bind(R.id.name) protected TextView vName;
        @Bind(R.id.season) protected TextView vSeason;
        @Bind(R.id.episode) protected TextView vEpisode;

        public ShowViewHolder(View v) {
            super(v);
            mView = v;
            ButterKnife.bind(this, v);
        }

    }

    public interface Callback {
        void openHero(long _id);
    }

}