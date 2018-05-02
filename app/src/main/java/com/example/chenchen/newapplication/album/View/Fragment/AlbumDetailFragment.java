package com.example.chenchen.newapplication.album.View.Fragment;

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
import com.example.chenchen.newapplication.album.entity.AlbumInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.chenchen.newapplication.tensorflow.ImageDealer.getAlbumInfo;

/**
 * Created by chenchen on 18-4-30.
 */

public class AlbumDetailFragment extends Fragment implements AlbumGridAdapter.OnClickPreviewImageListener {
    private static final int PREVIEW_REQUEST_CODE=1000;
    private List<Map<String,String>> albumInfoList;
    private static final String ARG_PARAM1 = "param1";

    /**
     * 相册视图控件
     */
    private GridView mAlbumGridView;
    private BaseAdapter mAlbumGridViewAdapter;

    private List<AlbumInfo> albumList = new ArrayList<AlbumInfo>();

    /**
     * @param imageInfoList 相册列表
     * @return
     */
    public static AlbumDetailFragment newInstance(List<AlbumInfo> imageInfoList) {
        AlbumDetailFragment fragment = new AlbumDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, (Serializable) imageInfoList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            albumInfoList = (List<Map<String,String>>) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("chen", "AlbumDetailFragment onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_album, container, false);
        mAlbumGridView = (GridView) rootView.findViewById(R.id.album_list);
        init();
//        ImageLoaderWrapper loaderWrapper = ImageLoaderFactory.getLoader();
        mAlbumGridViewAdapter = new AlbumGridAdapter(albumInfoList, this);
        mAlbumGridView.setAdapter(mAlbumGridViewAdapter);
        return rootView;
    }

    protected void init(){
        AlbumInfo album;
        if (getActivity().getApplicationContext() == null)
            Log.d("getContext() in Album", "null");
        albumInfoList = getAlbumInfo(getActivity().getApplicationContext());
        for (Map<String, String> s: albumInfoList) {
            album = new AlbumInfo(s.get("album_name"), s.get("url"));
            albumList.add(album);
        }

    }
    @Override
    public void onAttach(Context context) {
        Log.d("chen", "AlbumDetailFragment onAttach");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Log.d("chen", "AlbumDetailFragment onDetach");
        super.onDetach();
    }

    @Override
    public void onClickPreview(AlbumInfo albumInfo) {
        Intent intent = new Intent(getContext(), ImageScanActivity.class);
        intent.putExtra(ImageScanActivity.EXTRA_IMAGE_INFO, albumInfo);
        intent.putExtra(ImageScanActivity.EXTRA_IMAGE_INFO_LIST, (Serializable) albumInfoList);
        startActivityForResult(intent, PREVIEW_REQUEST_CODE);
    }
}
