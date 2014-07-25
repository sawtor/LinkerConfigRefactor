package com.taylor.stb.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;
import com.taylor.stb.R;
import com.taylor.stb.STBDataManager;
import com.tvezu.urc.restclient.UrcObject;

import java.awt.font.TextAttribute;
import java.lang.annotation.Target;

/**
 * Created by Taylor on 14-7-22.
 * Use this button to show spinner content.
 */
public class STBButton extends Button {

    public UrcObject content ;

    public boolean hasContent = false;

    private STBButton nextButton ;

    public STBDataManager.ContentType contentType = null ;

    public STBButton(Context context) {
        super(context);
        setContent(null);
    }

    public STBButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContent(null);
    }

    public STBButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setContent(null);
    }

    public void setContent(UrcObject object){
        if (object == null){
            Log.d("Button","Content is null ");
            setText(R.string.please_selsct);
            content = null;
            hasContent = false;
        }else {
            content = object;
            setText(content.getName());
            hasContent = true ;
        }
        setNextButtonState(hasContent);
    }

    public UrcObject getContent(){
        return content;
    }

    public void setContentType(STBDataManager.ContentType type){
        this.contentType = type ;
    }

    public STBDataManager.ContentType getContentType (){
        return this.contentType;
    }
    public void setNextButton(STBButton button){
        nextButton = button ;
    }

    private void setNextButtonState(boolean state){
        if (nextButton == null ) return;
        nextButton.setEnabled(state);
        nextButton.setContent(null);

    }

}
