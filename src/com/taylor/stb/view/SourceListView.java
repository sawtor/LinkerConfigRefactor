package com.taylor.stb.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class SourceListView extends ListView {

	public SourceListView(Context context) {
		super(context);
	}
	
	public SourceListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public SourceListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isInTouchMode() {
		return false;
	}

	@Override
	public void onTouchModeChanged(boolean isInTouchMode) {
	}
	
}
