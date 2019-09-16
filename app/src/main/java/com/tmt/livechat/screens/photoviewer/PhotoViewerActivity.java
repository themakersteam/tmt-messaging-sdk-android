package com.tmt.livechat.screens.photoviewer;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.tmt.livechat.R;
import com.tmt.livechat.utils.DownloadUtils;
import com.tmt.livechat.utils.ImageUtils;

public class PhotoViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        String url = getIntent().getStringExtra("url");

        PhotoView imageView =  findViewById(R.id.main_image_view);
        final ProgressBar progressBar = findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.VISIBLE);

        ImageUtils.displayImageFromFile(this, DownloadUtils.getFileFromCache(this, url), imageView, new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }
        });
    }
}
