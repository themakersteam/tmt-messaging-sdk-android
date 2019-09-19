package com.tmt.livechat.screens.sendfile.presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import com.tmt.livechat.screens.abstraction.activity.BaseActivity;
import com.tmt.livechat.screens.abstraction.mvp.MvpPresenter;
import com.tmt.livechat.screens.chat.activity.view.LiveChatActivity;
import com.tmt.livechat.screens.sendfile.SendFileInterface;
import com.tmt.livechat.utils.FileUtils;
import com.tmt.livechat.utils.ImageUtils;
import java.io.File;

/**
 * Created by mohammednabil on 2019-09-18.
 */
public class SendFilePresenter  extends MvpPresenter<SendFileInterface.View> implements SendFileInterface.Presenter{

    private String currentPhotoPath = null;
    private int action = 0;
    private Uri currentBitmap = null;

    @Override
    public void init() {
        view().setupViews();
        if (view().activity().getIntent() != null && view().activity().getIntent().hasExtra("ACTION")) {
            action = view().activity().getIntent().getIntExtra("ACTION", BaseActivity.REQUEST_IMAGE_CAPTURE);
            if (action == BaseActivity.REQUEST_IMAGE_CAPTURE)
                dispatchTakePictureIntent();
            else if (action == BaseActivity.REQUEST_GALLERY_CAPTURE)
                dispatchGalleryIntent();
        }
        else {
            view().finishActivity();
        }
    }

    @Override
    public void sendImage(String captionText) {
        Intent intent = new Intent().setData(currentBitmap);
        if (!captionText.isEmpty())
            intent.putExtra("CAPTION", captionText);
        view().finishActivityWithResult(intent, Activity.RESULT_OK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BaseActivity.REQUEST_IMAGE_CAPTURE && resultCode == view().activity().RESULT_OK) {
            if (currentPhotoPath != null) {
                File file = new File(currentPhotoPath);
                currentBitmap = FileUtils.getImageContentUri(view().activity(), file);
                if (currentBitmap != null) {
                    view().loadImage(currentBitmap);
                }
                else {
                    view().finishActivity();
                }
            }
        }
        else if (requestCode == BaseActivity.REQUEST_GALLERY_CAPTURE && resultCode == view().activity().RESULT_OK) {
            if (data == null) {
                return;
            }
            currentBitmap = data.getData();
            view().loadImage(currentBitmap);
        }
        else {
            view().finishActivity();
        }
    }

    @Override
    public void backPressed() {
        if (action == BaseActivity.REQUEST_GALLERY_CAPTURE)
            dispatchGalleryIntent();
        else
            view().finishActivity();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(view().activity().getPackageManager()) != null) {
            try {
                File photoFile = ImageUtils.createImageFile(view().activity());
                currentPhotoPath = photoFile.getAbsolutePath();
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(view().activity(),
                            LiveChatActivity.PACKAGE_NAME + ".fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    view().startActivity(takePictureIntent, BaseActivity.REQUEST_IMAGE_CAPTURE);
                }
            }
            catch (Exception e) {
                view().toast("Camera Error !");
                view().finishActivity();
            }
        }
    }

    private void dispatchGalleryIntent() {
        view().startActivity(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), BaseActivity.REQUEST_GALLERY_CAPTURE);
    }

}
