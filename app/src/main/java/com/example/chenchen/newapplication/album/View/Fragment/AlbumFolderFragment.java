package com.example.chenchen.newapplication.album.View.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.chenchen.newapplication.R;
import com.example.chenchen.newapplication.album.Adapter.AlbumFolderAdapter;
import com.example.chenchen.newapplication.album.Database.MyDatabaseHelper;
import com.example.chenchen.newapplication.album.Database.MyDatabaseOperator;
import com.example.chenchen.newapplication.album.ImageScanActivity;
import com.example.chenchen.newapplication.album.MainActivity;
import com.example.chenchen.newapplication.album.entity.AlbumFolderInfo;
import com.example.chenchen.newapplication.album.imageloader.ImageLoaderWrapper;
import com.example.chenchen.newapplication.tensorflow.Config;
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
    private FragmentManager manager;
    private FragmentTransaction ft;

    public AlbumFolderFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("chen", "AlbumFolderFragment onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_album_folder, container, false);
        folderListView = (ListView) rootView.findViewById(R.id.list_album);
        manager = getFragmentManager();

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

                    String url = folder.get(key);

                    count = ImageDealer.getFolderImageCount(getActivity(), key);

                    albumFolderInfo = new AlbumFolderInfo(key, count, url);
//                    Log.d("chen", albumFolderInfo.toString());
                    albumFolderInfoList.add(albumFolderInfo);

                }
                break;
            }
//        Log.d("chen","albumFolderInfoList size ="+albumFolderInfoList.size());

        }
    }

    @Override
    public void onStart() {
        init();
        AlbumFolderAdapter albumFolderAdapter = new AlbumFolderAdapter(getActivity(), albumFolderInfoList);
        folderListView.setAdapter(albumFolderAdapter);
        folderListView.setOnItemClickListener(this);
        super.onStart();
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


    //每一项的点击事件   listView.setOnclickListen(new Adapter.......)
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == folderListView) {

            albumFolderInfo = albumFolderInfoList.get(position);
            String type = albumFolderInfo.getName();
            AlbumDetailFragment fragment = new AlbumDetailFragment("folder_name", type);
            ft = manager.beginTransaction();
            ft.add(R.id.content, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN); //设置动画和效果
            try {
                android.support.v7.app.ActionBar actionBar = MainActivity.actionBar;
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle("");
            } catch (Exception e) {
                ;
            }
            ft.addToBackStack(null);//将fragment加入回退栈
            ft.commit();
        }
    }
}