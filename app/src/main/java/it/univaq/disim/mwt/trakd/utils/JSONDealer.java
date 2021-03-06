package it.univaq.disim.mwt.trakd.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import it.univaq.disim.mwt.trakd.model.Episode;
import it.univaq.disim.mwt.trakd.model.TvShowPreview;

public class JSONDealer {

    private static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public static String tvShowListToJSON(List<TvShowPreview> tvShowPreviews){
        return gson.toJson(tvShowPreviews);
    }

    public static List<TvShowPreview> tvShowListFromJSON(String JSONtvShowPreviews){
        Type listTvShowPreviewType = new TypeToken<List<TvShowPreview>>(){}.getType();
        return gson.fromJson(JSONtvShowPreviews, listTvShowPreviewType);
    }

    public static String episodeListToJSON(List<Episode> episodes){
        return gson.toJson(episodes);
    }

    public static List<Episode> episodeListFromJSON(String JSONepisodes){
        Type listEpisodeType = new TypeToken<List<Episode>>(){}.getType();
        return gson.fromJson(JSONepisodes, listEpisodeType);
    }

    public static String dataContainerObjectToJSON(DataContainerObject container){
        return gson.toJson(container);
    }

    public static DataContainerObject dataContainerObjectFromJSON(String JSONcontainer){
        Type dataContainerType = new TypeToken<DataContainerObject>(){}.getType();
        return gson.fromJson(JSONcontainer, dataContainerType);
    }
}
