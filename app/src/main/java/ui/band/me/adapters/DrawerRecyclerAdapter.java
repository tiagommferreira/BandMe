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
import ui.band.me.extras.DrawerItemInfo;

/**
 * Created by Tiago on 26/04/2015.
 */
public class DrawerRecyclerAdapter extends RecyclerView.Adapter<DrawerRecyclerAdapter.DrawerViewHolder> {

    private LayoutInflater inflater;
    private List<DrawerItemInfo> data = Collections.emptyList();

    public DrawerRecyclerAdapter(Context context, List<DrawerItemInfo> data) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public DrawerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.drawer_recycler_item,viewGroup,false);
        DrawerViewHolder viewHolder = new DrawerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DrawerViewHolder drawerViewHolder, int pos) {
        DrawerItemInfo currentRow = data.get(pos);
        drawerViewHolder.title.setText(currentRow.title);
        drawerViewHolder.icon.setImageResource(currentRow.iconId);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class DrawerViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView icon;

        public DrawerViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.listText);
            this.icon = (ImageView) itemView.findViewById(R.id.listIcon);
        }

    }

}
