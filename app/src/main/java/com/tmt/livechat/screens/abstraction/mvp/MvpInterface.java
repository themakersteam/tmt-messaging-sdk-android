package com.tmt.livechat.screens.abstraction.mvp;


import android.app.Activity;
import android.content.Intent;

/**
 * Created by mohammednabil on 2019-06-26.
 */
public interface MvpInterface {

    /** Represents the View in MVP. */
    interface View {
        Activity activity();
        void setupViews();
        void finishActivity();
        void finishActivityWithResult(Intent intent, int result);
        void startActivity(Intent intent, Integer requestCode);
        void toast(String message);
    }

    /** Represents the Presenter in MVP. */
    interface Presenter {
        void init();
    }

}
