package com.tmt.livechat.screens.photoviewer;

import com.github.chrisbanes.photoview.PhotoView;
import com.tmt.livechat.screens.abstraction.mvp.MvpInterface;

/**
 * Created by mohammednabil on 2019-09-18.
 */
public interface PhotoViewerInterface {

    /** Represents the View in MVP. */
    interface View extends MvpInterface.View {
        void showProgress(boolean show);
    }

    /** Represents the Presenter in MVP. */
    interface Presenter extends MvpInterface.Presenter {
        void loadImage(PhotoView image);
    }

}
