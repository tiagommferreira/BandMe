package ui.band.me.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ui.band.me.API.APICallerSingleton;
import ui.band.me.R;
import ui.band.me.adapters.DrawerRecyclerAdapter;
import ui.band.me.extras.Band;
import ui.band.me.extras.DrawerItemInfo;
import ui.band.me.extras.Keys;
import ui.band.me.extras.Track;
import ui.band.me.listeners.RecyclerTouchListener;

/**
 * Created by Tiago on 05/05/2015.
 */
public class BandActivity extends AppCompatActivity {

    private ImageView bandPicture;
    private View discographyTile;
    private View spotifyTile;
    private View tracksTile;
    private View recommendedTile;
    private View bioTile;

    private APICallerSingleton mAPIcaller;
    private RequestQueue mRequestQueue;

    private ArrayList<Track> topTracks = new ArrayList<>();
    private TextView track1;
    private TextView track2;
    private TextView track3;
    private TextView track4;
    private TextView track5;
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

        getSupportActionBar().setTitle(band.getName());

        mAPIcaller = APICallerSingleton.getsInstance();
        mRequestQueue = mAPIcaller.getRequestQueue();

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
        track1 = (TextView) tracksTile.findViewById(R.id.track1);
        track2 = (TextView) tracksTile.findViewById(R.id.track2);
        track3 = (TextView) tracksTile.findViewById(R.id.track3);
        track4 = (TextView) tracksTile.findViewById(R.id.track4);
        track5 = (TextView) tracksTile.findViewById(R.id.track5);

        sendTrackRequest();

    }

    private void sendTrackRequest() {


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,getRequestURL(bandId),new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                topTracks = parseTracksJSON(response);
                setTextViews();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mRequestQueue.add(request);

    }

    private void setTextViews() {

        track1.setText(topTracks.get(0).getName());
        track2.setText(topTracks.get(1).getName());
        track3.setText(topTracks.get(2).getName());
        track4.setText(topTracks.get(3).getName());
        track5.setText(topTracks.get(4).getName());

        Picasso.with(this).load(topTracks.get(0).getAlbum_image_url()).into(tracksImage);
    }

    private ArrayList<Track> parseTracksJSON(JSONObject response) {
        ArrayList<Track> listTracks = new ArrayList<>();

        if(response != null && response.length()>0) {
            try {
                JSONArray tracksArray = response.getJSONArray(Keys.TrackKeys.KEY_TRACKS);

                for(int i = 0; i < tracksArray.length();i++) {
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
        return Normalizer.normalize(APICallerSingleton.URL_SPOTIFY_ARTISTS + id + "/top-tracks?country=US", Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
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
        if(id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

}
