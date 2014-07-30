package com.taylor.stb;

import android.util.Log;

import java.lang.annotation.Target;
import java.util.ArrayList;

/**
 * Created by Taylor on 14-7-17.
 */
public final class Constant {

    public static final String TAG = "Linker2";

    public static final String LOCATION_FRAGMENT_NAME = "location";
    public static final String STB_FRAGMENT_NAME = "stb";
    /**
     * Baidu LBS API developer key.
     *
     * Ask pujl1 about more information.
     */
    public final static String LBS_KEY = "tyTV8C7hTGwdL0BHd88tezIv";

    /*
     *This url use the IP address query part_location of the user.
     *When failure by Wi-fi.
     */
    public final static String REQUEST_URL = "http://api.map.baidu.com/location/ip?ak=tyTV8C7hTGwdL0BHd88tezIv";

    /**
     * Source description for CVBS 1.
     */
    public static final String SOURCE_CVBS = "av0";

    /**
     * Source description for HDMI port 1.
     */
    public static final String SOURCE_HDMI_1 = "hdmi0";

    /**
     * Source description for HDMI port 2.
     */
    public static final String SOURCE_HDMI_2 = "hdmi1";

    /**
     * Source description for HDMI port 3.
     */
    public static final String SOURCE_HDMI_3 = "hdmi2";

    /**
     * Source description for HDMI port 4.
     */
    public static final String SOURCE_HDMI_4 = "hdmi3";

    /**
     * Source description for VGA.
     */
    public static final String SOURCE_VGA = "vga0";

    /**
     * Source description for tuner.
     */
    public static final String SOURCE_ATV = "atv";

    /**
     * Source description for DTV
     */
    public static final String SOURCE_DTV = "dtv";

    /**
     * Source description for component.
     */
    public static final String SOURCE_COMPONENT = "component0";

    public static ArrayList<Source> SOURCES = new ArrayList<Source>();
    static{
        //Init all Source Data
        for (Source e : Source.values()) {
            if (e == Source.DTV) {
//                if (!TvManager.getInstance().isSupportDTV()) {
//                    continue;
//                }
                Log.d("Constant","e == DTV");
                continue;
            }else if(e == Source.VGA){
//                if (!TvManager.getInstance().isSupportVGA()) {
//                    continue;
//                }
                Log.d("Constant","e == VGA");
            }
            SOURCES.add(e);
        }
    };

    public static final String MAIN_OUT_PUT = "main";

    public enum Source {
        ATV(0, SOURCE_ATV, R.string.source_atv),
        DTV(1, SOURCE_DTV, R.string.source_dtv),
        CVBS(2, SOURCE_CVBS, R.string.source_cvbs),
        COMPONENT(3, SOURCE_COMPONENT, R.string.source_component),
        VGA(4, SOURCE_VGA, R.string.source_vga),
        HDMI1(5, SOURCE_HDMI_1, R.string.source_hdmi0),
        HDMI2(6, SOURCE_HDMI_2, R.string.source_hdmi1),
        HDMI3(7, SOURCE_HDMI_3, R.string.source_hdmi2);
        //HDMI4(8, SOURCE_HDMI_4, R.string.source_hdmi3);

        private int id;
        private String name;
        private int resId;

        private Source(int id, String name, int resId) {
            this.id = id;
            this.name = name;
            this.resId = resId;
        }

        /**
         * get Source ID
         *
         * @return id
         */
        public int getId() {
            return id;
        }

        /**
         * get Source Name
         *
         * @return name
         */
        public String getName() {
            return name;
        }

        /**
         * get Source Resource ID
         *
         * @return resID
         */
        public int getResId() {
            return resId;
        }
    }
}
