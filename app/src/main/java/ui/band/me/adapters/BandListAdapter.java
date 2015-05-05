package ui.band.me.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import ui.band.me.R;
import ui.band.me.extras.Band;
import ui.band.me.extras.DrawerItemInfo;

/**
 * Created by Tiago on 05/05/2015.
 */
public class BandListAdapter extends RecyclerView.Adapter<BandListAdapter.BandViewHolder> {
    private LayoutInflater inflater;
    private List<Band> data = Collections.emptyList();

    public BandListAdapter(Context context, List<Band> data) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public BandViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.band_recycler_item,viewGroup,false);
        BandViewHolder viewHolder = new BandViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BandViewHolder bandViewHolder, int pos) {
        Band currentRow = data.get(pos);
        bandViewHolder.bandName.setText(currentRow.getName());
        //bandViewHolder.bandGenres.setText(currentRow.getGenres());
        //bandViewHolder.icon.setImageResource(currentRow.iconId);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class BandViewHolder extends RecyclerView.ViewHolder {

        TextView bandName;
        TextView bandGenres;
        ImageView bandPic;

        public BandViewHolder(View itemView) {
            super(itemView);
            this.bandName = (TextView) itemView.findViewById(R.id.band_name);
            this.bandGenres = (TextView) itemView.findViewById(R.id.band_genres);
            this.bandPic = (ImageView) itemView.findViewById(R.id.band_pic);
        }

    }
}
