package ui.band.me.activities;

import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dexafree.materialList.cards.OnButtonPressListener;
import com.dexafree.materialList.controller.RecyclerItemClickListener;
import com.dexafree.materialList.model.Card;
import com.dexafree.materialList.model.CardItemView;
import com.dexafree.materialList.view.MaterialListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ui.band.me.API.APICallerSingleton;
import ui.band.me.extras.Band;
import ui.band.me.extras.BandCard;
import ui.band.me.extras.Keys;
import ui.band.me.fragments.NavigationDrawerFragment;
import ui.band.me.R;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private APICallerSingleton mAPIcaller;
    private RequestQueue mRequestQueue;

    private ArrayList<Band> mBandList = new ArrayList<>();
    private MaterialListView mListView;


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

        mAPIcaller = APICallerSingleton.getsInstance();
        mRequestQueue = mAPIcaller.getRequestQueue();

        sendBandRequest("Muse");
        mListView = (MaterialListView) findViewById(R.id.material_bandlistview);
        mListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(CardItemView cardItemView, int pos) {
                Log.d("Card View", "Clicked at position " + pos);
            }

            @Override
            public void onItemLongClick(CardItemView cardItemView, int pos) {

            }
        });

    }

    private void addCards() {

        for(Band band: mBandList) {
            BandCard card = new BandCard(this);
            card.setTitle(band.getName());
            card.setDescription(String.valueOf(band.getGenres()));
            card.setDrawable(band.getImageLink());
            card.setLeftButtonText("SHARE");
            card.setRightButtonText("MORE");
            card.setRightButtonTextColor(getResources().getColor(R.color.accentColor));

            card.setOnLeftButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    Log.d("Card", "leftButton");
                    Toast.makeText(getApplicationContext(),
                            "You have pressed the left button",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });


            card.setOnRightButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    Toast.makeText(getApplicationContext(),
                            "You have pressed the right button",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });

            mListView.add(card);
        }

    }

    private void sendBandRequest(String bandName) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,getRequestURL(bandName),new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mBandList = parseBandJSON(response);
                //recyclerAdapter.setBandList(mBandList);
                addCards();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mRequestQueue.add(request);
    }

    private ArrayList<Band> parseBandJSON(JSONObject response) {
        ArrayList<Band> listBands = new ArrayList<>();

        if(response != null && response.length()>0) {
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
                    band.setImageLink(currentItem.getJSONArray(Keys.BandKeys.KEY_IMAGES).getJSONObject(0).getString("url"));


                    listBands.add(band);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return listBands;
    }

    public String getRequestURL(String name) {
        return APICallerSingleton.URL_SPOTIFY_SEARCH + "?q=" + name.replaceAll(" ","+") + "&type=artist";
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
