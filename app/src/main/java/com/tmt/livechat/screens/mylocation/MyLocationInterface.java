package com.tmt.livechat.screens.mylocation;

import android.widget.RadioGroup;

import com.tmt.livechat.screens.abstraction.mvp.MvpInterface;

/**
 * Created by mohammednabil on 2019-09-18.
 */
public interface MyLocationInterface {

    /** Represents the View in MVP. */
    interface View extends MvpInterface.View {
        void updateRadio(int map_style, int checked_color, int color);
    }

    /** Represents the Presenter in MVP. */
    interface Presenter extends MvpInterface.Presenter {
        void onCheckedChanged(RadioGroup group, int checkedId);
        void sendLocationClicked(String description, double lat, double lng);
    }

}
