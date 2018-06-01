package com.example.chenchen.newapplication.album.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chenchen.newapplication.R;
import com.example.chenchen.newapplication.album.entity.AlbumFolderInfo;
import java.util.List;

/**
 * 相册目录适配器
 * <p/>
 * Created by chenchen on 2018/4/30.
 */
public class AlbumFolderAdapter extends BaseAdapter {

    private List<AlbumFolderInfo> albumFolderInfoList;
    private Context context;

    public AlbumFolderAdapter(Context context,List<AlbumFolderInfo> albumFolderInfoList) {
        this.context=context;
        this.albumFolderInfoList = albumFolderInfoList;
    }

    @Override
    public int getCount() {
        if (albumFolderInfoList == null)
            return 0;
        else
            return albumFolderInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return albumFolderInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.album_directory_item, null);
            holder = new ViewHolder();
            holder.ivAlbumCover = (ImageView) convertView.findViewById(R.id.iv_album_cover);
            holder.tvDirectoryName = (TextView) convertView.findViewById(R.id.tv_directory_name);
            holder.tvChildCount = (TextView) convertView.findViewById(R.id.tv_child_count);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();

        }
        AlbumFolderInfo albumFolderInfo = albumFolderInfoList.get(position);

        String url=albumFolderInfo.getUrl();


        Glide
                .with(context)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .crossFade()
                .thumbnail(0.1f).into(holder.ivAlbumCover);

        String folderName = albumFolderInfo.getName();
        holder.tvDirectoryName.setText(folderName);

        holder.tvChildCount.setText(albumFolderInfo.getCount()+"");

        return convertView;


    }

    private static class ViewHolder {
        ImageView ivAlbumCover;
        TextView tvDirectoryName;
        TextView tvChildCount;
    }
}
