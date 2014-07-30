package com.taylor.stb;

import android.app.Activity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.taylor.stb.view.STBButton;
import com.tvezu.urc.restclient.Location;
import com.tvezu.urc.restclient.UrcObject;

import java.util.PriorityQueue;

/**
 * Created by Taylor on 14-7-21.
 *
 * This fragment use to choose user location.
 */
public class LocationFragment extends BasicFragment {

    private static final String TAG = Constant.TAG;

    private static final String NAME = Constant.LOCATION_FRAGMENT_NAME;

    public STBButton mProvinceSpinner,mCitySpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);


        Log.d("scenic"," " + getArguments());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG,"OnActivityCreated");
        super.onActivityCreated(savedInstanceState);
        View parentLayout = getView().findViewById(R.id.part_location);
        parentLayout.setVisibility(View.VISIBLE);
        mProvinceSpinner = (STBButton) getView().findViewById(R.id.select_province);
        mCitySpinner = (STBButton) getView().findViewById(R.id.select_city);
        mProvinceSpinner.setNextButton(mCitySpinner);
        mProvinceSpinner.setContentType(STBDataManager.ContentType.PROVINCE);
        mCitySpinner.setContentType(STBDataManager.ContentType.CITY);
        mProvinceSpinner.setOnClickListener(this);
        mCitySpinner.setOnClickListener(this);
        mEventCallback.onViewReady(NAME);
    }

    @Override
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);
        Log.d(TAG,"onInflate");
        if (savedInstanceState != null){
            Log.d(TAG,"Saved province value = " + savedInstanceState.getCharSequence("province"));
        }
    }

    public void setProvince(Location province){
        mProvinceSpinner.setContent(province);
    }

    public void setCity(Location city){
        mCitySpinner.setContent(city);
    }

    @Override
    public void onStart() {
        Log.d(TAG,"onStart");
        super.onStart();
        mTitle.setText(R.string.title_user_address);
        mLeft.setText(R.string.btn_next);
        mCenter.setVisibility(View.GONE);
        mRight.setText(R.string.btn_exit);

        if (!mCitySpinner.hasContent){
            mLeft.setEnabled(false);
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG,"onAttach");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG,"onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.select_province:
                break;
            case R.id.select_city:
                break;
        }


    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        super.onItemClick(adapterView, view, position, l);
        Log.d(TAG,"onItemClick, " + position);
        UrcObject obj = mSpinner.getData().get(position);
        STBDataManager.ContentType type = mSpinner.getContentType();
        switch (type){
            case PROVINCE:
                mProvinceSpinner.setContent(obj);
                ((MainActivity)getActivity()).chooseComplete(obj,type);
                mLeft.setEnabled(false);
                break;
            case CITY:
                mCitySpinner.setContent(obj);
                ((MainActivity)getActivity()).chooseComplete(obj,type);
                mLeft.setEnabled(true);
        }
    }




}
