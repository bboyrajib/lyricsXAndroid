package com.example.bboyrajib.lyricsx;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by bboyrajib on 15/12/17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<ListItem> listItems;
    private Context context;
    private RecyclerViewClickListener mListener;





    public RecyclerViewAdapter(List<ListItem> listItems, Context context,RecyclerViewClickListener listener) {
        this.listItems = listItems;
        this.context = context;
        mListener=listener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent,false);

        return new ViewHolder(v,mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ListItem listItem=listItems.get(position);
        holder.Song.setText(listItem.getSong());
        holder.Artist.setText(listItem.getArtist());
        holder.Album.setText(listItem.getAlbum());
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView Song,Artist,Album;
        public RecyclerViewClickListener mListener;

        public ViewHolder(View itemView,RecyclerViewClickListener listener) {
            super(itemView);

            Song=(TextView)itemView.findViewById(R.id.song_name);
            Artist=(TextView)itemView.findViewById(R.id.artist_name);
            Album=(TextView)itemView.findViewById(R.id.album_name);
            mListener=listener;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            mListener.onClick(itemView,getAdapterPosition());
        }


    }
    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }

}
