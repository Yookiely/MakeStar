package com.aliyun.svideo.base;

import android.os.Environment;

import java.io.File;

/**
 * @author cross_ly DATE 2019/04/23
 * <p>描述:常量值
 */
public class Constants {

    /**
     * 文件存储相关常量
     */
    public static class SDCardConstants {

        /**
         * 裁剪 & 录制 & 转码输出文件的目录
         * "/sdcard/DCIM/Camera/"
         */
        public final static String OUTPUT_PATH_DIR = Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "Camera" + File.separator;

        /**
         * 转码文件后缀
         */
        public final static String TRANSCODE_SUFFIX = ".mp4_transcode";

        /**
         * 裁剪文件后缀
         */
        public final static String CROP_SUFFIX = "-crop.mp4";

        /**
         * 合成文件后缀
         */
        public final static String COMPOSE_SUFFIX = "-compose.mp4";

        static {
            //静态代码快创建目录
            File dir = new File(OUTPUT_PATH_DIR);
            if (!dir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                dir.mkdirs();
            }
        }

    }
}
