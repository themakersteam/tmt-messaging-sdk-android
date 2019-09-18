package com.tmt.livechat.screens.sendfile.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import com.tmt.livechat.R;
import com.tmt.livechat.screens.abstraction.mvp.MvpActivity;
import com.tmt.livechat.screens.sendfile.SendFileInterface;
import com.tmt.livechat.screens.sendfile.presenter.SendFilePresenter;

public class SendFileActivity extends MvpActivity implements SendFileInterface.View {

    private SendFileInterface.Presenter presenter = new SendFilePresenter();

    private ImageView mainImage, send;
    private EditText captionText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);
        ((SendFilePresenter)presenter).setView(this);
        presenter.init();
    }

    @Override
    public void setupViews() {
        setupToolbarWithHome((Toolbar) findViewById(R.id.toolbar), null);
        captionText = findViewById(R.id.edittext_caption_message);
        send = findViewById(R.id.img_send_image);
        mainImage = findViewById(R.id.main_image_view);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.sendImage(captionText.getText().toString());
            }
        });
    }

    @Override
    public void loadImage(Uri uri) {
        mainImage.setImageURI(uri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        presenter.backPressed();
    }
}
