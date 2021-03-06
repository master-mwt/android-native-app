package it.univaq.disim.mwt.trakd.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import it.univaq.disim.mwt.trakd.R;
import it.univaq.disim.mwt.trakd.model.TvShowCharacter;
import it.univaq.disim.mwt.trakd.utils.VolleyRequest;

public class RecyclerViewTvShowCharacterCardAdapter extends RecyclerView.Adapter<RecyclerViewTvShowCharacterCardAdapter.ViewHolder> {

    private Context context;
    private List<TvShowCharacter> data;

    public RecyclerViewTvShowCharacterCardAdapter(Context context, List<TvShowCharacter> data) {
        this.context = context;
        this.data = data;
        if(this.data == null)
            this.data = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_card_character_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.title.setText(data.get(position).getName());
        holder.subtitle.setText(data.get(position).getCharacter());

        String imageUrl = data.get(position).getProfile_path();

        if(imageUrl != null && !"".equals(imageUrl) && !"null".equals(imageUrl)){

            String requestUrl = context.getString(R.string.tmdb_image_baselink) + imageUrl;

            VolleyRequest.getInstance(context).getImageLoader().get(requestUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    holder.cardImage.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.w(RecyclerViewTvShowCharacterCardAdapter.class.getName(), (error.getCause() != null) ? error.getCause().getMessage() : error.getMessage());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView subtitle;
        ImageView cardImage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.recycler_view_card_adapter_title);
            cardImage = itemView.findViewById(R.id.recycler_view_card_adapter_image);
            subtitle = itemView.findViewById(R.id.recycler_view_card_adapter_subtitle);

        }
    }
}
