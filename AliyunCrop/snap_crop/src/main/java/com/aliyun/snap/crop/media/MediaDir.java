/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.snap.crop.media;

public class MediaDir {

    public String thumbnailUrl;

    public String dirName;

    public String mVideoDirPath;

    public int id;

    public int type;

    public int fileCount;

    public int resId;

    @Override
    public boolean equals(Object o) {
        if (o instanceof MediaDir) {
            MediaDir md = (MediaDir) o;
            return dirName.equals(md.dirName);
        }
        return false;
    }
}
