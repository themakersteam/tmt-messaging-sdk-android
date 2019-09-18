package com.tmt.livechat.screens.mylocation.presenter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RadioGroup;

import com.google.android.gms.maps.GoogleMap;
import com.tmt.livechat.R;
import com.tmt.livechat.screens.abstraction.mvp.MvpPresenter;
import com.tmt.livechat.screens.mylocation.MyLocationInterface;

/**
 * Created by mohammednabil on 2019-09-18.
 */
public class MyLocationPresenter extends MvpPresenter<MyLocationInterface.View> implements MyLocationInterface.Presenter{

    @Override
    public void init() {
        view().setupViews();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId == R.id.rb_map) {
            view().updateRadio(GoogleMap.MAP_TYPE_NORMAL, Color.WHITE, Color.BLACK);
        }
        else {
            view().updateRadio(GoogleMap.MAP_TYPE_SATELLITE, Color.BLACK, Color.WHITE);
        }
    }

    @Override
    public void sendLocationClicked(String description, double lat, double lng) {
        try {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("geoDesc", description);
            resultIntent.putExtra("lat", lat);
            resultIntent.putExtra("lng", lng);
            view().finishActivityWithResult(resultIntent, Activity.RESULT_OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
