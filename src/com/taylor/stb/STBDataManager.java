package com.taylor.stb;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.tvezu.localdb.LocalDBManager;
import com.tvezu.restclient.AsyncRESTClient;
import com.tvezu.restclient.RESTException;
import com.tvezu.urc.restclient.*;

import java.util.*;


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
    private Map<String,List<SetTopBox>> mSTBMap;

    private Location mProvince,mCity;
    private Operator mOperator;
    private UrcObject mBrand;
    private SetTopBox mSetTopBox;

    private static final int MAX_REQUEST_TIMES = 3 ;
    private int requestTimes = 0 ;

    public void setProvince(Location province){
        mProvince = province;
    }

    public void setCity(Location city){
        mCity = city;
        mOperator = null;
    }

    public void setOperator(Operator operator){
        mOperator = operator;
    }

    public void setBrand(UrcObject brand){
        mBrand = brand;
    }

    public void setSetTopBox(SetTopBox setTopBox){
        mSetTopBox = setTopBox;
        afterSTBSelected();
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

    public UrcObject getBrand(){
        return mBrand;
    }

    public SetTopBox getSetTopBox(){
        return mSetTopBox;
    }


    public STBDataManager(Context context){
        this.mContext = context;
        mUrcClient = new AsyncUrcClient(mContext,new Handler());
        mLocalDBManager = LocalDBManager.getInstance(mContext);

    }


    public void setDataListener(DataListener listener){
        this.mDataListener = listener;
        Log.d(TAG,mDataListener != null ? "非空" : "空\n");

    }

    /**
     * get user location by baidu city code.
     *
     * @param cityCode use wifi/IP query location in baidu service.
     *
     */

    /*    */
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

    private void querySTBByOperator(){
        mUrcClient.requestSetTopBoxListByOperator(mOperator,this,this);
    }

    private void querySTBByBrand(){
        List<SetTopBox> setTopBoxes = mSTBMap.get(mBrand.getName());
        mDataListener.onQuerySucceed(setTopBoxes);

    }

    public void afterSTBSelected(){
        mUrcClient.requestPidMappingByOperator(mOperator, new AsyncRESTClient.OnResponseListener() {
            @Override
            public void onResponse(Object response) {
                if (response!=null){
                    List<PidMapping> pidMapping = ((ListResult<PidMapping>)response).list;
                    mLocalDBManager.updatePidsToLocalDB(pidMapping);
                    Log.d(TAG,"Updated pids to local db.");
                }
            }
        },new AsyncRESTClient.OnErrorListener() {
            @Override
            public void onError(RESTException e) {
                Log.d(TAG,"request pid mapping by operator error!",e);
            }
        });
        mUrcClient.requestIRCodeListBySetTopBox(mSetTopBox,new AsyncRESTClient.OnResponseListener() {
            @Override
            public void onResponse(Object response) {
                List<Key> keys = ((ListResult<Key>)response).list;
                mLocalDBManager.updateKeysToLocalDB(keys);
                Log.d(TAG,"Updated keys to local db.");
            }
        }, new AsyncRESTClient.OnErrorListener() {
            @Override
            public void onError(RESTException e) {
                Log.d(TAG,"request keys error!",e);

            }
        });
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
        Log.d(TAG,"Format STB data.");
       // List<Map<String,List<SetTopBox>>> stbDate = new ArrayList<Map<String, List<SetTopBox>>>();
        mSTBMap = new HashMap<String,List<SetTopBox>>() ;
        for (int i = 0 ; i < setTopBoxes.size() ; i ++ ){
            SetTopBox stb = setTopBoxes.get(i);
            String brand = stb.getBrand();
            if (mSTBMap.get(brand) == null){
                List<SetTopBox> modelList = new ArrayList<SetTopBox>();
                modelList.add(stb);
                mSTBMap.put(brand, modelList);
            }else {
                List<SetTopBox> modelList = mSTBMap.get(brand);
                modelList.add(stb);
            }
        }
        Set<String> set = mSTBMap.keySet();
        List<UrcObject> brandList = new ArrayList<UrcObject>();
        for(String s : set) {
            UrcObject brand = new UrcObject();
            brand.setName(s);
            brandList.add(brand);
        }
        mDataListener.onQuerySucceed(brandList);
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
        mRequestContent = type;
        switch (type){
            case BAIDU_LOCATION:
                getLocationByCityCode(131);
            case PROVINCE:
                if(mAllProvince != null && mAllProvince.size() == 31){
                    Log.d(TAG,"return mAllProvince" + mAllProvince.size());
                    mDataListener.onQuerySucceed(mAllProvince);
                    break;
                }
                queryAllProvince();
                break;
            case CITY:
                queryCityByProvince(mProvince);
                break;
            case OPERATOR:
                queryOperatorByCity(mCity);
                break;
            case STB_BRAND:
                querySTBByOperator();
                break;
            case STB_MODEL:
                querySTBByBrand();


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
        Log.d(TAG,"Content Type : " + mRequestContent.name());
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
                Log.d(TAG,"Response : STB brand.");
                List<SetTopBox> setTopBoxes = ((ListResult<SetTopBox>)response).list;
                Log.d(TAG,"List result + " + setTopBoxes.toString());
                formatSTB(setTopBoxes);
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
