package com.example.chenchen.newapplication.album.View.Fragment;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import android.support.v4.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.chenchen.newapplication.R;
import com.example.chenchen.newapplication.album.Adapter.AlbumAdapter;
import com.example.chenchen.newapplication.album.MainActivity;
import com.example.chenchen.newapplication.album.entity.AlbumInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.chenchen.newapplication.tensorflow.ImageDealer.getAlbumInfo;

/**
 * Created by chenchen on 18-5-2.
 */

public class AlbumFragment extends Fragment {

    private String content;
    private FragmentManager manager;
    private FragmentTransaction ft;
    private AlbumAdapter adapter;


    private List<AlbumInfo> albumList = new ArrayList<AlbumInfo>();

    private List<Map<String, String>> result;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         //GridList
        View view = inflater.inflate(R.layout.fragment_album,container,false);
        initAlbums();
        GridView listView = (GridView) view.findViewById(R.id.album_list);
        manager = getFragmentManager();  //v4包中
        adapter = new AlbumAdapter(getActivity(), R.layout.album_item, albumList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                String type = result.get(position).get("album_name");

                AlbumDetailFragment fragment=new AlbumDetailFragment("album_name",type);
                ft = manager.beginTransaction();
                ft.add(R.id.content , fragment);
                ft.setTransition(FragmentTransaction. TRANSIT_FRAGMENT_OPEN); //设置动画和效果
                try {
                    android.support.v7.app.ActionBar actionBar = MainActivity.actionBar;
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    actionBar.setTitle("");
                } catch (Exception e) {
                    ;
                }
                //将fragment加入回退栈
                ft.addToBackStack( null);
                ft.commit();
            }
        });
        return view;
    }



    private void initAlbums() {
        AlbumInfo album;
        if (getActivity().getApplicationContext() == null)
            Log.d("getContext() in Album", "null");
        result = getAlbumInfo(getActivity().getApplicationContext());
        for (Map<String, String> s: result) {
            album = new AlbumInfo(s.get("album_name"), s.get("image"));
            albumList.add(album);
        }

    }
    public void onAttach(Context context) {
        Log.d("chen", "AlbumFragment onAttach");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Log.d("chen","AlbumFragment onDeach");
        super.onDetach();
    }

    public void onRefresh() {

        albumList.clear();
        initAlbums();
        adapter.notifyDataSetChanged();
    }
}
