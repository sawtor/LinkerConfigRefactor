package com.taylor.stb;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import com.taylor.stb.view.STBSpinner;

/**
 * Created by Taylor on 14-7-16.
 * Fragments super class.
 */
public class BasicFragment extends Fragment implements View.OnClickListener,AdapterView.OnItemClickListener{
    private static final String TAG = Constant.TAG;

    private Activity mContext;

    public Button mLeft,mCenter,mRight;
    public TextView mTitle,mMessage;
    private View mPartOfLocation,mPartOfSTB;
    public STBSpinner mSpinner;

    private FragmentEvent mEventCallback ;

    private STBDataManager.ContentType mContentType = STBDataManager.ContentType.PROVINCE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate the layout for this fragment.
        return inflater.inflate(R.layout.basic_fragment_layout,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG,"onActivityCreated.");
        inflateViews();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        mEventCallback = (FragmentEvent) activity ;
    }

    private void inflateViews(){
        Log.d(TAG,"inflateViews");
        mTitle = (TextView) getView().findViewById(R.id.fragment_title);
        mMessage = (TextView) getView().findViewById(R.id.fragment_message);
        mLeft = (Button) getView().findViewById(R.id.btn_left);
        mCenter = (Button) getView().findViewById(R.id.btn_center);
        mRight = (Button) getView().findViewById(R.id.btn_right);
        mPartOfLocation = getView().findViewById(R.id.part_location);
        mPartOfSTB = getView().findViewById(R.id.part_stb);

        mLeft.setOnClickListener(this);
        mCenter.setOnClickListener(this);
        mRight.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        Log.d(TAG,"onClick in fragment.");
        mEventCallback.onViewClick(view);
    }

    public STBSpinner getSpinner(){
        return mSpinner;
    }

    public void setSpinner(STBSpinner spinner){
        Log.d(TAG,"Set spinner in basic fragment.");
        mSpinner = spinner;
        mSpinner.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Log.d(TAG,"List view on item click in basic fragment.");
    }
}
