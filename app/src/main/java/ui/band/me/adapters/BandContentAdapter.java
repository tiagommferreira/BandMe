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

/**
 * Created by Tiago on 12/05/2015.
 */
public class BandContentAdapter extends RecyclerView.Adapter<BandContentAdapter.BandContentHolder> {

    private LayoutInflater inflater;
    private List<Object> data = Collections.emptyList();

    public BandContentAdapter(Context context, List<Object> data) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public BandContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = inflater.inflate(R.layout.item, parent, false);
        return new BandContentHolder(convertView);
    }

    @Override
    public void onBindViewHolder(BandContentHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class BandContentHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView icon;

        public BandContentHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.listText);
            this.icon = (ImageView) itemView.findViewById(R.id.listIcon);
        }

    }
}
