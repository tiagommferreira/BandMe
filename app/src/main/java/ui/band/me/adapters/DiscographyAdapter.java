package ui.band.me.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import ui.band.me.R;
import ui.band.me.extras.Album;

/**
 * Created by Tiago on 26/05/2015.
 */
public class DiscographyAdapter extends RecyclerView.Adapter<DiscographyAdapter.DiscographyViewHolder> {

    private LayoutInflater inflater;
    private List<Album> data = Collections.emptyList();
    private Context context;

    public DiscographyAdapter(Context context, List<Album> data) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public DiscographyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.discography_item,viewGroup,false);
        DiscographyViewHolder viewHolder = new DiscographyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DiscographyViewHolder drawerViewHolder, int pos) {
        Album currentRow = data.get(pos);

        drawerViewHolder.title.setText(currentRow.getName());
        Picasso.with(context).load(currentRow.getImage_url()).into(drawerViewHolder.icon);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class DiscographyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView icon;

        public DiscographyViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.album_name);
            this.icon = (ImageView) itemView.findViewById(R.id.album_image);
        }

    }

}
