package ui.band.me.API;

import org.json.JSONObject;

/**
 * Created by Tiago on 16-05-2015.
 */
public interface APIListener {

    void requestCompleted(JSONObject response);

}
