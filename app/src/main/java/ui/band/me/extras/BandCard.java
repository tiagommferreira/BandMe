package ui.band.me.extras;

import android.content.Context;

import com.dexafree.materialList.cards.BigImageButtonsCard;

import ui.band.me.R;

/**
 * Created by Tiago on 07/05/2015.
 */
public class BandCard extends BigImageButtonsCard {

    public BandCard(Context context) {
        super(context);
    }

    @Override
    public int getLayout(){
        return R.layout.card_band;
    }
}
