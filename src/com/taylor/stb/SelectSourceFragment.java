package com.taylor.stb;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.taylor.stb.view.SourceListView;

import java.util.ArrayList;

/**
 * Created by Taylor on 14-7-19.
 */
public class SelectSourceFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = Constant.TAG;
    private SourceListView mSourceList;
    private int selectposition;
    private GridView gridView;
    private DataAdapter adapter;
    private Context mContext ;
    private ArrayList<Constant.Source> sources = new ArrayList<Constant.Source>();

    private static int[] icon_normal = new int[]{
            R.drawable.cvbs_icon_normal,
            R.drawable.component_icon_normal, R.drawable.hdmi_icon_normal,
            R.drawable.hdmi_icon_normal, R.drawable.hdmi_icon_normal
    };

    private static int[] icon_focus = new int[]{
            R.drawable.cvbs_icon_focus,
            R.drawable.component_icon_focus, R.drawable.hdmi_icon_focus,
            R.drawable.hdmi_icon_focus, R.drawable.hdmi_icon_focus
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.source_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inflateViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (selectposition != getCurrentSourcePosition()) {
            selectposition = getCurrentSourcePosition();
        }
        gridView.setSelection(selectposition);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        selectposition = position;
       // String selectedSource = sources.get(position).getName() ;
       // Log.i(TAG, "onItemClick, Change source to ===> " + selectedSource);
        //tvCMManager.changeInputSource(Constant.MAIN_OUT_PUT,selectedSource);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
//        STBSettingManager StbSettingManager = new STBSettingManager(this.getActivity()) ;
//        StbSettingManager.setSignalSource(selectedSource);
        //Intent intent = new Intent(this, STBMainActivity.class);
        //this.startActivity(intent);
        ((MainActivity)getActivity()).onSourceSelected("Source");


        getView().findViewById(android.R.id.content).setVisibility(View.INVISIBLE);
        Log.d(TAG,"Invisible") ;
    }

    public void inflateViews(){
        Log.d(TAG,"inflate views.");
        gridView = (GridView) getView().findViewById(R.id.source);
        adapter = new DataAdapter(initData());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
    }


    public ArrayList<SourceItem> initData() {

        ArrayList<SourceItem> data = new ArrayList<SourceItem>();

        for (int i = 0; i < 5; i++) {
            //String str = mContext.getResources().getString(sources.get(i).getResId());
            String str = "Source";
            SourceItem item = new SourceItem(i, icon_normal[i], str);
            data.add(item);
        }
        return data;
    }

    private String getCurrentSource() {
        return Constant.SOURCE_CVBS;
    }

    private int getCurrentSourcePosition() {
        String currentSource = getCurrentSource();
        if (currentSource == null) {
            Log.e(TAG, "CurrentSource is Null");
            return 0;
        }
        for (int i = 0; i < sources.size(); i++) {
            if (currentSource.equals(sources.get(i).getName())) {
                return i;
            }
        }
        return 0;
    }

    private final class DataAdapter extends BaseAdapter {
        private ArrayList<SourceItem> items;

        public DataAdapter(ArrayList<SourceItem> items) {
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LinearLayout.inflate(SelectSourceFragment.this.getActivity(), R.layout.source_item_layout, null);
                holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                holder.sourceName = (TextView) convertView.findViewById(R.id.source_name);
                convertView.setTag(holder);

                convertView.setOnHoverListener(new View.OnHoverListener() {

                    @Override
                    public boolean onHover(View v, MotionEvent event) {
                        int action = event.getAction();
                        switch (action) {
                            case MotionEvent.ACTION_HOVER_ENTER:
                            case MotionEvent.ACTION_HOVER_MOVE:
                                gridView.requestFocusFromTouch();
                                v.setSelected(true);
                                break;
                            case MotionEvent.ACTION_HOVER_EXIT:
                                v.setSelected(false);
                                break;
                        }
                        return false;
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == selectposition){
                convertView.setBackgroundResource(R.drawable.source_list_enable_selector);
            }else{
                convertView.setBackgroundResource(R.drawable.source_list_selector);
            }

            holder.icon.setImageResource(icon_normal[position]);
            holder.sourceName.setText(items.get(position).text);
            return convertView;
        }
    }

    static class ViewHolder {
        public ImageView icon;
        public TextView sourceName;
    }

    private final class SourceItem {
        public int id;
        public int imgRes;
        public String text;

        public SourceItem(int id, int imgRes, String text) {
            this.id = id;
            this.imgRes = imgRes;
            this.text = text;
        }

    }

}
