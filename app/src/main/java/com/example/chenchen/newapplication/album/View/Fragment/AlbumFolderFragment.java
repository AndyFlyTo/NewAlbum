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
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.chenchen.newapplication.R;
import com.example.chenchen.newapplication.album.Adapter.AlbumFolderAdapter;
import com.example.chenchen.newapplication.album.ImageScanActivity;
import com.example.chenchen.newapplication.album.entity.AlbumFolderInfo;
import com.example.chenchen.newapplication.album.imageloader.ImageLoaderWrapper;
import com.example.chenchen.newapplication.tensorflow.ImageDealer;
import com.nostra13.universalimageloader.utils.L;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by chenchen on 18-4-30.
 */

public class AlbumFolderFragment extends Fragment implements AdapterView.OnItemClickListener {

//    public static AlbumFolderFragment newInstance(List<AlbumFolderInfo> albumFolderInfoList) {
//        AlbumFolderFragment fragment = new AlbumFolderFragment();
//        Bundle args = new Bundle();
//        args.putSerializable(ARG_PARAM1, (Serializable) albumFolderInfoList);
//        fragment.setArguments(args);
//        return fragment;
//    }

    private ListView folderListView;
    private List<AlbumFolderInfo> albumFolderInfoList;
    private AlbumFolderInfo albumFolderInfo;
    public AlbumFolderFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("chen", "AlbumFolderFragment onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_album_folder, container, false);
        folderListView = (ListView) rootView.findViewById(R.id.list_album);
        init();
//        ImageLoaderWrapper loaderWrapper = ImageLoaderFactory.getLoader();
        AlbumFolderAdapter albumFolderAdapter = new AlbumFolderAdapter(getActivity(), albumFolderInfoList);
        folderListView.setAdapter(albumFolderAdapter);
        folderListView.setOnItemClickListener(this);
        return rootView;
    }

    private void init() {
        AlbumFolderInfo albumFolderInfo;
        albumFolderInfoList = new ArrayList<>();
        List<Map> folderInfo = ImageDealer.getFolderInfo(getActivity());
        int count;

        if (folderInfo != null) {
            for (Map<String, String> folder : folderInfo) {

                for (String key : folder.keySet()) {
//                    Log.d("chen", "key=" + key);
                    String url = folder.get(key);
//                    Log.d("chen","url="+url);
                    count = ImageDealer.getFolderImageCount(getActivity(), key);
                    albumFolderInfo = new AlbumFolderInfo(key, count, url);
                    Log.d("chen",albumFolderInfo.toString());
                    albumFolderInfoList.add(albumFolderInfo);
                    // }
                }
                break;
            }
        Log.d("chen","albumFolderInfoList size ="+albumFolderInfoList.size());

        }
    }

    @Override
    public void onAttach(Context context) {
        Log.d("chen", "FolderFragment onAttach");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Log.d("chen", "FolderFragment onDetach");
        super.onDetach();
    }


    //每一项的点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == folderListView) {
            Log.d("chen","click Item");

            albumFolderInfo=albumFolderInfoList.get(position);
            Intent intent = new Intent(getContext(), ImageScanActivity.class);
//            intent.putExtra(ImageScanActivity.EXTRA_IMAGE_INFO, albumFolderInfo); //点击的那个图片的url
//            intent.putExtra(ImageScanActivity.EXTRA_IMAGE_INFO_LIST, (Serializable) albumInfoList);
//            startActivityForResult(intent, PREVIEW_REQUEST_CODE);
//            if (albumFolderInfo != null) {
//                AlbumFolderInfo albumFolderInfo = mAlbumFolderInfoList.get(position);
//                mAlbumView.switchAlbumFolder(albumFolderInfo);
//            }
        }
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
