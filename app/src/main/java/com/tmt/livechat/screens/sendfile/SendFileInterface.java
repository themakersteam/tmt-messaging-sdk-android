package com.tmt.livechat.screens.sendfile;

import android.content.Intent;
import android.net.Uri;

import com.tmt.livechat.screens.abstraction.mvp.MvpInterface;

/**
 * Created by mohammednabil on 2019-09-18.
 */
public interface SendFileInterface {

    /** Represents the View in MVP. */
    interface View extends MvpInterface.View {
        void loadImage(Uri uri);
    }

    /** Represents the Presenter in MVP. */
    interface Presenter extends MvpInterface.Presenter {
        void sendImage(String caption_text);
        void onActivityResult(int requestCode, int resultCode, Intent data);
        void backPressed();
    }

}
