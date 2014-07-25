package com.taylor.stb;

import android.os.Bundle;
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

    public STBButton mProvinceSpinner,mCitySpinner;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View parentLayout = getView().findViewById(R.id.part_location);
        parentLayout.setVisibility(View.VISIBLE);
        mProvinceSpinner = (STBButton) getView().findViewById(R.id.select_province);
        mCitySpinner = (STBButton) getView().findViewById(R.id.select_city);
        mProvinceSpinner.setNextButton(mCitySpinner);
        mProvinceSpinner.setContentType(STBDataManager.ContentType.PROVINCE);
        mProvinceSpinner.setContent(null);
        mCitySpinner.setContentType(STBDataManager.ContentType.CITY);

        mProvinceSpinner.setOnClickListener(this);
        mCitySpinner.setOnClickListener(this);

        if (savedInstanceState != null && savedInstanceState.size() > 0){
            Location province = (Location)savedInstanceState.get("province") ;
            Location city = (Location)savedInstanceState.get("city");
            mProvinceSpinner.setContent(province);
            mCitySpinner.setContent(city);
            Log.d(TAG,"Saved instance" + province.getName() + city.getName());
        }

        Log.d(TAG,savedInstanceState.size() + "<<<<<<<<<<Size");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onStart() {
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("province",(Location)mProvinceSpinner.getContent());
        outState.putSerializable("city",(Location)mCitySpinner.getContent());
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
