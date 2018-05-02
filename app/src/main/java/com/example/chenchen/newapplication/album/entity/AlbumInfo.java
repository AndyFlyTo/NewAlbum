package com.example.chenchen.newapplication.album.entity;

import java.io.Serializable;

/**
 * Created by chenchen on 18-5-2.
 */

//必须实现线性化，负责无法通过putExtra传输
public class AlbumInfo implements Serializable {
    private String album_name;
    private String url;

    public AlbumInfo(String album_name,String url){
        this.album_name=album_name;
        this.url=url;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public String getUrl() {
        return url;
    }
}
