package com.taylor.stb;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.tvezu.localdb.LocalDBManager;
import com.tvezu.restclient.AsyncRESTClient;
import com.tvezu.restclient.RESTException;
import com.tvezu.urc.restclient.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Taylor on 14-7-21.
 * Manage spinner's data.
 */
public class STBDataManager implements AsyncRESTClient.OnErrorListener,AsyncRESTClient.OnResponseListener{
    private static final String TAG = Constant.TAG;

    private Context mContext;

    private DataListener mDataListener;

    private AsyncUrcClient mUrcClient;

    private LocalDBManager mLocalDBManager;

    private ContentType mRequestContent = ContentType.BAIDU_LOCATION;

    private Location mBaiduProvince,mBaiduCity;
    public Location mDefaultProvince,mDefaultCity;
    private List<Location> mAllProvince;

    private Location mProvince,mCity;
    private Operator mOperator;

    private static final int MAX_REQUEST_TIMES = 3 ;
    private int requestTimes = 0 ;

    public void setProvince(Location province){
        mProvince = province;
    }

    public void setCity(Location city){
        mCity = city;
    }

    public void setOperator(Operator operator){
        mOperator = operator;
    }

    public Location getProvince(){
        return mProvince;
    }

    public Location getCity(){
        return mCity;
    }

    public Operator getOperator(){
        return mOperator;
    }


    public STBDataManager(Context context){
        this.mContext = context;
        mUrcClient = new AsyncUrcClient(mContext,new Handler());
        mLocalDBManager = LocalDBManager.getInstance(mContext);

    }

    public void setDataListener(DataListener listener){
        this.mDataListener = listener;
        Log.d(TAG,mDataListener != null ? "非空" : "空");

    }

    /**
     * get user location by baidu city code.
     *
     * @param cityCode use wifi/IP query location in baidu service.
     *
     */
    private void getLocationByCityCode(int cityCode){
        //DEBUG
        cityCode = 131;
        mRequestContent = ContentType.BAIDU_LOCATION;
        mUrcClient.requestLocationByBaiduCityCode(cityCode,this,this);
    }

    /**
     * Query all province.
     */
    private void queryAllProvince(){
        mUrcClient.requestProvinceList(this,this);
    }

    /**
     * Query city by selected province.
     * @param province
     */
    private void queryCityByProvince(Location province){
        mUrcClient.requestSublocationList(province,this,this);
    }

    /**
     * Query operator by select city
     * @param city
     */
    private void queryOperatorByCity(Location city){
        mUrcClient.requestOperatorListByLocation(city,this,this);
    }
    /**
     * Comparing baidu location with list,if they same, use that location as the default value.
     * @param list
     */
    public void setDefaultLocation(List<Location> list){
        if (mRequestContent == ContentType.PROVINCE){
            if (mBaiduProvince != null && list != null){
                for (Location location : list){
                    if (location.equals(mBaiduProvince)){
                        mDefaultProvince = location;
                        requestData(ContentType.CITY);
                        Log.d(TAG,"Set default province :" + mDefaultProvince.getName());
                    }
                }
            }
        }
        if (mRequestContent == ContentType.CITY){
            if (mBaiduCity != null){
                for (Location location : list){
                    if (location.equals(mBaiduCity)){
                        mDefaultCity = location;
                        Log.d(TAG,"Set default city : " + mDefaultCity.getName());
                    }
                }
            }
        }
    }

    public void formatSTB(List<SetTopBox> setTopBoxes){
       // List<Map<String,List<SetTopBox>>> stbDate = new ArrayList<Map<String, List<SetTopBox>>>();
        Map<String,List<SetTopBox>> map = new HashMap<String,List<SetTopBox>>() ;
        for (int i = 0 ; i < setTopBoxes.size() ; i ++ ){
            SetTopBox stb = setTopBoxes.get(i);
            String brand = stb.getBrand();
            if (map.get(brand) == null){
                List<SetTopBox> modelList = new ArrayList<SetTopBox>();
                modelList.add(stb);
                map.put(brand,modelList);
            }else {
                List<SetTopBox> modelList = map.get(brand);
                modelList.add(stb);
            }
        }
        Log.d(TAG," Brand map to string " + map.toString());
    }

