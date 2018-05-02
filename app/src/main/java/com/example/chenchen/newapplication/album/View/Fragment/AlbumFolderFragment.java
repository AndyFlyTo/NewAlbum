package com.example.chenchen.newapplication.album.View.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.chenchen.newapplication.R;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chenchen on 18-4-30.
 */

public class AlbumFolderFragment extends Fragment {

//    public static AlbumFolderFragment newInstance(List<AlbumFolderInfo> albumFolderInfoList) {
//        AlbumFolderFragment fragment = new AlbumFolderFragment();
//        Bundle args = new Bundle();
//        args.putSerializable(ARG_PARAM1, (Serializable) albumFolderInfoList);
//        fragment.setArguments(args);
//        return fragment;
//    }


    public AlbumFolderFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("chen","AlbumFolderFragment onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_album_folder, container, false);
//        mFolderListView = (ListView) rootView.findViewById(R.id.list_album);
//        ImageLoaderWrapper loaderWrapper = ImageLoaderFactory.getLoader();
//        AlbumFolderAdapter albumFolderAdapter = new AlbumFolderAdapter(mAlbumFolderInfoList, loaderWrapper);
//        mFolderListView.setAdapter(albumFolderAdapter);
//        mFolderListView.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        Log.d("chen","FolderFragment onAttach");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Log.d("chen","FolderFragment onDetach");
        super.onDetach();
    }

//    private List<AlbumFolderInfo> mAlbumFolderInfoList;
//    private ListView mFolderListView;
//
//    /**
//     * @param albumFolderInfoList 相册目录列表
//     * @return
//     */
//    public static AlbumFolderFragment newInstance(List<AlbumFolderInfo> albumFolderInfoList) {
//        AlbumFolderFragment fragment = new AlbumFolderFragment();
//        Bundle args = new Bundle();
//        args.putSerializable(ARG_PARAM1, (Serializable) albumFolderInfoList);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mAlbumFolderInfoList = (List<AlbumFolderInfo>) getArguments().getSerializable(ARG_PARAM1);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View rootView = inflater.inflate(R.layout.fragment_album_directory, container, false);
//        mFolderListView = (ListView) rootView.findViewById(R.id.list_album);
//        ImageLoaderWrapper loaderWrapper = ImageLoaderFactory.getLoader();
//        AlbumFolderAdapter albumFolderAdapter = new AlbumFolderAdapter(mAlbumFolderInfoList, loaderWrapper);
//        mFolderListView.setAdapter(albumFolderAdapter);
//        mFolderListView.setOnItemClickListener(this);
//        return rootView;
//    }
//
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof AlbumView) {
//            mAlbumView = (AlbumView) context;
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mAlbumView = null;
//    }
//
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        if (parent == mFolderListView) {
//            if (mAlbumView != null) {
//                AlbumFolderInfo albumFolderInfo = mAlbumFolderInfoList.get(position);
//                mAlbumView.switchAlbumFolder(albumFolderInfo);
//            }
//        }
//    }
}
