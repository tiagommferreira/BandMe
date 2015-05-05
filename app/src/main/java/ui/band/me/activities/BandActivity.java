package ui.band.me.activities;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ui.band.me.API.APICallerSingleton;
import ui.band.me.R;
import ui.band.me.extras.Keys;

/**
 * Created by Tiago on 05/05/2015.
 */
public class BandActivity extends AppCompatActivity {

    private APICallerSingleton mAPIcaller;
    private ImageLoader mImageLoader;
    private RequestQueue mRequestQueue;
    private TextView mBandName;
    private TextView mBandGenres;
    private TextView mBandPopularity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBandName = (TextView) findViewById(R.id.bandName);
        mBandGenres = (TextView) findViewById(R.id.bandGenres);
        mBandPopularity = (TextView) findViewById(R.id.bandPopularity);

        String bandId = "12Chz98pHFMPJEknJQMWvI";

        mAPIcaller = APICallerSingleton.getsInstance();
        mRequestQueue = mAPIcaller.getRequestQueue();

        sendBandRequest(bandId);
    }

    private void sendBandRequest(String bandId) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,getRequestURL(bandId),new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseBandJSON(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mRequestQueue.add(request);
    }

    private void parseBandJSON(JSONObject response) {
        if(response == null || response.length()==0) {
            return;
        }

        try {
            mBandName.setText(response.getString(Keys.BandKeys.KEY_NAME));
            mBandPopularity.setText("Popularity: " + String.valueOf(response.getInt(Keys.BandKeys.KEY_POPULARITY)));

            JSONArray genres = response.getJSONArray(Keys.BandKeys.KEY_GENRES);
            ArrayList<String> genresList = new ArrayList<>();
            if(genres != null) {
                for (int i=0;i<genres.length();i++){
                    genresList.add(genres.get(i).toString());
                }
            }

            if(genresList.size() == 0) {
                mBandGenres.setText("Genres: No defined genres");
            }
            else {
                mBandGenres.setText("Genres: " + genresList);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

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
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    public String getRequestURL(String id) {
        return APICallerSingleton.URL_SPOTIFY_ARTISTS + id;
    }
}
