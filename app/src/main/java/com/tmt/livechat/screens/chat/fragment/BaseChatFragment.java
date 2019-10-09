package com.tmt.livechat.screens.chat.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.snackbar.Snackbar;
import com.tmt.livechat.R;
import com.tmt.livechat.chat.model.BaseMessage;
import com.tmt.livechat.chat.model.FileMessage;
import com.tmt.livechat.screens.chat.adapters.FileOptionsBottomSheetAdapter;
import com.tmt.livechat.screens.mylocation.view.MyLocationActivity;
import com.tmt.livechat.screens.photoviewer.view.PhotoViewerActivity;
import com.tmt.livechat.screens.sendfile.view.SendFileActivity;
import com.tmt.livechat.utils.AudioPlayerUtils;
import com.tmt.livechat.utils.AudioRecorderUtil;
import com.tmt.livechat.utils.LoadingUtils;

/**
 * Created by mohammednabil on 2019-10-08.
 */
public abstract class BaseChatFragment extends Fragment {


    protected final int INTENT_REQUEST_CHOOSE_MEDIA = 301;
    protected final int PERMISSION_WRITE_EXTERNAL_STORAGE = 13;
    protected final int PERMISSION_CAMERA = 14;
    protected final int PERMISSION_RECORD_AUDIO = 16;
    protected final int PERMISSION_CALL = 15;
    protected final int SEND_FILE_ACTIVITY_RESULT = 101;
    protected final int OPEN_LOCATION_ACTIVITY_RESULT = 102;


    private FileOptionsBottomSheetAdapter mBottomSheetAdapter;

    abstract void retrySendingMessage(BaseMessage baseMessage);


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void showSettingsPermissionSnack(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).setAction(getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                getActivity().startActivity(new Intent().setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(uri));
            }
        }).show();
    }

    protected void requestMedia(int request) {
        if (request == SendFileActivity.REQUEST_GALLERY_CAPTURE && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermissions();
        }
        else if (request == SendFileActivity.REQUEST_IMAGE_CAPTURE &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermissions();
        }
        else {
            openSendFileScreen(request);
        }
    }

    protected void requestAudioRecord() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermissions();
        }
        else if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestAudioRecordPermissions();
        }
        else {
            AudioPlayerUtils.destroy();
            AudioRecorderUtil.instance().startRecording(getContext());
        }
    }

    protected void requestStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(getView(), getString(R.string.storage_access_permission_needed),
                    Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    PERMISSION_WRITE_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_WRITE_EXTERNAL_STORAGE);
        }
    }


    protected void requestCameraPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
            Snackbar.make(getView(), getString(R.string.camera_access_permission_needed),
                    Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    PERMISSION_CAMERA);
                        }
                    })
                    .show();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_CAMERA);
        }
    }

    protected void requestAudioRecordPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.RECORD_AUDIO)) {
            Snackbar.make(getView(), getString(R.string.audio_access_permission_needed),
                    Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    PERMISSION_RECORD_AUDIO);
                        }
                    })
                    .show();
        }
        else {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_RECORD_AUDIO);
        }
    }


    protected void openSendFileScreen(int action) {
        startActivityForResult(new Intent(getActivity(), SendFileActivity.class).putExtra("ACTION", action), SEND_FILE_ACTIVITY_RESULT);
    }


    protected void onFileMessageClicked(BaseMessage message) {
        FileMessage fileMessage = (FileMessage) message;
        if (fileMessage.getFile_type().startsWith("image")) {
            Intent i = new Intent(getActivity(), PhotoViewerActivity.class);
            i.putExtra("url", fileMessage.getUrl());
            i.putExtra("type", fileMessage.getType());
            startActivity(i);
        }
    }

    protected void showFileOptions() {
        if(mBottomSheetAdapter != null && mBottomSheetAdapter.isAdded()) {
            mBottomSheetAdapter.dismiss();
        }
        else {
            mBottomSheetAdapter = new FileOptionsBottomSheetAdapter().newInstance();
            mBottomSheetAdapter.setListener(new FileOptionsBottomSheetAdapter.ButtonClickListener() {
                @Override
                public void onCameraClicked() {
                    mBottomSheetAdapter.dismiss();
                    requestMedia(SendFileActivity.REQUEST_IMAGE_CAPTURE);
                }

                @Override
                public void onGalleryClicked() {
                    mBottomSheetAdapter.dismiss();
                    requestMedia(SendFileActivity.REQUEST_GALLERY_CAPTURE);
                }

                @Override
                public void onLocationClicked() {
                    startActivityForResult(new Intent(getActivity(), MyLocationActivity.class), OPEN_LOCATION_ACTIVITY_RESULT);
                    mBottomSheetAdapter.dismiss();
                }
            });
            mBottomSheetAdapter.show(getActivity().getSupportFragmentManager(), mBottomSheetAdapter.getTag());
        }
    }

    protected void retryFailedMessage(final BaseMessage message) {
        new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.retry))
                .setPositiveButton(R.string.resend_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            retrySendingMessage(message);
                        }
                    }
                }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED && requestCode == PERMISSION_WRITE_EXTERNAL_STORAGE) {
            showSettingsPermissionSnack(getString(R.string.storage_access_permission_needed));
        }
        else if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED && requestCode == PERMISSION_CAMERA) {
            showSettingsPermissionSnack(getString(R.string.camera_access_permission_needed));
        }
        else if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED && requestCode == PERMISSION_RECORD_AUDIO) {
            showSettingsPermissionSnack(getString(R.string.audio_access_permission_needed));
        }
        else if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED && requestCode == PERMISSION_CALL) {
            showSettingsPermissionSnack(getString(R.string.ms_phone_access_permission_needed));
        }
    }

}
