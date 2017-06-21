package com.example.mikaila.otakubinge;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * SearchAnime validates access token to make calls necessary to perform searches. Additionally,
 * this class converts search results to a JSONArray for parsing.
 *
 * @author Mikaila Smith
 */

public class SearchAnime {
    Context context;
    AniListAPI aniList = new AniListAPI();

    /**
     * SearchAnime Constructor
     *
     * @param c Context of activity
     */
    public SearchAnime(Context c){
        context = c;
    }

    /**
     * Performs search using user-provided query. Generates access token is necessary.
     *
     * @param query User-provided search query
     * @returns A collection of Anime objects containing individual show search result details
     */
    public List<Anime> search(String query) throws JSONException {
        // Generate a new access token is current token is no longer valid
        if (!isTokenValid()) {
            aniList.generateAccessToken(context.getResources().getString(R.string.api_auth_url), context.getResources().getString(R.string.client_id),
                    context.getResources().getString(R.string.client_secret));
        }

        String results = aniList.searchAnime(context.getResources().getString(R.string.api_search_url), query);

        JSONArray results_array = new JSONArray(results);
        List<Anime> animeCollection = getAnimeCollection(results_array);

        return animeCollection;
    }

    /**
     * Convert JSONArray of search results to a collection of Anime objects.
     *
     * @param array JSONArray of anime search results
     * @returns A collection of Anime objects containing individual show search result details
     * @throws JSONException
     */
    public List<Anime> getAnimeCollection(JSONArray array) throws JSONException {
        List<Anime> animeCollection = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            Anime anime = new Anime();

            JSONObject jsonObj  = array.getJSONObject(i);
            anime.set_title_romaji(jsonObj.getString("title_romaji"));
            anime.set_title_english(jsonObj.getString("title_english"));
            anime.set_description(jsonObj.getString("description"));
            anime.set_image_url(jsonObj.getString("image_url_lge"));
            anime.set_total_episodes(jsonObj.getString("total_episodes"));
            anime.set_duration(jsonObj.getString("duration"));

            animeCollection.add(anime);
        }

        return animeCollection;
    }

    /**
     * Check is current access token is still valid.
     *
     * @returns true if token is valid; false if there is no token found or if the token has expired
     */
    public boolean isTokenValid() {
        long timeMillis = System.currentTimeMillis();
        long timeSeconds = TimeUnit.MILLISECONDS.toSeconds(timeMillis);

        // Checks if access token is currently present (on first call accessToken will equal null)
        if (aniList.getAccessToken() != null) {
            // Token is valid (returns true) if current time in seconds is less than token
            // expiration time/date.
            return (timeSeconds < Long.valueOf(aniList.getTokenExpires()));
        }
        else {
            return false;
        }
    }
}
