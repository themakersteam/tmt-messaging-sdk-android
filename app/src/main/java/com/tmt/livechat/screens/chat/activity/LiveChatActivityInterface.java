package com.tmt.livechat.screens.chat.activity;

import android.app.AlertDialog;

import androidx.fragment.app.Fragment;

import com.tmt.livechat.screens.abstraction.mvp.MvpInterface;

/**
 * Created by mohammednabil on 2019-09-19.
 */
public interface LiveChatActivityInterface {

    /** Represents the View in MVP. */
    interface View extends MvpInterface.View {
        void setupToolbar(String title, String subtitle);
        void onFragment(Fragment fragment);
        void showInProgressMessageAlert(AlertDialog.Builder dialog);
        void addProgress(String name, boolean val);
        void removeProgress(String name);
        void close();
    }

    /** Represents the Presenter in MVP. */
    interface Presenter extends MvpInterface.Presenter {
        void addProgress(String name, boolean val);
        void removeProgress(String name);
        void onBackPressed();
    }

}
