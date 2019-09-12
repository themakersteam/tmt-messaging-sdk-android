package com.tmt.messagecenter.screens.sendfile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.tmt.messagecenter.R;
import com.tmt.messagecenter.screens.chat.MessageCenterChatActivity;
import com.tmt.messagecenter.utils.FileUtils;
import com.tmt.messagecenter.utils.ImageUtils;

import java.io.File;
import java.io.IOException;

public class SendFileActivity extends AppCompatActivity {

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_GALLERY_CAPTURE = 2;

    private Uri currentBitmap = null;
    private String currentPhotoPath = null;
    private Toolbar toolbar;
    private ImageView mainImage;
    private EditText captionText;
    private ImageView send;
    private int action = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);
        init();
        if (getIntent() != null && getIntent().hasExtra("ACTION")) {
            action = getIntent().getIntExtra("ACTION", REQUEST_IMAGE_CAPTURE);
            if (action == REQUEST_IMAGE_CAPTURE) {
                dispatchTakePictureIntent();
            }
            else if (action == REQUEST_GALLERY_CAPTURE) {
                dispatchGalleryIntent();
            }
        }
        else {
            finish();
        }
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        captionText = findViewById(R.id.edittext_caption_message);
        send = findViewById(R.id.img_send_image);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendImage();
            }
        });
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void sendImage() {
        Intent intent = new Intent();
        intent.setData(currentBitmap);
        if (captionText.getText().toString().replaceAll("\n", "").replaceAll(" ", "").length() > 0) {
            intent.putExtra("CAPTION", captionText.getText().toString());
        }
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = ImageUtils.createImageFile(this);
                currentPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                try {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            MessageCenterChatActivity.PACKAGE_NAME + ".fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
                catch (RuntimeException e) {
                    Toast.makeText(this, "Camera Error ! ", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            else {
                Toast.makeText(this, "Camera Error ! ", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void dispatchGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GALLERY_CAPTURE);
    }

    private void loadImage() {
        if (mainImage == null) {
            mainImage = findViewById(R.id.main_image_view);
        }
        mainImage.setImageURI(currentBitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (currentPhotoPath != null) {
                File file = new File(currentPhotoPath);
                currentBitmap = FileUtils.getImageContentUri(this, file);
                if (currentBitmap != null) {
                    loadImage();
                }
                else {
                    finish();
                }
            }
        }
        else if (requestCode == REQUEST_GALLERY_CAPTURE && resultCode == RESULT_OK) {
            if (data == null) {
                return;
            }
            currentBitmap = data.getData();
            loadImage();
        }
        else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (action == REQUEST_GALLERY_CAPTURE) {
            dispatchGalleryIntent();
        }
        else {
            super.onBackPressed();
        }
    }
}
