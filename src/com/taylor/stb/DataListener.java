package com.taylor.stb;

import com.tvezu.urc.restclient.UrcObject;

import java.util.List;

/**
 * Created by Taylor on 14-7-22.
 * Data manager and Spinner callback interface
 */
public interface DataListener {
    /**
     * Data manager call this method when it load data done.
     * e.g. Province .
     * @param list
     */
    public void onQuerySucceed(List<? extends UrcObject> list);
}
