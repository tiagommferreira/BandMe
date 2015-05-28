package ui.band.me.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dexafree.materialList.controller.RecyclerItemClickListener;
import com.dexafree.materialList.model.CardItemView;
import com.dexafree.materialList.view.MaterialListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.Normalizer;
import java.util.ArrayList;

import ui.band.me.API.APIListener;
import ui.band.me.API.APIThread;
import ui.band.me.R;
import ui.band.me.database.BandMeDB;
import ui.band.me.extras.Band;
import ui.band.me.extras.BandCard;
import ui.band.me.extras.Keys;
import ui.band.me.fragments.NavigationDrawerFragment;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;


    private ArrayList<Band> mBandList = new ArrayList<>();
    private MaterialListView mListView;

    private EditText toolbarSearchView;
    private ImageView searchClearButton;

    private NavigationDrawerFragment drawerFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Keys.Database.database = new BandMeDB(this);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackground(R.color.primaryColorDark);

        setUpSearch();

        mListView = (MaterialListView) findViewById(R.id.material_bandlistview);
        mListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(CardItemView cardItemView, int pos) {
                Log.d("CLICKED",String.valueOf(mBandList.get(pos)));
                startBandActivity(mBandList.get(pos));
                if(isOnline())
                    Keys.Database.database.insertBand(mBandList.get(pos));
            }

            @Override
            public void onItemLongClick(CardItemView cardItemView, int pos) {

            }
        });

    }

    private void startBandActivity(Band band) {
        Intent i = new Intent(this, BandActivity.class);
        i.putExtra("band", band);
        startActivity(i);
    }

    private void setUpSearch() {
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
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        toolbarSearchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (isOnline()) {
                        sendBandRequest(Normalizer.normalize(v.getText().toString(), Normalizer.Form.NFD).replaceAll("\'", ""));
                    } else {
                        mBandList = Keys.Database.database.getBandsByName(v.getText().toString());
                        Log.d("new answer",String.valueOf(mBandList.size()));
                        removePrevCards();
                        addCards();
                    }

                    Keys.Database.database.insertSearch(v.getText().toString());

                    // hide virtual keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);

                    return true;
                }
                return false;
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

        for (Band band : mBandList) {
            BandCard card = new BandCard(this);
            card.setTitle(band.getName());

            card.setDescription(band.getFollowers() + " people like this");

            card.setDrawable(band.getImageLink());

            mListView.add(card);
        }

    }

    private void sendBandRequest(String bandName) {

        new APIThread(getRequestURL(bandName), new APIListener() {
            @Override
            public void requestCompleted(JSONObject response) {
                mBandList = parseBandJSON(response);
                for (Band band : mBandList) {
                    Log.d("Band name", band.getName());
                }
                removePrevCards();
                addCards();
            }
        }).execute();
    }

    private void removePrevCards() {
        mListView.clear();
    }

    private ArrayList<Band> parseBandJSON(JSONObject response) {
        ArrayList<Band> listBands = new ArrayList<>();

        if (response != null && response.length() > 0) {
            try {
                JSONObject artistsObject = response.getJSONObject(Keys.BandKeys.KEY_ARTISTS);

                JSONArray itemsArray = artistsObject.getJSONArray(Keys.BandKeys.KEY_ITEMS);

                for (int i = 0; i < itemsArray.length(); i++) {
                    Band band = new Band();
                    JSONObject currentItem = itemsArray.getJSONObject(i);
                    band.setId(currentItem.getString(Keys.BandKeys.KEY_ID));
                    band.setName(currentItem.getString(Keys.BandKeys.KEY_NAME));

                    JSONArray genresArray = currentItem.getJSONArray(Keys.BandKeys.KEY_GENRES);
                    ArrayList<String> genres = new ArrayList<>();

                    for (int j = 0; j < genresArray.length(); j++) {
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
        return Normalizer.normalize(Keys.API.URL_SPOTIFY_SEARCH + "?q=" + name.replaceAll(" ", "+") + "&type=artist", Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
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

        // Pass the activity result to the fragment, which will
        // then pass the result to the login button.
        if (drawerFragment != null) {
            drawerFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
