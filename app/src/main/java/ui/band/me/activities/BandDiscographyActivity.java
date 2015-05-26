package ui.band.me.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;

import ui.band.me.API.APIListener;
import ui.band.me.API.APIThread;
import ui.band.me.R;
import ui.band.me.adapters.DiscographyAdapter;
import ui.band.me.extras.Album;
import ui.band.me.extras.Keys;

public class BandDiscographyActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DiscographyAdapter recyclerAdapter;
    private ArrayList<Album> albums = new ArrayList<>();
    private String bandId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band_discography);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        bandId = i.getStringExtra("bandId");

        sendAlbumsRequest();

        this.recyclerView = (RecyclerView) findViewById(R.id.album_list);
        this.recyclerAdapter = new DiscographyAdapter(this, albums);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void sendAlbumsRequest() {
        new APIThread(getRequestURL(bandId), new APIListener() {
            @Override
            public void requestCompleted(JSONObject response) {
                parseAlbumsJSON(response);
            }
        }).execute();
    }

    private void parseAlbumsJSON(JSONObject response) {

        try {
            JSONArray itemsArray = response.getJSONArray("items");

            for(int i = 0; i < itemsArray.length(); i++) {
                JSONObject albumObject = itemsArray.getJSONObject(i);

                Album a = new Album();

                a.setName(albumObject.getString("name"));
                a.setImage_url(albumObject.getJSONArray("images").getJSONObject(0).getString("url"));
                albums.add(a);
                recyclerAdapter.notifyItemInserted(i);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String getRequestURL(String bandId) {
        return Normalizer.normalize(Keys.API.URL_SPOTIFY_ARTISTS + bandId + "/albums", Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_band_discography, menu);
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
        } else if(id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
