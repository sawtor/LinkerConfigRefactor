package com.taylor.stb;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.taylor.stb.view.STBButton;
import com.taylor.stb.view.STBSpinner;
import com.tvezu.urc.STBSettingManager;
import com.tvezu.urc.restclient.Location;
import com.tvezu.urc.restclient.Operator;
import com.tvezu.urc.restclient.UrcObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements FragmentEvent{

    private static final String TAG = Constant.TAG ;

    private static final String LOCATION_FRAGMENT_NAME = "location";
    private static final String STB_FRAGMENT_NAME = "stb";

    private STBDataManager mDataManager ;

    FragmentTransaction mFragmentTransaction ;

   // SelectSourceFragment mSelectSourceFragment = new SelectSourceFragment();

    private LocationFragment mLocationFragment = new LocationFragment();

    private STBFragment mSTBFragment = new STBFragment();

    private STBSpinner mSpinner2;

    private PopupWindow mOptionShower;

    private List<String> mFragmentList = new ArrayList<String>();

    Bundle mLocationBundle = new Bundle();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        /**************************************Select Source **********************************/
//        mFragmentTransaction = getFragmentManager().beginTransaction();
//        mFragmentTransaction.add(R.id.main_container,mSelectSourceFragment);
//        mFragmentTransaction.commit();
        /**************************************Select Source **********************************/
        mDataManager = new STBDataManager(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"Main activity on start, init spinner and start to choose location.");
        mSpinner2 = new STBSpinner(this,mDataManager);
        mDataManager.setDataListener(mSpinner2);
        chooseLocation();
    }

    public void onSourceSelected(String str){
        STBSettingManager stbSettingManager = new STBSettingManager(this);
        stbSettingManager.setSignalSource(str);
    }

//    public void onSpinnerReady(STBSpinner spinner){
//        mDataManager.setDataListener(spinner);
//        mDataManager.getLocationByCityCode(131);
//    }

    public void chooseLocation(){
        mFragmentTransaction = getFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.main_container, mLocationFragment, LOCATION_FRAGMENT_NAME);
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.commit();
        mFragmentList.add(LOCATION_FRAGMENT_NAME);
        mLocationFragment.setSpinner(mSpinner2);
        //mDataManager.requestData(STBDataManager.ContentType.BAIDU_LOCATION);
    }

    public void chooseSTB(){
        mFragmentTransaction = getFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.main_container,mSTBFragment,STB_FRAGMENT_NAME);
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.commit();
        mFragmentList.add(STB_FRAGMENT_NAME);
        mSTBFragment.setSpinner(mSpinner2);
    }

    public void showSpinnerOptions(STBButton button){
        if (mOptionShower == null){
            mOptionShower = new PopupWindow(mSpinner2,button.getWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);
            mOptionShower.setOutsideTouchable(true);
            mOptionShower.setFocusable(true);
            mOptionShower.setBackgroundDrawable(new BitmapDrawable());
        }
        mOptionShower.setWidth(button.getWidth());
        mSpinner2.setContentType(button.getContentType());
        if (mOptionShower.isShowing()){
            Log.d(TAG,"spinner is showing.");
            mOptionShower.dismiss();
            return;
        }
        mOptionShower.showAsDropDown(button);
    }

    public void dismissSpinnerOptions(){
        if (mOptionShower.isShowing()){
            mOptionShower.dismiss();
        }
    }

    public void chooseComplete(UrcObject object,STBDataManager.ContentType type){
        dismissSpinnerOptions();
        switch (type){
            case PROVINCE:
                mDataManager.setProvince((Location)object);
                break;
            case CITY:
                mDataManager.setCity((Location)object);
                break;
            case OPERATOR:
                mDataManager.setOperator((Operator)object);
                break;
        }
    }

    @Override
    public void onViewClick(View view) {
        switch (view.getId()){
            case R.id.btn_left:
                String currentFragmentTAG = mFragmentList.get(mFragmentList.size()-1);
                if (LOCATION_FRAGMENT_NAME.equals(currentFragmentTAG)){  //Means location fragment is showing now.
                    chooseSTB();
                }else {  //Means stb fragment is showing now.
                    chooseLocation();
                }
                break;
            case R.id.btn_right:
                this.finish();
                break;
            case R.id.select_province:
                showSpinnerOptions(mLocationFragment.mProvinceSpinner);
                Log.d(TAG,"Select province");
                break;
            case R.id.select_city:
                Log.d(TAG,"Select city");
                showSpinnerOptions(mLocationFragment.mCitySpinner);
                break;
            case R.id.select_operator:
                showSpinnerOptions(mSTBFragment.mOperatorSpinner);
                break;
            case R.id.select_brand:
                break;

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG,"back");
        if (LOCATION_FRAGMENT_NAME.equals(mFragmentList.get(mFragmentList.size()-1))){ //current fragment is location fragment.finish activity.
            this.finish();
        }
    }
}
