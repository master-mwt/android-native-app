package it.univaq.disim.mwt.trakd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.material.button.MaterialButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.univaq.disim.mwt.trakd.adapters.RecyclerViewEpisodeAdapter;
import it.univaq.disim.mwt.trakd.api.TMDB;
import it.univaq.disim.mwt.trakd.model.Episode;
import it.univaq.disim.mwt.trakd.model.Season;
import it.univaq.disim.mwt.trakd.services.DataParserService;
import it.univaq.disim.mwt.trakd.services.UserCollectionService;
import it.univaq.disim.mwt.trakd.utils.Network;
import it.univaq.disim.mwt.trakd.utils.VolleyRequest;

public class SeasonFragment extends Fragment {
    private static final String ARG_SEASON = "arg_season";

    private Season season;
    private Season seasonDetailed;
    private List<Episode> data = new ArrayList<>();
    //private TextView seasonName;
    private TextView seasonOverview;
    private ImageView seasonPosterPath;
    private RecyclerView recyclerView;
    private RecyclerViewEpisodeAdapter recyclerViewEpisodeAdapter;
    private ProgressBar progressBar;
    private MaterialButton markAllButton;
    private boolean isTvShowInCollection;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null){
                String action = intent.getAction();
                switch (action){
                    case DataParserService.FILTER_PARSE_TV_SHOW_SEASON:
                        if(seasonDetailed == null && season.equals(intent.getSerializableExtra(DataParserService.EXTRA))){
                            seasonDetailed = (Season) intent.getSerializableExtra(DataParserService.EXTRA);
                            season.setSeason_id(seasonDetailed.getSeason_id());

                            progressBar.setVisibility(View.INVISIBLE);

                            data.clear();

                            //seasonName.setText(seasonDetailed.getName());
                            if(!seasonDetailed.getOverview().isEmpty())
                                seasonOverview.setText(seasonDetailed.getOverview());
                            else
                                seasonOverview.setText("No description");

                            setTvShowImage(seasonDetailed.getPoster_path());

                            for(Episode episode : seasonDetailed.getEpisodes()){
                                episode.setTv_show_id(season.getTv_show_id());
                                episode.setSeason_id(season.getSeason_id());
                            }

                            data.addAll(seasonDetailed.getEpisodes());
                            recyclerViewEpisodeAdapter.notifyDataSetChanged();

                            Intent i = new Intent(getContext(), UserCollectionService.class);
                            i.putExtra(UserCollectionService.KEY_ACTION, UserCollectionService.ACTION_GET_EPISODES_BY_SEASON);
                            i.putExtra(UserCollectionService.KEY_DATA, seasonDetailed);
                            Objects.requireNonNull(getContext()).startService(i);
                        }
                        break;
                    case UserCollectionService.FILTER_IS_TV_SHOW_IN_COLLECTION:
                        if(seasonDetailed == null){
                            isTvShowInCollection = intent.getBooleanExtra(UserCollectionService.EXTRA, false);

                            recyclerViewEpisodeAdapter = new RecyclerViewEpisodeAdapter(getContext(), data, isTvShowInCollection);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(recyclerViewEpisodeAdapter);

                            /* Check network connection */
                            Network.checkAvailability(getContext(), getFragmentManager());

                            TMDB.requestRemoteTvShowSeason(getContext(), season.getTv_show_id(), season.getSeason_number());
                        }
                        break;
                    case UserCollectionService.FILTER_GET_EPISODES_BY_SEASON:
                        if(isTvShowInCollection && (season.equals(seasonDetailed))){
                            ArrayList<Episode> episodes = (ArrayList<Episode>) intent.getSerializableExtra(UserCollectionService.EXTRA);
                            if(episodes != null){
                                for(Episode e : data){
                                    if(episodes.contains(e)){
                                        data.get(data.indexOf(e)).setWatched(true);
                                    } else {
                                        data.get(data.indexOf(e)).setWatched(false);
                                    }
                                }
                                recyclerViewEpisodeAdapter.notifyDataSetChanged();
                            }
                            recyclerViewEpisodeAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                                @Override
                                public void onChanged() {
                                    boolean allWatched = true;
                                    for(Episode e : data){
                                        if(!e.isWatched()){
                                            allWatched = false;
                                            break;
                                        }
                                    }
                                    if(allWatched){
                                        markAllButton.setText(getString(R.string.season_mark_all_as_unseen));
                                        markAllButton.setBackgroundColor(getResources().getColor(R.color.colorMarked, getContext().getTheme()));
                                    } else {
                                        markAllButton.setText(getString(R.string.season_mark_all_as_seen));
                                        markAllButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary, getContext().getTheme()));
                                    }
                                    super.onChanged();
                                }
                            });

                            markAllButton.setEnabled(true);

                            if(episodes.size() == data.size()){
                                markAllButton.setText(getString(R.string.season_mark_all_as_unseen));
                                markAllButton.setBackgroundColor(getResources().getColor(R.color.colorMarked, getContext().getTheme()));
                            } else {
                                markAllButton.setText(getString(R.string.season_mark_all_as_seen));
                                markAllButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary, getContext().getTheme()));
                            }

                            markAllButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    boolean allWatched = true;
                                    for(Episode e : data){
                                        if(!e.isWatched()){
                                            allWatched = false;
                                            break;
                                        }
                                    }
                                    if(allWatched){
                                        for(Episode e : data){
                                            e.setWatched(false);
                                        }
                                        recyclerViewEpisodeAdapter.notifyDataSetChanged();

                                        Intent intent = new Intent(getContext(), UserCollectionService.class);
                                        intent.putExtra(UserCollectionService.KEY_ACTION, UserCollectionService.ACTION_MARK_SEASON_AS_UNSEEN);
                                        intent.putExtra(UserCollectionService.KEY_DATA, seasonDetailed);
                                        Objects.requireNonNull(getContext()).startService(intent);

                                        markAllButton.setText(getString(R.string.season_mark_all_as_seen));
                                        markAllButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary, getContext().getTheme()));
                                    } else {
                                        for(Episode e : data){
                                            e.setWatched(true);
                                        }
                                        recyclerViewEpisodeAdapter.notifyDataSetChanged();

                                        Intent intent = new Intent(getContext(), UserCollectionService.class);
                                        intent.putExtra(UserCollectionService.KEY_ACTION, UserCollectionService.ACTION_MARK_SEASON_AS_SEEN);
                                        intent.putExtra(UserCollectionService.KEY_DATA, (Serializable) data);
                                        Objects.requireNonNull(getContext()).startService(intent);

                                        markAllButton.setText(getString(R.string.season_mark_all_as_unseen));
                                        markAllButton.setBackgroundColor(getResources().getColor(R.color.colorMarked, getContext().getTheme()));
                                    }
                                }
                            });
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };

    public SeasonFragment() {
        // Required empty public constructor
    }

    public static SeasonFragment newInstance(Season season) {
        SeasonFragment fragment = new SeasonFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SEASON, season);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            season = (Season) getArguments().getSerializable(ARG_SEASON);
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(DataParserService.FILTER_PARSE_TV_SHOW_SEASON);
        intentFilter.addAction(UserCollectionService.FILTER_IS_TV_SHOW_IN_COLLECTION);
        intentFilter.addAction(UserCollectionService.FILTER_GET_EPISODES_BY_SEASON);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, intentFilter);

        if(season.getTv_show_id() != 0 && seasonDetailed == null){

            Intent intent = new Intent(getContext(), UserCollectionService.class);
            intent.putExtra(UserCollectionService.KEY_ACTION, UserCollectionService.ACTION_IS_TV_SHOW_IN_COLLECTION);
            intent.putExtra(UserCollectionService.KEY_DATA, season.getTv_show_id());
            Objects.requireNonNull(getContext()).startService(intent);

            progressBar.setVisibility(View.VISIBLE);
        }

        if(seasonDetailed != null){
            //seasonName.setText(seasonDetailed.getName());
            if(!seasonDetailed.getOverview().isEmpty())
                seasonOverview.setText(seasonDetailed.getOverview());
            else
                seasonOverview.setText("No description");

            setTvShowImage(seasonDetailed.getPoster_path());

            if(recyclerViewEpisodeAdapter != null){
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(recyclerViewEpisodeAdapter);
            }

            if(isTvShowInCollection){
                Intent intent = new Intent(getContext(), UserCollectionService.class);
                intent.putExtra(UserCollectionService.KEY_ACTION, UserCollectionService.ACTION_GET_EPISODES_BY_SEASON);
                intent.putExtra(UserCollectionService.KEY_DATA, seasonDetailed);
                Objects.requireNonNull(getContext()).startService(intent);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_season, container, false);

        //seasonName = view.findViewById(R.id.season_name);

        seasonOverview = view.findViewById(R.id.season_overview);

        seasonPosterPath = view.findViewById(R.id.season_poster_path);

        recyclerView = view.findViewById(R.id.episodes_recycle_view);

        progressBar = view.findViewById(R.id.season_progress);

        markAllButton = view.findViewById(R.id.mark_all);

        // Inflate the layout for this fragment
        return view;
    }

    private void setTvShowImage(String imageUrl){

        String baseUrl = getString(R.string.tmdb_image_baselink);

        if(imageUrl != null && !"".equals(imageUrl) && !"null".equals(imageUrl)){
            String requestUrl = baseUrl + imageUrl;

            VolleyRequest.getInstance(getContext()).getImageLoader().get(requestUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    seasonPosterPath.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.w(TvShowDetailsActivity.class.getName(), (error.getCause() != null) ? error.getCause().getMessage() : error.getMessage());
                }
            });
        }
    }
}
