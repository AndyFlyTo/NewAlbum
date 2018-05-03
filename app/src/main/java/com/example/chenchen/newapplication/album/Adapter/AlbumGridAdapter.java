package com.example.chenchen.newapplication.album.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.clock.utils.common.RuleUtils;
import com.example.chenchen.newapplication.R;
import com.example.chenchen.newapplication.album.entity.AlbumInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chenchen on 18-5-2.
 */

public class AlbumGridAdapter extends BaseAdapter {

    private List<String> albumInfoList;
    private View.OnClickListener onClickListener;
    private OnClickPreviewImageListener onClickPreviewImageListener;
    private Context context;

    public AlbumGridAdapter(Context context,List<String> albumInfoList, OnClickPreviewImageListener OnClickPreviewImageListener){
        this.context=context;
        this.albumInfoList=albumInfoList;
        this.onClickPreviewImageListener=OnClickPreviewImageListener;
    }

    @Override
    public int getCount() {
        if (albumInfoList==null)return 0;
        else
        return albumInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return albumInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AlbumViewHolder holder = null;
        if (convertView == null) {
            holder = new AlbumViewHolder();
            convertView = View.inflate(parent.getContext(), R.layout.album_grid_item, null);

            int gridItemSpacing = (int) RuleUtils.convertDp2Px(parent.getContext(), 2);
            int gridEdgeLength = (RuleUtils.getScreenWidth(parent.getContext()) - gridItemSpacing * 2) / 3;

            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(gridEdgeLength, gridEdgeLength);
            convertView.setLayoutParams(layoutParams);
            holder.albumItem = (ImageView) convertView.findViewById(R.id.iv_album_item);
//            holder.imageSelectedCheckBox = (CheckBox) convertView.findViewById(R.id.ckb_image_select);
            convertView.setTag(holder);

        } else {
            holder = (AlbumViewHolder) convertView.getTag();
//            resetConvertView(holder);

        }

        String url=albumInfoList.get(position);

        Glide
                .with(context)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .crossFade()
                .thumbnail(0.1f).into(holder.albumItem);

        // TODO: 18-5-2
        /*
        ImageInfo imageInfo = mImageInfoList.get(position);
        ImageLoaderWrapper.DisplayOption displayOption = new ImageLoaderWrapper.DisplayOption();
        displayOption.loadingResId = R.mipmap.img_default;
        displayOption.loadErrorResId = R.mipmap.img_error;
        mImageLoaderWrapper.displayImage(holder.albumItem, imageInfo.getImageFile(), displayOption);
*/
//        holder.imageSelectedCheckBox.setChecked(imageInfo.isSelected());
//        if (mImageOnSelectedListener == null) {
//            mImageOnSelectedListener = new CompoundButton.OnCheckedChangeListener() {
//
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    ImageInfo imageInfo = (ImageInfo) buttonView.getTag();
//                    imageInfo.setIsSelected(isChecked);
//                    if (mImageChooseView != null) {
//                        mImageChooseView.refreshSelectedCounter(imageInfo);
//                    }
//                }
//            };
//        }
//        holder.imageSelectedCheckBox.setTag(imageInfo);
//        holder.imageSelectedCheckBox.setOnCheckedChangeListener(mImageOnSelectedListener);//监听图片是否被选中的状态

        if (onClickListener == null) {
            onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String image_url = (String) v.getTag(R.id.image_key);
                    if (onClickPreviewImageListener != null) {
                        onClickPreviewImageListener.onClickPreview(image_url);
                    }
                }
            };
        }
//


        // TODO: 18-5-2  setTag getTag确定那个被摁下
        //// java.lang.IllegalArgumentException: You must not call setTag() on a view Glide is targeting
        holder.albumItem.setTag(R.id.image_key,url);


        holder.albumItem.setOnClickListener(onClickListener);

        return convertView;

    }

    private static class AlbumViewHolder {
        /**
         * 显示图片的位置
         */
        ImageView albumItem;
        /**
         * 图片选择按钮
         */
//        CheckBox imageSelectedCheckBox;
    }

    /**
     * 点击预览图片操作监听借口
     */
    public static interface OnClickPreviewImageListener {
        /**
         * 当想点击某张图片进行预览的时候触发此函数
         *
         * @param image_url
         */
        public void onClickPreview(String image_url);
    }
}
