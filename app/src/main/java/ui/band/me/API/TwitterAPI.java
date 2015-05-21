package ui.band.me.API;

import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import io.fabric.sdk.android.services.concurrency.AsyncTask;
import ui.band.me.extras.Keys;

/**
 * Created by Tiago on 19-05-2015.
 */
public class TwitterAPI extends AsyncTask<String,String,String> {

    //OAuth Header Keys
    private static final String oauth_consumer_key = "oauth_consumer_key";
    private static final String oauth_nonce = "oauth_nonce";
    private static final String oauth_signature = "oauth_signature";
    private static final String oauth_signature_method = "oauth_signature_method";
    private static final String oauth_timestamp = "oauth_timestamp";
    private static final String oauth_token = "oauth_token";
    private static final String oauth_version = "oauth_version";
    private static final String oauth_callback = "oauth_callback";
    private static final String status = "status";

    //message to send
    private String message;
    private Object OAuthTokens;

    public TwitterAPI(String message) {
        this.message = message;
    }

    @Override
    protected String doInBackground(String... strings) {

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("https://api.twitter.com/1.1/statuses/update.json");

        try {
            addAuthorizationHeader(message, post);
            post.setEntity(new StringEntity(percentEncode(status) + "=" + percentEncode(message)));
            HttpResponse response = client.execute(post);

            //parsePostResponse(response);

            HttpEntity entity = response.getEntity();

            BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"), 8);
            StringBuilder theStringBuilder = new StringBuilder();

            String line = null;

            while ((line = reader.readLine()) != null) {
                theStringBuilder.append(line + "\n");
            }

            Log.d("TWITTER", theStringBuilder.toString());
        } catch (Exception e) {
            Log.e("Twitter", e.getMessage());
        }

        return null;
    }

    private void addAuthorizationHeader(String message, HttpRequestBase request) throws Exception {
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");

        String nonce = base64Encode((UUID.randomUUID().toString().replaceAll("-", "").getBytes()));
        String signatureMethod = "HMAC-SHA1";
        String timeStamp = String.valueOf(new Date().getTime() / 1000);
        String version = "1.0";

        //Generating the Parameter String:
        //Add request parameters and status message alphabetically (DO NOT ADD SIGNATURE)
        //Encode keys and values while adding
        //Insert = character between each key and its value
        //Add an ampersand (&) to the end if there are more parameters
        String parameterString =
                percentEncode(oauth_consumer_key) + "=" + percentEncode(Keys.API.TWITTER_KEY) + "&" +
                        percentEncode(oauth_nonce) + "=" + percentEncode(nonce) + "&" +
                        percentEncode(oauth_signature_method) + "=" + percentEncode(signatureMethod) + "&" +
                        percentEncode(oauth_timestamp) + "=" + percentEncode(timeStamp) + "&" +
                        percentEncode(oauth_token) + "=" + percentEncode(Keys.API.TWITTER_ACCESS_TOKEN) + "&" +
                        percentEncode(oauth_version) + "=" + percentEncode(version) + "&" +
                        percentEncode(status) + "=" + percentEncode(message);

        //Generate the SignatureBaseString
        String signatureBaseString = "POST&" + percentEncode("https://api.twitter.com/1.1/statuses/update.json") + "&" + percentEncode(parameterString);

        //Generate the SigningKey
        String signingKey = percentEncode(Keys.API.TWITTER_SECRET) + "&" + percentEncode(Keys.API.TWITTER_ACCESS_TOKEN_SECRET);

        //Generate HMAC-MD5 signature
        String signature = generateHmacSHA1(signingKey, signatureBaseString);

        //Build the HTTP Header
        String oauthHeader =
                "OAuth " +
                        percentEncode(oauth_consumer_key) + "=\"" + percentEncode(Keys.API.TWITTER_KEY) + "\", " +
                        percentEncode(oauth_nonce) + "=\"" + percentEncode(nonce) + "\", " +
                        percentEncode(oauth_signature) + "=\"" + percentEncode(signature) + "\", " +
                        percentEncode(oauth_signature_method) + "=\"" + percentEncode(signatureMethod) + "\", " +
                        percentEncode(oauth_timestamp) + "=\"" + percentEncode(timeStamp) + "\", " +
                        percentEncode(oauth_token) + "=\"" + percentEncode(Keys.API.TWITTER_ACCESS_TOKEN) + "\", " +
                        percentEncode(oauth_version) + "=\"" + percentEncode(version) + "\"";

        request.addHeader("Authorization", oauthHeader);
    }

    private String percentEncode(String status) {
        //This could be done faster with more hand-crafted code.
        try {
            return URLEncoder.encode(status, "UTF-8")
                    // OAuth encodes some characters differently:
                    .replace("+", "%20").replace("*", "%2A")
                    .replace("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String generateHmacSHA1(String signingKey, String signatureBaseString) throws Exception{
        SecretKeySpec keySpec = new SecretKeySpec(signingKey.getBytes("UTF-8"), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(keySpec);
        byte[] result = mac.doFinal(signatureBaseString.getBytes("UTF-8"));
        return base64Encode(result);
    }

    private static String base64Encode(byte[] array) {
        return Base64.encodeToString(array, Base64.NO_WRAP);
    }

}
