package it.univaq.disim.mwt.trakd.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import it.univaq.disim.mwt.trakd.R;
import it.univaq.disim.mwt.trakd.services.DataParserService;
import it.univaq.disim.mwt.trakd.utils.VolleyRequest;

public class TMDB {

    public static void requestRemoteTvShowDetails(final Context context, long tvShowID){

        final String API_URL = context.getResources().getString(R.string.tmdb_api_url);
        final String API_KEY = context.getResources().getString(R.string.tmdb_api_key);
        final String LANGUAGE = context.getResources().getString(R.string.tmdb_api_language);

        String requestLink = API_URL + "tv/" + tvShowID + "?api_key=" + API_KEY + "&language=" + LANGUAGE;

        doRequest(context, requestLink, DataParserService.ACTION_PARSE_TV_SHOW_DETAILS);
    }

    public static void requestRemoteTvShowsPopular(final Context context, long page){

        final String API_URL = context.getResources().getString(R.string.tmdb_api_url);
        final String API_KEY = context.getResources().getString(R.string.tmdb_api_key);
        final String LANGUAGE = context.getResources().getString(R.string.tmdb_api_language);

        String requestLink = API_URL + "tv/popular" + "?api_key=" + API_KEY + "&language=" + LANGUAGE + "&page=" + page;

        doRequest(context, requestLink, DataParserService.ACTION_PARSE_TV_SHOWS_POPULAR);
    }

    public static void requestRemoteTvShowsTopRated(final Context context, long page){

        final String API_URL = context.getResources().getString(R.string.tmdb_api_url);
        final String API_KEY = context.getResources().getString(R.string.tmdb_api_key);
        final String LANGUAGE = context.getResources().getString(R.string.tmdb_api_language);

        String requestLink = API_URL + "tv/top_rated" + "?api_key=" + API_KEY + "&language=" + LANGUAGE + "&page=" + page;

        doRequest(context, requestLink, DataParserService.ACTION_PARSE_TV_SHOWS_TOP_RATED);
    }

    public static void requestRemoteTvShowSimilars(final Context context, long tvShowID, long page){

        final String API_URL = context.getResources().getString(R.string.tmdb_api_url);
        final String API_KEY = context.getResources().getString(R.string.tmdb_api_key);
        final String LANGUAGE = context.getResources().getString(R.string.tmdb_api_language);

        String requestLink = API_URL + "tv/" + tvShowID + "/similar" + "?api_key=" + API_KEY + "&language=" + LANGUAGE + "&page=" + page;

        doRequest(context, requestLink, DataParserService.ACTION_PARSE_TV_SHOWS_SIMILARS);
    }

    public static void requestRemoteTvShowSeason(final Context context, long tvShowID, int seasonNumber){

        final String API_URL = context.getResources().getString(R.string.tmdb_api_url);
        final String API_KEY = context.getResources().getString(R.string.tmdb_api_key);
        final String LANGUAGE = context.getResources().getString(R.string.tmdb_api_language);

        String requestLink = API_URL + "tv/" + tvShowID + "/season/" + seasonNumber + "?api_key=" + API_KEY + "&language=" + LANGUAGE;

        doRequest(context, requestLink, DataParserService.ACTION_PARSE_TV_SHOW_SEASON);
    }

    public static void requestRemoteTvShowEpisode(final Context context, long tvShowID, int seasonNumber, int episodeNumber){

        final String API_URL = context.getResources().getString(R.string.tmdb_api_url);
        final String API_KEY = context.getResources().getString(R.string.tmdb_api_key);
        final String LANGUAGE = context.getResources().getString(R.string.tmdb_api_language);

        String requestLink = API_URL + "tv/" + tvShowID + "/season/" + seasonNumber + "/episode/" + episodeNumber + "?api_key=" + API_KEY + "&language=" + LANGUAGE;

        doRequest(context, requestLink, DataParserService.ACTION_PARSE_TV_SHOW_EPISODE);
    }

    public static void requestRemoteTvShowCredits(final Context context, long tvShowID){

        final String API_URL = context.getResources().getString(R.string.tmdb_api_url);
        final String API_KEY = context.getResources().getString(R.string.tmdb_api_key);
        final String LANGUAGE = context.getResources().getString(R.string.tmdb_api_language);

        String requestLink = API_URL + "tv/" + tvShowID + "/credits" + "?api_key=" + API_KEY + "&language=" + LANGUAGE;

        doRequest(context, requestLink, DataParserService.ACTION_PARSE_TV_SHOW_CREDITS);
    }

    public static void requestRemoteTvShowsSearch(final Context context, String query, long page){

        final String API_URL = context.getResources().getString(R.string.tmdb_api_url);
        final String API_KEY = context.getResources().getString(R.string.tmdb_api_key);
        final String LANGUAGE = context.getResources().getString(R.string.tmdb_api_language);

        String requestLink = API_URL + "search/tv" + "?api_key=" + API_KEY + "&query=" + query +  "&page=" + page + "&language=" + LANGUAGE;

        doRequest(context, requestLink, DataParserService.ACTION_PARSE_TV_SHOWS_SEARCH);
    }


    private static void doRequest(final Context context, String url, final int action){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callService(context, response, action);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleResponseError(error);
                    }
                });
        VolleyRequest.getInstance(context).getQueue().add(request);
    }

    private static void callService(final Context context, JSONObject response, final int action){
        // Call DataParserService service
        Intent intent = new Intent(context, DataParserService.class);
        intent.putExtra(DataParserService.KEY_ACTION, action);
        intent.putExtra(DataParserService.KEY_DATA, response.toString());
        context.startService(intent);
    }

    private static void handleResponseError(VolleyError error){
        Log.w(TMDB.class.getName(), (error.getCause() != null) ? error.getCause().getMessage() : error.getMessage());
    }
}
