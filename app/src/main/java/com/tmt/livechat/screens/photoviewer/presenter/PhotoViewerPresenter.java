package com.tmt.livechat.screens.photoviewer.presenter;

import androidx.annotation.Nullable;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.tmt.livechat.screens.abstraction.mvp.MvpPresenter;
import com.tmt.livechat.screens.photoviewer.PhotoViewerInterface;
import com.tmt.livechat.utils.ImageUtils;

/**
 * Created by mohammednabil on 2019-09-18.
 */
public class PhotoViewerPresenter extends MvpPresenter<PhotoViewerInterface.View> implements PhotoViewerInterface.Presenter{

    private String url;

    @Override
    public void init() {
        view().setupViews();
        url = view().activity().getIntent().getStringExtra("url");
        view().showProgress(true);
    }

    @Override
    public void loadImage(PhotoView image) {
        ImageUtils.displayImageFromUrl(view().activity(), url, image, null,  new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                view().showProgress(false);
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                view().showProgress(false);
                return false;
            }
        });
    }
}