    @Override
    public void onError(RESTException e) {
        if (requestTimes < MAX_REQUEST_TIMES){
            requestData(mRequestContent);
            Log.d(TAG,"Sll request error times = " + requestTimes,e) ;
        }else {
            Log.d(TAG,"Give up request");
        }
    }

    public void requestData(ContentType type){
        Log.d(TAG,"spinner request data with " + type.name());
        if (mRequestContent == type){
            requestTimes += 1;
        }else {
            requestTimes = 0;
        }
        switch (type){
            case BAIDU_LOCATION:
                mRequestContent = type;
                getLocationByCityCode(131);
            case PROVINCE:
                mRequestContent = type;
                if(mAllProvince != null && mAllProvince.size() == 31){
                    Log.d(TAG,"return mAllProvince" + mAllProvince.size());
                    mDataListener.onQuerySucceed(mAllProvince);
                    break;
                }
                queryAllProvince();
                break;
            case CITY:
                mRequestContent = type;
                queryCityByProvince(mProvince);
                break;
            case OPERATOR:
                queryOperatorByCity(mCity);
                mRequestContent = type;

        }
    }
    /**
     * All response in sll service show here.
     * @param response
     */
    @Override
    public void onResponse(Object response) {
        if (response == null){
            if (requestTimes <= MAX_REQUEST_TIMES){
                onError(new RESTException(-444));
                Log.d(TAG, "Response is null");
                return;
            }else {
                requestTimes = 0 ;
                return;
            }
        }
        switch (mRequestContent){
            case BAIDU_LOCATION:
                List<Location> result = ((ListResult<Location>)response).list;
                if (result != null && result.size() > 0){
                    if (result.size() >= 1){ // Means there is maybe only have province in result. No city.
                        mBaiduProvince = result.get(0);
                    }
                    if (result.size() >= 2){
                        mBaiduCity = result.get(1);
                    }
                    Log.d(TAG,"Province:" + (mBaiduProvince != null ? mBaiduProvince.getName() : "Empty") +
                            "City:" + (mBaiduCity != null ? mBaiduCity.getName() : "Empty"));
                    requestData(ContentType.PROVINCE);
                    if (mBaiduCity == null && mBaiduProvince == null){
                        requestData(ContentType.PROVINCE);
                    }

                }

                break;
            case PROVINCE:
                mAllProvince = ((ListResult<Location>)response).list;
                for (Location location : mAllProvince){
                    Log.d(TAG,"Requested Province List " + location.getName());
                }
                
                mAllProvince.remove(mAllProvince.size()-1);
                mAllProvince.remove(mAllProvince.size()-1);
                mAllProvince.remove(mAllProvince.size()-1);

                if (mDefaultProvince == null) {
                    setDefaultLocation(mAllProvince);
                }
                mDataListener.onQuerySucceed(mAllProvince);
                break;
            case CITY:
                List<Location> cityResult = ((ListResult<Location>)response).list;
                if (mDefaultCity == null){
                    setDefaultLocation(cityResult);
                }
                mDataListener.onQuerySucceed(cityResult);
                break;
            case OPERATOR:
                List<Operator> operators = ((ListResult<Operator>)response).list;
                mDataListener.onQuerySucceed(operators);
                break;
            case STB_BRAND:
                break;
            case STB_MODEL:
                break;

            default:break;

        }

    }

    public enum ContentType {
        BAIDU_LOCATION,
        PROVINCE,
        CITY,
        DISTRACT,
        OPERATOR,
        STB_BRAND,
        STB_MODEL;

    }
}
