package ui.band.me.extras;

import java.util.ArrayList;

/**
 * Created by Tiago on 05/05/2015.
 */
public class Band {
    private String name;
    private ArrayList<String> genres;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getGenre() {
        return genres;
    }

    public void setGenre(ArrayList<String> genres) {
        this.genres = genres;
    }
}
