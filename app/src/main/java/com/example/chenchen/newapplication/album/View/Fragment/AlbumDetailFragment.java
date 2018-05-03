package com.example.chenchen.newapplication.album.View.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.example.chenchen.newapplication.R;
import com.example.chenchen.newapplication.album.Adapter.AlbumGridAdapter;
import com.example.chenchen.newapplication.album.ImageScanActivity;
import com.example.chenchen.newapplication.album.MainActivity;
import com.example.chenchen.newapplication.album.entity.AlbumInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.chenchen.newapplication.tensorflow.ImageDealer.getAlbumInfo;
import static com.example.chenchen.newapplication.tensorflow.ImageDealer.getImageInfo;

/**
 * Created by chenchen on 18-4-30.
 */

public class AlbumDetailFragment extends Fragment implements AlbumGridAdapter.OnClickPreviewImageListener {
    private static final int PREVIEW_REQUEST_CODE=1000;
    private List<String> albumInfoList;
    private static final String ARG_PARAM1 = "param1";

    private String type;

    /**
     * 相册视图控件
     */
    private GridView mAlbumGridView;
    private BaseAdapter mAlbumGridViewAdapter;

    private List<AlbumInfo> albumList = new ArrayList<AlbumInfo>();


    public AlbumDetailFragment(){

    }
    @SuppressLint("ValidFragment")   //// TODO: 18-5-2  少了会写不了构造函数
    public AlbumDetailFragment(String type) {
        this.type = type;
    }

    // TODO: 18-5-2   可以使用这种方式试一下。
    public static AlbumDetailFragment newInstance(List<String> albumInfo) {
        AlbumDetailFragment fragment = new AlbumDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, (Serializable) albumInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            albumInfoList = (List<String>) getArguments().getSerializable(ARG_PARAM1);
//        }
    }

    //只是在layout中把背景设成了白色 ，不然是可以看到AlbumFragment的 , 怎么能看不见呢
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("chen", "AlbumDetailFragment onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_detail_album, container, false);
        mAlbumGridView = (GridView) rootView.findViewById(R.id.gv_album);
        if (type!=null){
            initImage();
            try {
                // TODO: 18-5-2 返回键不管用
                android.support.v7.app.ActionBar actionBar = MainActivity.actionBar;
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setTitle(type);
            } catch (Exception e) {
                ;
            }
        }

        mAlbumGridViewAdapter = new AlbumGridAdapter(getContext(),albumInfoList, this);
        mAlbumGridView.setAdapter(mAlbumGridViewAdapter);
        return rootView;
    }

    protected void initImage(){

        if (getActivity().getApplicationContext() == null)
            Log.d("getContext() in Album", "null");
        albumInfoList = getImageInfo(getActivity().getApplicationContext(),"album_name",type);
        for(String s:albumInfoList){
            Log.d("chen",s);
        }
    }
    @Override
    public void onAttach(Context context) {
//        Log.d("chen", "AlbumDetailFragment onAttach");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
//        Log.d("chen", "AlbumDetailFragment onDetach");
        android.support.v7.app.ActionBar actionBar = MainActivity.actionBar;
//        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
//        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Album");

        super.onDetach();
    }

    @Override
    public void onClickPreview(String image_url) {
        Intent intent = new Intent(getContext(), ImageScanActivity.class);
        intent.putExtra(ImageScanActivity.EXTRA_IMAGE_INFO, image_url); //点击的那个图片的url
        intent.putExtra(ImageScanActivity.EXTRA_IMAGE_INFO_LIST, (Serializable) albumInfoList);
        startActivityForResult(intent, PREVIEW_REQUEST_CODE);
    }


}
