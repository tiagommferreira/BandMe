package ui.band.me.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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


    static final String BAND = "band";

    private String bandName;

    private ArrayList<Track> topTracks = new ArrayList<>();
    private ImageView tracksImage;

    private Band relatedArtist = new Band();
    private ImageView recommendedImage;

    private String bandId;

    private Band band;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState != null) {
            Log.d("NAO É NULL", "TA NO SITIO CERTO");
            band = (Band) savedInstanceState.getSerializable(BAND);
        }
        else {
            Log.d("TA NULL", "TA NO SITIO ERRADO");
            Intent intent = getIntent();
            band = (Band) intent.getSerializableExtra("band");
        }
        bandName = band.getName();

        getSupportActionBar().setTitle(band.getName());

        bandPicture = (ImageView) findViewById(R.id.bandPic);
        Picasso.with(this).load(band.getImageLink()).into(bandPicture);

        if (isOnline()){
            bandId = band.getId();
        }

        discographyTile = findViewById(R.id.discographyTile);
        discographyTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDiscographyActivity();
            }
        });

        spotifyTile = findViewById(R.id.spotifyTile);
        tracksTile = findViewById(R.id.tracksTile);
        tracksImage = (ImageView) tracksTile.findViewById(R.id.tracksImage);
        recommendedTile = findViewById(R.id.recommendedTile);
        recommendedImage = (ImageView) recommendedTile.findViewById(R.id.recommendedImage);


        recommendedTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBandActivity();
            }
        });


        if (isOnline()) {
            sendTrackRequest();
            sendRelatedBandRequest();
            //adds top tracks to database
        } else {
            //checks in db instead of the required band
            this.topTracks = Keys.Database.database.getTracksFromBand(bandName);
            setTextViews();
        }

        recommendedTile = findViewById(R.id.recommendedTile);
        bioTile = findViewById(R.id.bioTile);

    }

    private void startDiscographyActivity() {
        Intent i = new Intent(this, BandDiscographyActivity.class);
        i.putExtra("bandId", bandId);
        startActivity(i);
    }

    private void startBandActivity() {
        Intent i = new Intent(this, BandActivity.class);
        Band newBand = relatedArtist;
        i.putExtra("band", newBand);
        finish();
        startActivity(i);
    }

    private void sendRelatedBandRequest() {
        new APIThread(getRelatedArtistRequestUrl(bandId), new APIListener() {
            @Override
            public void requestCompleted(JSONObject response) {
                String relatedId = parseRelatedJSON(response);
                sendBandRequest(relatedId);
            }
        }).execute();
    }

    private void sendBandRequest(String relatedId) {
        new APIThread(getBandRequestUrl(relatedId), new APIListener() {
            @Override
            public void requestCompleted(JSONObject response) {
                Log.d("band", response.toString());
                relatedArtist = parseBandJSON(response);
                setRelatedView();
                Keys.Database.database.insertBand(relatedArtist);
            }
        }).execute();
    }

    private void sendTrackRequest() {
        new APIThread(getRequestURL(bandId), new APIListener() {
            @Override
            public void requestCompleted(JSONObject response) {
                topTracks = parseTracksJSON(response);
                Keys.Database.database.insertTracks(bandName, topTracks);
                setTextViews();
            }
        }).execute();
    }

    public void setRelatedView() {
        if (relatedArtist.getName() != null) {
            RelativeLayout wrapperLayout = (RelativeLayout) findViewById(R.id.recommendedTile);
            RelativeLayout topTracksLayout = (RelativeLayout) wrapperLayout.findViewById(R.id.recommendedTileLayout);

            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            TextView topTrackTextView = new TextView(this);
            topTrackTextView.setText(relatedArtist.getName());
            topTrackTextView.setBackgroundColor(getResources().getColor(R.color.textInImageBG));
            topTrackTextView.setTextColor(getResources().getColor(R.color.textInImageTC));
            topTrackTextView.setTextSize(getResources().getDimension(R.dimen.topTrackTS));
            topTrackTextView.setLayoutParams(linearParams);

            topTracksLayout.addView(topTrackTextView);

            Picasso.with(this).load(relatedArtist.getImageLink()).into(recommendedImage);
        }
    }

    public Band parseBandJSON(JSONObject response) {
        Band band = new Band();
        if (response != null && response.length() > 0) {
            try {
                band.setId(response.getString(Keys.BandKeys.KEY_ID));
                band.setName(response.getString(Keys.BandKeys.KEY_NAME));

                JSONArray genresArray = response.getJSONArray(Keys.BandKeys.KEY_GENRES);
                ArrayList<String> genres = new ArrayList<>();

                for (int j = 0; j < genresArray.length(); j++) {
                    genres.add(genresArray.getString(j));
                }

                band.setGenres(genres);

                band.setFollowers(response.getJSONObject(Keys.BandKeys.KEY_FOLLOWERS).getInt("total"));
                band.setPopularity(response.getInt(Keys.BandKeys.KEY_POPULARITY));
                band.setUri(response.getString(Keys.BandKeys.KEY_URI));
                band.setImageLink(response.getJSONArray(Keys.BandKeys.KEY_IMAGES).getJSONObject(0).getString("url"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return band;
    }

    private void setTextViews() {
        if (topTracks.size() != 0) {
            RelativeLayout wrapperLayout = (RelativeLayout) findViewById(R.id.tracksTile);
            LinearLayout topTracksLayout = (LinearLayout) wrapperLayout.findViewById(R.id.tracksLayout);
            for (int i = 0; i < topTracks.size(); i++) {

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


    private String parseRelatedJSON(JSONObject response) {
        String relatedArtistId = "";

        if (response != null && response.length() > 0) {
            try {
                JSONArray relatedArray = response.getJSONArray(Keys.TrackKeys.KEY_ARTIST);
                JSONObject currentItem = relatedArray.getJSONObject(0);
                relatedArtistId = String.valueOf(currentItem.getString("id"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return relatedArtistId;
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

    String getBandRequestUrl(String id) {
        return Normalizer.normalize(Keys.API.URL_SPOTIFY_ARTISTS + id, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    String getRelatedArtistRequestUrl(String id) {
        return Normalizer.normalize(Keys.API.URL_SPOTIFY_ARTISTS + id + "/related-artists", Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    String getRequestURL(String id) {
        return Normalizer.normalize(Keys.API.URL_SPOTIFY_ARTISTS + id + "/top-tracks?country=US", Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_band, menu);

        MenuItem fav_button = menu.findItem(R.id.action_favourite);
        if (Keys.Database.database.existsFavourite(bandName)) {
            fav_button.setIcon(getResources().getDrawable(R.drawable.ic_star_white_24dp));
        }

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
        } else if (id == R.id.action_share) {
            //creates and shows dialog to user
            if (Keys.API.TWITTER_ACCESS_TOKEN != null && Keys.API.TWITTER_ACCESS_TOKEN_SECRET != null) {
                ShareDialogFragment.newInstance(bandName)
                        .show(this.getFragmentManager(), "Share");
            } else {
                new AlertDialog.Builder(this)
                        .setMessage("You aren't currently logged in. Please login to use the share functionality")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        } else if (id == R.id.action_favourite) {

            if (Keys.Database.database.existsFavourite(bandName)) {
                Log.d("Favourite", "exists");
                Keys.Database.database.removeFavourite(bandName);
                item.setIcon(getResources().getDrawable(R.drawable.ic_star_outline_white_24dp));
            } else {
                Log.d("Favourite", "does not exist");
                Keys.Database.database.insertFavourite(bandName);
                item.setIcon(getResources().getDrawable(R.drawable.ic_star_white_24dp));
            }


        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putSerializable(BAND, band);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

}
