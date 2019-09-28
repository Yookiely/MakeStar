package com.wingedvampires.attention.model;

//import com.jiajunhui.xapp.medialoader.bean.VideoItem;

import com.example.playerlibrary.provider.VideoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Taurus on 2018/4/19.
 */

public class DataUtils {

    public static List<VideoBean> getVideoList() {
        List<VideoBean> videoList = new ArrayList<>();

        videoList.add(new VideoBean(
                "不耐烦的中国人?",
                "http://open-image.nosdn.127.net/image/snapshot_movie/2016/11/e/9/ac655948c705413b8a63a7aaefd4cde9.jpg",
                "https://mov.bn.netease.com/open-movie/nos/mp4/2017/05/31/SCKR8V6E9_hd.mp4"));


        return videoList;
    }


}
