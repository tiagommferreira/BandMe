package ui.band.me.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;

import ui.band.me.API.APICallerSingleton;
import ui.band.me.R;
import ui.band.me.extras.Band;

/**
 * Created by Tiago on 05/05/2015.
 */
public class BandListAdapter extends RecyclerView.Adapter<BandListAdapter.BandViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<Band> listBands = new ArrayList<>();
    private APICallerSingleton callerSingleton;
    private ImageLoader imageLoader;

    public BandListAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.callerSingleton = APICallerSingleton.getsInstance();
        this.imageLoader = callerSingleton.getImageLoader();
    }

    public void setBandList(ArrayList<Band> listBands) {
        this.listBands = listBands;
        notifyItemRangeChanged(0,listBands.size());
    }

    @Override
    public BandViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.band_recycler_item,viewGroup,false);
        BandViewHolder viewHolder = new BandViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final BandViewHolder bandViewHolder, int pos) {
        Band currentBand = listBands.get(pos);
        bandViewHolder.bandName.setText(currentBand.getName());
        //bandViewHolder.bandGenres.setText(currentRow.getGenres());

        String imageURL = currentBand.getImageLink();
        if(imageURL != null) {
            imageLoader.get(imageURL,new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    bandViewHolder.bandPic.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listBands.size();
    }

    class BandViewHolder extends RecyclerView.ViewHolder {

        private TextView bandName;
        private TextView bandGenres;
        private ImageView bandPic;

        public BandViewHolder(View itemView) {
            super(itemView);
            this.bandName = (TextView) itemView.findViewById(R.id.band_name);
            this.bandGenres = (TextView) itemView.findViewById(R.id.band_genres);
            this.bandPic = (ImageView) itemView.findViewById(R.id.band_pic);
        }

    }
}
