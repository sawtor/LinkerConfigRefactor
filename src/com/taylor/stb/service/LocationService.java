package com.taylor.stb.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.taylor.stb.Constant;

/**
 * Created by Taylor on 14-7-17.
 * Get user location information. province,city,city code.
 */
public class LocationService extends Service {

    private static final String TAG = Constant.TAG;

    private SharedPreferences mPreferences;

    public LocationClient mLocationClient;

    public BDLocationListener mLocationResultListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            bdLocation.getCityCode();
            StringBuffer sb = new StringBuffer(256);
            sb.append("\ntime : ");
            sb.append(bdLocation.getTime());
            sb.append("\nerror code : ");
            sb.append(bdLocation.getLocType());
            sb.append("\ncity code : ");
            sb.append(bdLocation.getCityCode());
            sb.append("\naddress :");
            sb.append(bdLocation.getAddrStr());
            sb.append("\ncity :");
            sb.append(bdLocation.getCity());
            sb.append("\nprovince :");
            sb.append(bdLocation.getProvince());
            sb.append("\nstreet :");
            sb.append(bdLocation.getStreet());
            Log.d(TAG,sb.toString());
            mLocationClient.stop();


        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        option.setIsNeedAddress(true);
        option.setScanSpan(1000);
        option.setOpenGps(false);
        option.setTimeOut(3500);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        /**
         * Request location now.
         * return:
         * 0:succeed.
         * 1:service wasn't started.
         * 2:There is no listener.
         * 6:option's span is too short.
         */
        //int errorCode = mLocationClient.requestLocation();
        Log.d(TAG,"Request location now.") ;
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Location service on create.");
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(mLocationResultListener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
