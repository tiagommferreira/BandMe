package ui.band.me.activities;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import java.lang.reflect.Field;
import java.text.Normalizer;
import java.util.ArrayList;

import ui.band.me.API.APICallerSingleton;
import ui.band.me.extras.Band;
import ui.band.me.extras.BandCard;
import ui.band.me.extras.Keys;
import ui.band.me.fragments.NavigationDrawerFragment;
import ui.band.me.R;

import static android.graphics.Color.*;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private APICallerSingleton mAPIcaller;
    private RequestQueue mRequestQueue;

    private ArrayList<Band> mBandList = new ArrayList<>();
    private MaterialListView mListView;

    private View searchContainer;
    private EditText toolbarSearchView;
    private ImageView searchClearButton;

    private NavigationDrawerFragment drawerFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer,(DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackground(R.color.primaryColorDark);

        mAPIcaller = APICallerSingleton.getsInstance();
        mRequestQueue = mAPIcaller.getRequestQueue();
        setUpSearch();

        mListView = (MaterialListView) findViewById(R.id.material_bandlistview);
        mListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(CardItemView cardItemView, int pos) {
                startBandActivity(mBandList.get(pos));
            }

            @Override
            public void onItemLongClick(CardItemView cardItemView, int pos) {

            }
        });

    }

    private void startBandActivity(Band band) {
        Intent i = new Intent(this,BandActivity.class);
        i.putExtra("band", band);

        startActivity(i);

    }

    private void setUpSearch() {
        searchContainer = findViewById(R.id.search_container);
        toolbarSearchView = (EditText) findViewById(R.id.search_view);
        searchClearButton = (ImageView) findViewById(R.id.search_clear);

        try {
            // Set cursor colour to white
            // http://stackoverflow.com/a/26544231/1692770
            // https://github.com/android/platform_frameworks_base/blob/kitkat-release/core/java/android/widget/TextView.java#L562-564
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(toolbarSearchView, R.xml.cursor);
        } catch (Exception ignored) {
        }

        // Search text changed listener
        toolbarSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchClearButton.setEnabled(true);
                searchClearButton.setVisibility(View.VISIBLE);
                sendBandRequest(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Clear search text when clear button is tapped
        searchClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbarSearchView.setText("");
                searchClearButton.setEnabled(false);
                searchClearButton.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void addCards() {


        for(Band band: mBandList) {
            BandCard card = new BandCard(this);
            card.setTitle(band.getName());

            /*
            String genres = "";

            if(band.getGenres().size() == 0) {
                genres = "Undefined genre";
            }
            else {
                ArrayList<String> bandGenres = band.getGenres();

                genres += bandGenres.get(0);

                for (int i = 1; i < bandGenres.size(); i++) {
                    genres += ", " + bandGenres.get(i);
                }
            }
            */

            card.setDescription(band.getFollowers() + " people like this");

            card.setDrawable(band.getImageLink());

            mListView.add(card);
        }

    }

    private void sendBandRequest(String bandName) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,getRequestURL(bandName),new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mBandList = parseBandJSON(response);
                removePrevCards();
                addCards();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mRequestQueue.add(request);
    }

    private void removePrevCards() {
        mListView.clear();
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
        return Normalizer.normalize(APICallerSingleton.URL_SPOTIFY_SEARCH + "?q=" + name.replaceAll(" ", "+") + "&type=artist", Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Log.d("Activity","Activity result");
        Log.d("Data",data.toString());
        // Pass the activity result to the fragment, which will
        // then pass the result to the login button.
        if (drawerFragment != null) {
            Log.d("Fragment","NOT NULL");
            drawerFragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
