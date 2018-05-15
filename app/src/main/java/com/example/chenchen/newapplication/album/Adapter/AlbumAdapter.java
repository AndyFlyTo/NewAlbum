package com.example.chenchen.newapplication.album.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chenchen.newapplication.R;
import com.example.chenchen.newapplication.album.View.Fragment.GlideRoundTransform;
import com.example.chenchen.newapplication.album.entity.AlbumInfo;

import java.util.List;

/**
 * Created by chenchen on 18-5-2.
 */

public class AlbumAdapter extends ArrayAdapter<AlbumInfo> {
    private int resourceId;
    private Context context;
    public AlbumAdapter(Context context, int textViewResourceId,
                        List<AlbumInfo> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        this.context = context;
    }

 

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        final AlbumInfo album = getItem(position);

        if (convertView == null) {
            holder = new ViewHolder();
            // TODO: 18-5-2  
            convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);
            holder.img = (ImageView) convertView.findViewById(R.id.album_image);
            holder.tv = (TextView) convertView.findViewById(R.id.album_name);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.tv.setText(album.getAlbum_name());
//        Log.d("chen","adapter album_name="+album.getAlbum_name());
        String url = album.getUrl();
//        Log.d("chen","adapter album_url="+url);

        // TODO: 18-5-2  url==null 加载失败
        Glide
                .with(context)
                .load(url) //
                .centerCrop()
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .crossFade()
                .transform(new GlideRoundTransform(context))  //转变图圆角 // TODO: 18-5-2
                .thumbnail(0.1f).into(holder.img);
        return convertView;
    }
    private static class ViewHolder
    {
        public ImageView img;
        public TextView tv;
    }
}
