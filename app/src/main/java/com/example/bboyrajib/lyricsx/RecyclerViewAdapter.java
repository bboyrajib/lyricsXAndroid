package com.example.bboyrajib.lyricsx;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by bboyrajib on 15/12/17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<ListItem> listItems;
    private Context context;
    private RecyclerViewClickListener mListener;
    private RecyclerViewLongClickListener mLongListener;






    public RecyclerViewAdapter(List<ListItem> listItems, Context context,RecyclerViewClickListener listener,RecyclerViewLongClickListener longClickListener) {
        this.listItems = listItems;
        this.context = context;
        mListener=listener;
        mLongListener=longClickListener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent,false);

        return new ViewHolder(v,mListener,mLongListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ListItem listItem=listItems.get(position);
        holder.Song.setText(listItem.getSong());
        holder.Artist.setText(listItem.getArtist());
        holder.Album.setText(listItem.getAlbum());
        Picasso.with(context).load(listItem.getImageURL()).into(holder.imageView);
    }

    public void clear(){
        listItems.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<ListItem>listItems){
        listItems.addAll(listItems);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        public TextView Song,Artist,Album;
        public RecyclerViewClickListener mListener;
        public RecyclerViewLongClickListener mLongListener;
        public ImageView imageView;

        public ViewHolder(View itemView,RecyclerViewClickListener listener,RecyclerViewLongClickListener longClickListener) {
            super(itemView);

            Song=(TextView)itemView.findViewById(R.id.song_name);
            Artist=(TextView)itemView.findViewById(R.id.artist_name);
            Album=(TextView)itemView.findViewById(R.id.album_name);
            imageView=(ImageView)itemView.findViewById(R.id.imageViewRV);

            mListener=listener;
            mLongListener=longClickListener;

          /*  itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int p=getLayoutPosition();
                    Toast.makeText(context," "+p,Toast.LENGTH_SHORT).show();
                    return true;
                }
            });*/
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);



        }

        @Override
        public boolean onLongClick(View v) {
            mLongListener.onLongClick(itemView,getAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(itemView,getAdapterPosition());
        }


    }
    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }

    public interface RecyclerViewLongClickListener{
        void onLongClick(View v,int position);
    }

}
