package com.taylor.stb.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.taylor.stb.Constant;
import com.taylor.stb.R;
import com.taylor.stb.DataListener;
import com.taylor.stb.STBDataManager;
import com.tvezu.urc.restclient.UrcObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Taylor on 13-12-11.
 *
 * It displays all the option for the user.
 */
public class STBSpinner extends LinearLayout implements View.OnClickListener,DataListener {

    private static final String TAG = Constant.TAG;

    Context mContext;

    private ProgressBar mProgressBar;
    private STBListView mDataListView;
    private List<? extends UrcObject> mData = new ArrayList<UrcObject>();

    private ImageButton mBtnTop, mBtnBottom;

    private STBDataManager.ContentType contentType ;

    private STBDataManager mDataManager;

    private BaseAdapterImpl mAdapter;

    public STBSpinner(Context context ,STBDataManager manager) {
        super(context);
        mDataManager = manager ;
        initialize(context);
    }

//    public STBSpinner(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        initialize(context);
//    }
//
//    public STBSpinner(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        initialize(context);
//    }

    public List<? extends UrcObject> getData(){
        return this.mData;
    }

    public void setContentType(STBDataManager.ContentType type){
        Log.d(TAG,"spinner will request data. content = " + type.name());
        if (this.contentType != type) {
            this.contentType = type;
            //mData.clear();
            requestData();
        }
    }

    public STBDataManager.ContentType getContentType(){
        return contentType;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
        Log.d(TAG,"Set listView listener in Spinner.");
        mDataListView.setOnItemClickListener(listener);
        Log.d(TAG,listener == null ? "listener is null" : "listener is ready. ");
        Log.d(TAG,mDataListView == null ? "ListView is null" : "ListView is ready.") ;
    }

    private void requestData(){
        mDataListView.setVisibility(INVISIBLE);
        mProgressBar.setVisibility(VISIBLE);
       // mAdapter.notifyDataSetChanged();
        mDataManager.requestData(contentType);
    }

    public void initialize(Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.spinner_list, this, true);
        mProgressBar = (ProgressBar) findViewById(R.id.spinner_progressbar);
        mDataListView = (STBListView) findViewById(R.id.data_list);

        mBtnTop = (ImageButton) findViewById(R.id.sp_btn_top);
        mBtnBottom = (ImageButton) findViewById(R.id.sp_btn_bottom);
        mBtnTop.setOnClickListener(this);
        mBtnBottom.setOnClickListener(this);
        mDataListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mDataListView.getFirstVisiblePosition() == 0 && mBtnTop.getVisibility() != INVISIBLE) {
                    if (mDataListView.getLastVisiblePosition() - mDataListView.getFirstVisiblePosition() > 4) {
                        return;
                    }
                    mBtnTop.setVisibility(INVISIBLE);
                } else if (mDataListView.getFirstVisiblePosition() != 0 && mBtnTop.getVisibility() == INVISIBLE) {
                    mBtnTop.setVisibility(VISIBLE);
                }
                if (mDataListView.getLastVisiblePosition() == mData.size() - 1 && mBtnBottom.getVisibility() != INVISIBLE) {
                    if (mDataListView.getLastVisiblePosition() - mDataListView.getFirstVisiblePosition() > 4) {
                        return;
                    }
                    mBtnBottom.setVisibility(INVISIBLE);
                } else if (mDataListView.getLastVisiblePosition() != mData.size() - 1 && mBtnBottom.getVisibility() != VISIBLE) {
                    mBtnBottom.setVisibility(VISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        int smoothToPosition;
        int lastPageFirstPosition;
        if (view.getId() == R.id.sp_btn_top) {
            int currentFirstVisiblePosition = mDataListView.getFirstVisiblePosition();
            smoothToPosition = currentFirstVisiblePosition - 4;
            Log.i("TEST", "smooth to " + smoothToPosition);
            mDataListView.smoothScrollToPosition(smoothToPosition);
            lastPageFirstPosition = mData.size() - smoothToPosition;
            if (smoothToPosition <= 0) {
                mDataListView.setSelection(0);
            } else {
                mDataListView.setSelection(smoothToPosition);
            }

        }
        if (view.getId() == R.id.sp_btn_bottom) {
            int currentLastVisiblePosition = mDataListView.getFirstVisiblePosition();
            smoothToPosition = currentLastVisiblePosition + 4;
            Log.i("TEST", "smooth to " + smoothToPosition);
            mDataListView.smoothScrollToPosition(smoothToPosition);
            if (smoothToPosition >= mData.size()) {
                mDataListView.setSelection(mData.size());
            } else {
                mDataListView.setSelection(smoothToPosition);
            }
        }
    }

    @Override
    public void onQuerySucceed(List<? extends UrcObject> list) {
        Log.d(TAG,"Load data complete!" + list.size());
        mData = list;
        for (UrcObject obj : list){
            Log.d(TAG,"obj name :" + obj.getName());
        }
        if (mAdapter == null){
            mAdapter = new BaseAdapterImpl(mContext);
        }
        mDataListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mProgressBar.setVisibility(GONE);
        mDataListView.setVisibility(VISIBLE);
    }

    class BaseAdapterImpl extends BaseAdapter {
        private Context context;

        public BaseAdapterImpl(Context context) {
            super();
            this.context = context;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.spinner_list_item, null);
                holder.dataName = (TextView) convertView.findViewById(R.id.data_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.dataName.setText(mData.get(position).getName());
            return convertView;
        }

        public final class ViewHolder {
            public TextView dataName;
        }

    }

}
