package com.taylor.stb;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import com.taylor.stb.view.STBButton;
import com.tvezu.urc.restclient.Operator;
import com.tvezu.urc.restclient.SetTopBox;
import com.tvezu.urc.restclient.UrcObject;

/**
 * Created by Taylor on 14-7-24.
 */
public class STBFragment extends BasicFragment {
    private static final String TAG = Constant.TAG;

    private static final String NAME = Constant.STB_FRAGMENT_NAME;

    public STBButton mOperatorSpinner,mBrandSpinner,mModelSpinner;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View parentLayout = getView().findViewById(R.id.part_stb);
        parentLayout.setVisibility(View.VISIBLE);
        mOperatorSpinner = (STBButton) getView().findViewById(R.id.select_operator);
        mBrandSpinner = (STBButton) getView().findViewById(R.id.select_brand);
        mModelSpinner = (STBButton) getView().findViewById(R.id.select_model);
        mOperatorSpinner.setNextButton(mBrandSpinner);
        mBrandSpinner.setNextButton(mModelSpinner);
        mOperatorSpinner.setContentType(STBDataManager.ContentType.OPERATOR);
        mBrandSpinner.setContentType(STBDataManager.ContentType.STB_BRAND);
        mModelSpinner.setContentType(STBDataManager.ContentType.STB_MODEL);
        mOperatorSpinner.setOnClickListener(this);
        mBrandSpinner.setOnClickListener(this);
        mModelSpinner.setOnClickListener(this);
        mEventCallback.onViewReady(NAME);
    }

    @Override
    public void onStart() {
        super.onStart();
        mLeft.setText(R.string.btn_previous);
        mCenter.setVisibility(View.VISIBLE);
        mCenter.setText(R.string.btn_next);
        mRight.setText(R.string.btn_exit);
    }

    public void setOperator(Operator operator){
        mOperatorSpinner.setContent(operator);
    }
    public void setBrand(UrcObject brand){
        mBrandSpinner.setContent(brand);
    }
    public void setModel(SetTopBox setTopBox){
        mModelSpinner.setContent(setTopBox);
    }



    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.select_operator:
                Log.d(TAG,"Select operator");
                break;
            case R.id.select_brand:
                Log.d(TAG,"Select Brand");
                break;
            case R.id.select_model:
                Log.d(TAG,"Select Model");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        super.onItemClick(adapterView, view, position, l);
        UrcObject obj = mSpinner.getData().get(position);
        STBDataManager.ContentType type = mSpinner.getContentType();
        switch (type){
            case OPERATOR:
                mOperatorSpinner.setContent(obj);
                mCenter.setEnabled(false);
                break;
            case STB_BRAND:
                mBrandSpinner.setContent(obj);
                break;
            case STB_MODEL:
                mModelSpinner.setContent(obj);
                mCenter.setEnabled(true);
                break;
        }
        ((MainActivity)getActivity()).chooseComplete(obj,type);
    }

}
