package com.taylor.stb;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import com.taylor.stb.service.LocationService;
import com.taylor.stb.view.STBButton;
import com.taylor.stb.view.STBSpinner;
import com.tvezu.urc.STBSettingManager;
import com.tvezu.urc.restclient.Location;
import com.tvezu.urc.restclient.Operator;
import com.tvezu.urc.restclient.SetTopBox;
import com.tvezu.urc.restclient.UrcObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements FragmentEvent{

    private static final String TAG = Constant.TAG ;

    private STBDataManager mDataManager ;

    FragmentTransaction mFragmentTransaction ;

   // SelectSourceFragment mSelectSourceFragment = new SelectSourceFragment();

    private LocationFragment mLocationFragment = new LocationFragment();

    private STBFragment mSTBFragment = new STBFragment();

    private STBSpinner mSpinner2;

    private PopupWindow mOptionShower;

    private List<String> mFragmentList = new ArrayList<String>();
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
        //chooseLocation();
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
    }

    public void onSourceSelected(String str){
        STBSettingManager stbSettingManager = new STBSettingManager(this);
        stbSettingManager.setSignalSource(str);
    }

    public void chooseLocation(){
        Log.d(TAG,"Choose Location Fragment");
        mFragmentTransaction = getFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.main_container, mLocationFragment, Constant.LOCATION_FRAGMENT_NAME);
        mLocationFragment.setSpinner(mSpinner2);
        mFragmentTransaction.commit();
        mFragmentList.add(Constant.LOCATION_FRAGMENT_NAME);

    }

    public void chooseSTB(){
        Log.d(TAG, "Choose STB Fragment");
        mFragmentTransaction = getFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.main_container, mSTBFragment, Constant.STB_FRAGMENT_NAME);
        mSTBFragment.setSpinner(mSpinner2);
        mFragmentTransaction.commit();
        mFragmentList.add(Constant.STB_FRAGMENT_NAME);
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
            case STB_BRAND:
                mDataManager.setBrand(object);
                break;
            case STB_MODEL:
                mDataManager.setSetTopBox((SetTopBox)object);
                break;
        }
    }

    @Override
    public void onViewClick(View view) {
        switch (view.getId()){
            case R.id.btn_left:
                String currentFragmentTAG = mFragmentList.get(mFragmentList.size()-1);
                if (Constant.LOCATION_FRAGMENT_NAME.equals(currentFragmentTAG)){  //Means location fragment is showing now.
                    chooseSTB();
                }else {
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
                showSpinnerOptions(mSTBFragment.mBrandSpinner);
                break;
            case R.id.select_model:
                showSpinnerOptions(mSTBFragment.mModelSpinner);
                break;

        }
    }

    @Override
    public void onViewReady(String fragmentTAG) {
        if (Constant.LOCATION_FRAGMENT_NAME.equals(fragmentTAG)){
            mLocationFragment.setProvince(mDataManager.getProvince());
            mLocationFragment.setCity(mDataManager.getCity());
        }else {
            mSTBFragment.setOperator(mDataManager.getOperator());
            mSTBFragment.setBrand(mDataManager.getBrand());
            mSTBFragment.setModel(mDataManager.getSetTopBox());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG,"back");
        if (Constant.LOCATION_FRAGMENT_NAME.equals(mFragmentList.get(mFragmentList.size()-1))){ //current fragment is location fragment.finish activity.
            this.finish();
        }
    }
}
