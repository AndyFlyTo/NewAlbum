package com.example.chenchen.newapplication.album.entity;

import java.security.Principal;

/**
 * Created by chenchen on 18-5-3.
 */

public class AlbumFolderInfo {
    private String name;
    private int count;
    private String url;
    public AlbumFolderInfo(String name,int count,String url){
        this.name=name;
        this.count=count;
        this.url=url;
    }

    public String getUrl() {
        return url;
    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "name="+name+",count="+count+",url="+url;
    }
}
