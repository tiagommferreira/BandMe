package ui.band.me.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dexafree.materialList.cards.OnButtonPressListener;
import com.dexafree.materialList.model.CardItemView;
import com.squareup.picasso.Picasso;

import ui.band.me.R;
import ui.band.me.extras.BandCard;

/**
 * Created by Tiago on 07/05/2015.
 */
public class BandCardView extends CardItemView<BandCard> {

    TextView mTitle;
    TextView mDescription;
    ImageView mBandPic;
    TextView mLeftButton;
    TextView mRightButton;

    // Default constructors
    public BandCardView(Context context) {
        super(context);
    }

    public BandCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BandCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void build(BandCard card) {
        //super.build(card);

        setTitle(card.getTitle());
        setDescription(card.getDescription());

        mBandPic = (ImageView) findViewById(R.id.imageView);
        if (mBandPic != null) {
            if(card.getUrlImage() == null || card.getUrlImage().isEmpty()) {
                mBandPic.setImageDrawable(card.getDrawable());
            } else {
                Picasso.with(getContext()).load(card.getUrlImage()).into(mBandPic);
            }
        }

        setLeftButton(card);
        setRightButton(card);

    }

    public void setTitle(String title){
        mTitle = (TextView)findViewById(R.id.titleTextView);
        mTitle.setText(title);
    }

    public void setDescription(String description){
        mDescription = (TextView)findViewById(R.id.descriptionTextView);
        mDescription.setText(description);
    }

    public void setLeftButton(final BandCard card) {
        final TextView leftText = (TextView) findViewById(R.id.left_text_button);

        int leftColor = card.getLeftButtonTextColor();

        if(leftColor != -1){
            leftText.setTextColor(leftColor);
        }

        leftText.setText(card.getLeftButtonText().toUpperCase());
        leftText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                OnButtonPressListener listener = card.getOnLeftButtonPressedListener();
                if (listener != null) {
                    listener.onButtonPressedListener(leftText, card);
                }
            }
        });
    }

    public void setRightButton(final BandCard card) {
        final TextView rightText = (TextView) findViewById(R.id.right_text_button);

        int rightColor = card.getRightButtonTextColor();

        if(rightColor != -1){
            rightText.setTextColor(rightColor);
        }

        rightText.setText(card.getRightButtonText().toUpperCase());
        if (card.getRightButtonTextColor() > -1) {
            rightText.setTextColor(card.getRightButtonTextColor());
        }
        rightText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                OnButtonPressListener listener = card.getOnRightButtonPressedListener();
                if(listener != null) {
                    listener.onButtonPressedListener(rightText, card);
                }
            }
        });
    }

}
