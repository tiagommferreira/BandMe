package ui.band.me.API;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.fabric.sdk.android.services.concurrency.AsyncTask;

public class APIThread extends AsyncTask<String,String,JSONObject> {

    private String siteUrl;
    private APIListener apiListener;

    public APIThread(String siteUrl, APIListener apiListener) {
        this.siteUrl = siteUrl;
        this.apiListener = apiListener;
    }

    @Override
    protected JSONObject doInBackground(String... strings) {

        DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());

        HttpGet httpget = new HttpGet(siteUrl);

        httpget.setHeader("Content-Type", "application/json");

        InputStream inputStream = null;

        String result = null;

        try {
            HttpResponse response = httpClient.execute(httpget);

            HttpEntity entity = response.getEntity();

            inputStream = entity.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder theStringBuilder = new StringBuilder();


            String line = null;

            while ((line = reader.readLine()) != null) {
                theStringBuilder.append(line + "\n");
            }

            result = theStringBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        try {
            return new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONObject s) {
        apiListener.requestCompleted(s);
    }
}
