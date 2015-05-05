package ui.band.me.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

import ui.band.me.API.APICallerSingleton;
import ui.band.me.adapters.BandListAdapter;
import ui.band.me.adapters.DrawerRecyclerAdapter;
import ui.band.me.extras.Band;
import ui.band.me.extras.DrawerItemInfo;
import ui.band.me.extras.Keys;
import ui.band.me.fragments.NavigationDrawerFragment;
import ui.band.me.R;
import ui.band.me.listeners.RecyclerTouchListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button nextButton;

    private RecyclerView recyclerView;
    private BandListAdapter recyclerAdapter;

    private APICallerSingleton mAPIcaller;
    private ImageLoader mImageLoader;
    private RequestQueue mRequestQueue;

    private ArrayList<Band> mBandList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NavigationDrawerFragment drawerFragment;
        drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer,(DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackground(R.color.primaryColorDark);

        mBandList = new ArrayList<>();
        mAPIcaller = APICallerSingleton.getsInstance();
        mRequestQueue = mAPIcaller.getRequestQueue();

        sendBandRequest("Muse");

        this.recyclerView = (RecyclerView) findViewById(R.id.band_list);
        this.recyclerAdapter = new BandListAdapter(this,mBandList);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //TODO: meter a iniciar as atividades
                //startActivity(new Intent(getActivity(),SubActivity.class));
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

    }

    private void sendBandRequest(String bandName) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,getRequestURL(bandName),new Response.Listener<JSONObject>() {
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
            JSONObject artistsObject = response.getJSONObject(Keys.BandKeys.KEY_ARTISTS);

            JSONArray itemsArray = artistsObject.getJSONArray(Keys.BandKeys.KEY_ITEMS);

            for(int i = 0; i < itemsArray.length();i++) {
                Band band = new Band();
                JSONObject currentItem = itemsArray.getJSONObject(i);
                band.setId(currentItem.getString(Keys.BandKeys.KEY_ID));
                band.setName(currentItem.getString(Keys.BandKeys.KEY_NAME));

                JSONArray genresArray = currentItem.getJSONArray(Keys.BandKeys.KEY_GENRES);
                ArrayList<String> genres = new ArrayList<>();

                for(int j = 0; j<genresArray.length();j++) {
                    genres.add(genresArray.getString(j));
                }

                band.setGenres(genres);

                band.setFollowers(currentItem.getJSONObject(Keys.BandKeys.KEY_FOLLOWERS).getInt("total"));
                band.setPopularity(currentItem.getInt(Keys.BandKeys.KEY_POPULARITY));
                band.setUri(currentItem.getString(Keys.BandKeys.KEY_URI));


                mBandList.add(band);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getRequestURL(String name) {
        return APICallerSingleton.URL_SPOTIFY_SEARCH + "?q=" + name + "&type=artist";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
