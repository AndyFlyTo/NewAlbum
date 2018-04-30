package com.example.chenchen.newapplication.album.View.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chenchen.newapplication.R;

/**
 * Created by chenchen on 18-4-30.
 */

public class AlbumFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("chen","AlbumFragment onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_album, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        Log.d("chen","AlbumFragment onAttach");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Log.d("chen","AlbumFragment onDetach");
        super.onDetach();
    }
}
