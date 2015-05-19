package ui.band.me.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;

import ui.band.me.API.APIListener;
import ui.band.me.API.APIThread;
import ui.band.me.API.TwitterAPI;
import ui.band.me.R;
import ui.band.me.extras.Band;
import ui.band.me.extras.Keys;
import ui.band.me.extras.Track;
import ui.band.me.fragments.ShareDialogFragment;

public class BandActivity extends AppCompatActivity {

    private ImageView bandPicture;
    private View discographyTile;
    private View spotifyTile;
    private View tracksTile;
    private View recommendedTile;
    private View bioTile;

    private String bandName;


    private ArrayList<Track> topTracks = new ArrayList<>();
    private ImageView tracksImage;

    private String bandId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Band band = (Band) intent.getSerializableExtra("band");
        bandName = band.getName();

        getSupportActionBar().setTitle(band.getName());


        bandPicture = (ImageView) findViewById(R.id.bandPic);
        Picasso.with(this).load(band.getImageLink()).into(bandPicture);
        bandId = band.getId();

        discographyTile = findViewById(R.id.discographyTile);
        spotifyTile = findViewById(R.id.spotifyTile);
        tracksTile = findViewById(R.id.tracksTile);
        setupTracksTile();
        recommendedTile = findViewById(R.id.recommendedTile);
        bioTile = findViewById(R.id.bioTile);

    }

    private void setupTracksTile() {

        tracksImage = (ImageView) tracksTile.findViewById(R.id.tracksImage);
        sendTrackRequest();
    }

    private void sendTrackRequest() {
        new APIThread(getRequestURL(bandId), new APIListener() {
            @Override
            public void requestCompleted(JSONObject response) {
                topTracks = parseTracksJSON(response);
                setTextViews();
            }
        }).execute();
    }

    private void setTextViews() {
        if(topTracks.size()!=0) {
            RelativeLayout wrapperLayout = (RelativeLayout) findViewById(R.id.tracksTile);
            LinearLayout topTracksLayout = (LinearLayout) wrapperLayout.findViewById(R.id.tracksLayout);
            for(int i=0;i<topTracks.size();i++) {

                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                TextView topTrackTextView = new TextView(this);
                topTrackTextView.setText(topTracks.get(i).getName());
                topTrackTextView.setBackgroundColor(getResources().getColor(R.color.textInImageBG));
                topTrackTextView.setTextColor(getResources().getColor(R.color.textInImageTC));
                topTrackTextView.setTextSize(getResources().getDimension(R.dimen.topTrackTS));
                topTrackTextView.setLayoutParams(linearParams);

                topTracksLayout.addView(topTrackTextView);
            }
            Picasso.with(this).load(topTracks.get(0).getAlbum_image_url()).into(tracksImage);
        }


    }

    private ArrayList<Track> parseTracksJSON(JSONObject response) {
        ArrayList<Track> listTracks = new ArrayList<>();

        if (response != null && response.length() > 0) {
            try {
                JSONArray tracksArray = response.getJSONArray(Keys.TrackKeys.KEY_TRACKS);

                for (int i = 0; i < tracksArray.length(); i++) {
                    Track track = new Track();
                    JSONObject currentItem = tracksArray.getJSONObject(i);

                    track.setName(currentItem.getString(Keys.TrackKeys.KEY_NAME));

                    track.setAlbum_image_url(currentItem.getJSONObject("album").getJSONArray(Keys.TrackKeys.KEY_IMAGES).getJSONObject(0).getString("url"));

                    listTracks.add(track);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return listTracks;
    }

    String getRequestURL(String id) {
        return Normalizer.normalize(Keys.API.URL_SPOTIFY_ARTISTS + id + "/top-tracks?country=US", Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_band, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        else if(id == R.id.action_share) {
            //creates and shows dialog to user
            ShareDialogFragment.newInstance(bandName)
                    .show(this.getFragmentManager(), "Share");

        }

        return super.onOptionsItemSelected(item);
    }

}
