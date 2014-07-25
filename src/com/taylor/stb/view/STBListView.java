package com.taylor.stb.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import com.taylor.stb.Constant;

@SuppressLint("NewApi")
public class STBListView extends ListView {
    protected static final String TAG = Constant.TAG;
    private int selectId = 0;

    public int getSelectId() {
        return selectId;
    }

    public void setSelectId(int selectId) {
        this.selectId = selectId;
    }

    public STBListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initHover();
    }

    public STBListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHover();
    }

    public STBListView(Context context) {
        super(context);
        initHover();
    }

    @Override
    public boolean isInTouchMode() {
        return false;
    }

    private void initHover() {
        this.setOnHoverListener(new OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                        if (getSelectedView() != null) {
                            getSelectedView().setSelected(true);
                        }
                        if (!STBListView.this.isFocused()) {
                            STBListView.this.requestFocus();
                        }
                        break;
                    case MotionEvent.ACTION_HOVER_MOVE:
                        if (!STBListView.this.isFocused()) {
                            STBListView.this.requestFocus();
                        }
                        setSelectionFromHover(event);
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                        if (getSelectedView() != null) {
                            getSelectedView().setSelected(false);
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    protected void setSelectionFromHover(MotionEvent event) {
        int nextP = this.pointToPosition((int) event.getX(), (int) event.getY());
        if (nextP - getFirstVisiblePosition() < 0 || nextP - getFirstVisiblePosition() >= getChildCount()) {
            return;
        }
        int distance = (int) (this.getChildAt(nextP - getFirstVisiblePosition()).getY());
        if (nextP == getSelectedItemPosition()) {
            return;
        }
        setSelectionFromTop(nextP, distance);

    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        // disable mouse scroll
        if (event.getSource() == InputDevice.SOURCE_MOUSE) {
            return true;
        }
        return super.onGenericMotionEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // disable touch scroll
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            return true;
        }
        return super.onTouchEvent(ev);
    }

}
