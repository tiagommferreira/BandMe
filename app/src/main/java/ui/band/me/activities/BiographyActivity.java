package ui.band.me.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;

import ui.band.me.API.APIListener;
import ui.band.me.API.APIThread;
import ui.band.me.R;
import ui.band.me.extras.Keys;

public class BiographyActivity extends AppCompatActivity {

    private String bandImageLink;
    private String bandName;
    private ImageView bandPicture;
    private TextView bioTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biography);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        bandImageLink = intent.getStringExtra("bandLink");
        bandName = intent.getStringExtra("bandName");

        getSupportActionBar().setTitle(bandName);

        bandPicture = (ImageView) findViewById(R.id.bandPic);
        Picasso.with(this).load(bandImageLink).into(bandPicture);

        bioTextView = (TextView) findViewById(R.id.bio);

        setUpBio();
    }

    private void setUpBio() {
        new APIThread(getBioUrl(bandName), new APIListener() {
            @Override
            public void requestCompleted(JSONObject response) {
                String bio = parseBioJSON(response);
                bioTextView.setText(bio);
            }


        }).execute();
    }

    private String parseBioJSON(JSONObject response) {
        try {
            return response.getJSONObject("response").getJSONArray("biographies").getJSONObject(0).getString("text") + " ~" + response.getJSONObject("response").getJSONArray("biographies").getJSONObject(0).getString("site");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getBioUrl(String bandName) {
        return Normalizer.normalize(Keys.API.URL_ECHONEST_BIO + "&name=" + bandName.replaceAll(" ", "+") + "&format=json&results=1&start=0&license=cc-by-sa", Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_biography, menu);
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
