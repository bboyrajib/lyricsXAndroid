package com.example.bboyrajib.lyricsx;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
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
    SharedPreferences prefs;





    public RecyclerViewAdapter(List<ListItem> listItems, Context context,RecyclerViewClickListener listener,RecyclerViewLongClickListener longClickListener) {
        this.listItems = listItems;
        this.context = context;
        mListener=listener;
        mLongListener=longClickListener;
        prefs= PreferenceManager.getDefaultSharedPreferences(context);

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
        if(listItem.getTimeStamp().equals("0"))
            holder.TimeStamp.setVisibility(View.GONE);
       else holder.TimeStamp.setText(listItem.getTimeStamp());
        if(listItem.getImageURL()==null || listItem.getImageURL().isEmpty()){
            Picasso.with(context).load("https://deathgrind.club/uploads/posts/2017-09/1506510157_no_cover.png").into(holder.imageView);
        }
        else
            Picasso.with(context).load(listItem.getImageURL()).into(holder.imageView);

        if(prefs.getBoolean("isNightModeEnabledTrue",false)){
            holder.cardView.setCardBackgroundColor(Color.parseColor("#29282e"));
            holder.Song.setTextColor(Color.YELLOW);
            holder.Artist.setTextColor(Color.WHITE);
            holder.Album.setTextColor(Color.WHITE);
            holder.TimeStamp.setTextColor(Color.WHITE);
        }
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

        public TextView Song,Artist,Album,TimeStamp;
        public RecyclerViewClickListener mListener;
        public RecyclerViewLongClickListener mLongListener;
        public ImageView imageView;
        public CardView cardView;

        public ViewHolder(View itemView,RecyclerViewClickListener listener,RecyclerViewLongClickListener longClickListener) {
            super(itemView);

            Song=(TextView)itemView.findViewById(R.id.song_name);
            Artist=(TextView)itemView.findViewById(R.id.artist_name);
            Album=(TextView)itemView.findViewById(R.id.album_name);
            imageView=(ImageView)itemView.findViewById(R.id.imageViewRV);
            TimeStamp=(TextView)itemView.findViewById(R.id.timestamp);
            cardView=(CardView) itemView.findViewById(R.id.cvResults);

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
        public void onClick(View v)  {
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
