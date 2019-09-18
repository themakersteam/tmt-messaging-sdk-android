package com.tmt.livechat.screens.photoviewer.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import androidx.annotation.Nullable;
import com.github.chrisbanes.photoview.PhotoView;
import com.tmt.livechat.R;
import com.tmt.livechat.screens.abstraction.mvp.MvpActivity;
import com.tmt.livechat.screens.photoviewer.PhotoViewerInterface;
import com.tmt.livechat.screens.photoviewer.presenter.PhotoViewerPresenter;

public class PhotoViewerActivity extends MvpActivity implements PhotoViewerInterface.View {

    private PhotoViewerInterface.Presenter presenter = new PhotoViewerPresenter();

    private PhotoView imageView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);
        ((PhotoViewerPresenter)presenter).setView(this);
        presenter.init();
        presenter.loadImage(imageView);
    }

    @Override
    public void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setupViews() {
        imageView =  findViewById(R.id.main_image_view);
        progressBar = findViewById(R.id.progress_bar);
    }
}
