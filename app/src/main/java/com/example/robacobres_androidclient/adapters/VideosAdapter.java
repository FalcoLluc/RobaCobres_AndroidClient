package com.example.robacobres_androidclient.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import androidx.lifecycle.LifecycleOwner;
import com.example.robacobres_androidclient.R;
import com.example.robacobres_androidclient.models.Video;

import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {
    private List<Video> videoList;
    private LifecycleOwner lifecycleOwner;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView Description;
        YouTubePlayerView youtubePlayerView;

        public ViewHolder(View v) {
            super(v);
            Description = itemView.findViewById(R.id.Descripcion);
            youtubePlayerView = itemView.findViewById(R.id.youtube_player_view);
        }
    }

    public VideosAdapter(List<Video> videoList, LifecycleOwner lifecycleOwner) {
        this.videoList = videoList;
        this.lifecycleOwner = lifecycleOwner;
    }

    @Override
    public VideosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.videoview, parent, false);
        VideosAdapter.ViewHolder vh = new VideosAdapter.ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(VideosAdapter.ViewHolder holder, final int position) {
        Video video = videoList.get(position);
        holder.Description.setText(video.getDescripcion());
        lifecycleOwner.getLifecycle().addObserver(holder.youtubePlayerView);
        holder.youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                //Se obtiene el id a partir de la url
                String videoId = obtenerIDdeURL(video.getUrl());
                if (videoId != null) {
                    youTubePlayer.cueVideo(videoId, 0);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    //Para obtener el id a partir de la url
    private String obtenerIDdeURL(String videoUrl) {
        String videoId = null;
        if (videoUrl != null && videoUrl.contains("youtube.com")) {
            String[] splitUrl = videoUrl.split("v=");
            if (splitUrl.length > 1) {
                videoId = splitUrl[1].split("&")[0];
            }
        } else if (videoUrl != null && videoUrl.contains("youtu.be")) {
            String[] splitUrl = videoUrl.split("youtu\\.be/");
            if (splitUrl.length > 1) {
                videoId = splitUrl[1];
            }
        }
        return videoId;
    }
}
