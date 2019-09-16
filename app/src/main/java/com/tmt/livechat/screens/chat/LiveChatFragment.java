package com.tmt.livechat.screens.chat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.google.android.material.snackbar.Snackbar;
import com.tmt.livechat.R;
import com.tmt.livechat.connection_xmpp.XmppClient;
import com.tmt.livechat.connection_xmpp.constants.DeliveryReceiptStatus;
import com.tmt.livechat.connection_xmpp.interfaces.SendFileInterface;
import com.tmt.livechat.connection_xmpp.interfaces.XmppChatCallbacks;
import com.tmt.livechat.model.Theme;
import com.tmt.livechat.model.UserMessage;
import com.tmt.livechat.screens.chat.adapters.FileOptionsBottomSheetAdapter;
import com.tmt.livechat.screens.chat.adapters.LiveChatAdapter;
import com.tmt.livechat.screens.mylocation.MyLocationActivity;
import com.tmt.livechat.screens.photoviewer.PhotoViewerActivity;
import com.tmt.livechat.screens.sendfile.SendFileActivity;
import com.tmt.livechat.utils.AudioPlayerUtils;
import com.tmt.livechat.utils.AudioRecorderUtil;
import com.tmt.livechat.utils.DownloadUtils;
import com.tmt.livechat.utils.FileUtils;
import com.tmt.livechat.utils.LoadingUtils;
import com.tmt.livechat.utils.TextUtils;
import org.jivesoftware.smackx.muc.MultiUserChat;
import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;


public class LiveChatFragment extends Fragment implements XmppChatCallbacks, LiveChatAdapter.OnItemClickListener {

    private static final int INTENT_REQUEST_CHOOSE_MEDIA = 301;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 13;
    private static final int PERMISSION_CAMERA = 14;
    private static final int PERMISSION_RECORD_AUDIO = 16;
    private static final int PERMISSION_CALL = 15;
    private static final int SEND_FILE_ACTIVITY_RESULT = 101;
    private static final int OPEN_LOCATION_ACTIVITY_RESULT = 102;

    private InputMethodManager mIMM;
    private HashMap<String, Boolean> inProgressMessages;

    private RelativeLayout mRootLayout;
    private FileOptionsBottomSheetAdapter mBottomSheetAdapter;
    private RecyclerView mRecyclerView;
    private LiveChatAdapter mChatAdapter;
    private LinearLayoutManager mLayoutManager;
    private EditText mMessageEditText;
    private ImageView mMessageSendButton;
    private ImageView mMessageCameraButton;
    private RecordButton mRecordButton;
    private RecordView mRecordView;
    private LinearLayout mEditTextContainer;
    private RelativeLayout mUploadFileButton;
    private View mCurrentEventLayout;
    private TextView mCurrentEventText;
    private TextView welcomeMessage;
    private Theme theme;
    private String chatId;
    private boolean screenVisable = true;

    private boolean showWelcome = true;

    private LoadingUtils loadingUtils;
    private XmppClient xmppClient;


    /**
     * To create an instance of this fragment
     */
    public static LiveChatFragment newInstance() {
        LiveChatFragment fragment = new LiveChatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatId = getArguments().getString("URL");
        xmppClient = new XmppClient();
        mChatAdapter = new LiveChatAdapter(getActivity(), xmppClient);
        setUpChatListAdapter();
        xmppClient.onCreate(getContext(), this, chatId);
        loadingUtils = new LoadingUtils(getActivity());

        mIMM = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inProgressMessages = new HashMap<>();


        // Load messages from cache.
        mChatAdapter.load();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_live_chat, container, false);
        setRetainInstance(true);

        mRootLayout = rootView.findViewById(R.id.layout_group_chat_root);
        mRecyclerView = rootView.findViewById(R.id.recycler_group_chat);
        mCurrentEventLayout = rootView.findViewById(R.id.layout_group_chat_current_event);
        mCurrentEventText =  rootView.findViewById(R.id.text_group_chat_current_event);
        mMessageEditText = rootView.findViewById(R.id.edittext_group_chat_message);
        mMessageSendButton = rootView.findViewById(R.id.button_group_chat_send);
        mMessageCameraButton = rootView.findViewById(R.id.button_camera_send);
        mEditTextContainer = rootView.findViewById(R.id.editTextContainer);
        mRecordButton = rootView.findViewById(R.id.record_button);
        mRecordView = rootView.findViewById(R.id.record_view);
        mRecordButton.setRecordView(mRecordView);
        mUploadFileButton = rootView.findViewById(R.id.button_group_chat_upload);
        welcomeMessage = rootView.findViewById(R.id.text_group_chat_welcome);

        if (getArguments() != null && getArguments().containsKey("THEME")) {
            theme = getArguments().getParcelable("THEME");
        }
        if (theme != null && theme.getWelcomeMessage() != null) {
            welcomeMessage.setText(theme.getWelcomeMessage());
        }
        else {
            welcomeMessage.setVisibility(View.GONE);
            showWelcome = false;
        }

        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    xmppClient.setTypingStatus(false);
                }
                else if (s.length() == 1){
                    xmppClient.setTypingStatus(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                mMessageSendButton.setVisibility(s.toString().replaceAll(" ", "").replaceAll("\n", "").length() > 0 ? View.VISIBLE : View.GONE);
                mMessageCameraButton.setVisibility(s.toString().replaceAll(" ", "").replaceAll("\n", "").length() > 0 ? View.GONE : View.VISIBLE);
                mRecordButton.setVisibility(s.toString().replaceAll(" ", "").replaceAll("\n", "").length() > 0 ? View.GONE : View.VISIBLE);
            }
        });

        mMessageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = mMessageEditText.getText().toString();
                if (userInput.length() > 0) {
                    sendUserMessage(userInput);
                    mMessageEditText.setText("");
                }
            }
        });

        mMessageCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestMedia(SendFileActivity.REQUEST_IMAGE_CAPTURE);
            }
        });

        mUploadFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileOptions();
            }
        });

        mRecordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                onRecording(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestAudioRecord();
                    }
                }, 500);
            }

            @Override
            public void onCancel() {
                AudioRecorderUtil.instance().cancelRecording();
            }

            @Override
            public void onFinish(long recordTime) {
                onRecording(false);
                String fileName = AudioRecorderUtil.instance().stopRecording();
                if (fileName != null) {
                    sendFile(Uri.fromFile(new File(fileName)), null);
                }
            }

            @Override
            public void onLessThanSecond() {
                onRecording(false);
                AudioRecorderUtil.instance().cancelRecording();
            }
        });


        mRecordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                onRecording(false);
            }
        });

        setUpRecyclerView();
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onChatReady(MultiUserChat chat) {
    }

    @Override
    public void onConnectionError(int code, Exception e) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), getString(R.string.ms_connection_failed), Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
    }

    @Override
    public void reconnecting() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingUtils.showOnScreenLoading();
            }
        });
    }

    @Override
    public void reconnected() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingUtils.hideOnScreenLoading();
            }
        });
    }

    @Override
    public void newIncomingMessage(final UserMessage message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(150);
                if (isVisible() && screenVisable)
                    xmppClient.seenTheMessages();
                mChatAdapter.addFirst(message);
            }
        });
    }

    @Override
    public void sendingMessage(final UserMessage userMessage) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChatAdapter.addFirst(userMessage);
            }
        });
    }


    @Override
    public void onTypingStatusUpdated(final boolean isTyping,final String from) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCurrentEventLayout.setVisibility(isTyping ? View.VISIBLE : View.GONE);
                mCurrentEventText.setText(String.format(getString(R.string.is_typing), from));
            }
        });
    }


    @Override
    public void onUserMessageItemClick(UserMessage message) {
        if (mChatAdapter.isFailedMessage(message)) {
            retryFailedMessage(message);
            return;
        }
        if (message.isLocation()) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(TextUtils.getLocationUrlMessageIfExists(message)));
            try {
                startActivity(browserIntent);
            }
            catch (ActivityNotFoundException e){
            }
        }
        else if (message.isFile()) {
            onFileMessageClicked(message);
        }
    }

    @Override
    public void onMessageStatusUpdated(final String id, final String status) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChatAdapter.updateStatus(id, status);
            }
        });
    }

    @Override
    public void seenStampReceived(final String room_id) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChatAdapter.updateStatus(room_id);
            }
        });
    }

    private void onRecording(boolean on) {
        mMessageCameraButton.setVisibility(on ? View.INVISIBLE : View.VISIBLE);
        mUploadFileButton.setVisibility(on ? View.INVISIBLE : View.VISIBLE);
        mMessageEditText.setVisibility(on ? View.INVISIBLE : View.VISIBLE);
        mEditTextContainer.setBackgroundResource(on ? 0 : R.drawable.send_message_edit_text_design);
    }

    private void openSendFileScreen(int action) {
        startActivityForResult(new Intent(getActivity(), SendFileActivity.class).putExtra("ACTION", action), SEND_FILE_ACTIVITY_RESULT);
    }

    @Override
    public void onResume() {
        super.onResume();
        xmppClient.seenTheMessages();
        screenVisable = true;
        mChatAdapter.setContext(getActivity());
    }

    @Override
    public void onPause() {
        screenVisable = false;
        xmppClient.setTypingStatus(false);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        screenVisable = false;
        AudioPlayerUtils.destroy();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == INTENT_REQUEST_CHOOSE_MEDIA || requestCode == SEND_FILE_ACTIVITY_RESULT) && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            sendFile(data.getData(), data.getStringExtra("CAPTION"));
        }
        else if (requestCode == OPEN_LOCATION_ACTIVITY_RESULT && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("lat") && data.hasExtra("lng")) {
                xmppClient.sendLocationMessage(data.getDoubleExtra("lat", 0) + "", data.getDoubleExtra("lng", 0) + "");
            }
        }
    }

    private void setUpRecyclerView() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mChatAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mChatAdapter.getItemCount() >= 10 && mLayoutManager.findFirstVisibleItemPosition() == 0 && showWelcome) {
                    welcomeMessage.setVisibility(View.GONE);
                }
                else if (showWelcome){
                    welcomeMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (mLayoutManager.findLastVisibleItemPosition() == mChatAdapter.getItemCount() - 1) {
                    mChatAdapter.loadPrev();
                }
            }
        });
    }

    private void showFileOptions() {
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

    private void setUpChatListAdapter() {
        mChatAdapter.setItemClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((LiveChatActivity)context).setOnBackPressedListener(new LiveChatActivity.onBackPressedListener() {
            @Override
            public boolean onBack() {
                if (inProgressMessages != null && inProgressMessages.size() > 0) {
                    showInProgressMessageAlert();
                    return true;
                }
                else {
                    mIMM.hideSoftInputFromWindow(mMessageEditText.getWindowToken(), 0);
                    return false;
                }
            }
        });
    }

    private void retryFailedMessage(final UserMessage message) {
        new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.retry))
                .setPositiveButton(R.string.resend_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            if (!message.isFile()) {
                                String userInput = message.getMessage().getBody();
                                sendUserMessage(userInput);
                            }
                            else if (message.isFile()) {
                                sendFile(Uri.fromFile(DownloadUtils.getFileFromCache(getContext(), message.getFile().getId())), null);
                            }
                            mChatAdapter.removeFailedMessage(message);
                        }
                    }
                }).show();
    }


    private void requestMedia(int request) {
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

    private void requestAudioRecord() {
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

    private void requestStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(mRootLayout, getString(R.string.storage_access_permission_needed),
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


    private void requestCameraPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
            Snackbar.make(mRootLayout, getString(R.string.camera_access_permission_needed),
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

    private void requestAudioRecordPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.RECORD_AUDIO)) {
            Snackbar.make(mRootLayout, getString(R.string.audio_access_permission_needed),
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

    private void onFileMessageClicked(UserMessage message) {
        String type = message.getFile().getContentType().toLowerCase();
        if (type.startsWith("image")) {
            Intent i = new Intent(getActivity(), PhotoViewerActivity.class);
            i.putExtra("url", message.getFile().getId());
            i.putExtra("type", message.getFile().getContentType());
            startActivity(i);
        }
    }

    private void sendUserMessage(String text) {
        xmppClient.sendUserMessage(text);
    }



    private void sendFile(Uri uri, final String caption) {

        Hashtable<String, Object> info = FileUtils.getFileInfo(getActivity(), uri);
        if (info == null) {
            Toast.makeText(getActivity(), getString(R.string.extracting_file_information_failed), Toast.LENGTH_LONG).show();
            return;
        }

        final String path = (String) info.get("path");
        final File file = new File(path);

        if (path.equals("")) {
            Toast.makeText(getActivity(), getString(R.string.file_must_be_in_local_storage), Toast.LENGTH_LONG).show();
        } else {
            inProgressMessages.put(file.getName(), false);
            xmppClient.sendFileMessage(getContext(), file, caption,  new SendFileInterface() {
                @Override
                public void onReady(final UserMessage userMessage) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mChatAdapter.addFirst(userMessage);
                            mChatAdapter.setFileProgressPercent(userMessage, 0);
                        }
                    });
                }

                @Override
                public void onProgress(final UserMessage userMessage,final int parentage) {
                    if (getActivity() != null && isVisible()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mChatAdapter.setFileProgressPercent(userMessage, parentage);
                            }
                        });
                    }
                }

                @Override
                public void onSent(final UserMessage message,final Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                inProgressMessages.remove(file.getName());
                            }
                            catch (Exception activity_is_killed) {}
                            if (getActivity() != null && isVisible()) {
                                mChatAdapter.setFileProgressPercent(message, 100);
                                if (e != null) {
                                    message.setStatus(DeliveryReceiptStatus.FAILED);
                                    return;
                                }
                                mChatAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            });
        }
    }

    private void showInProgressMessageAlert() {
        try {
            new AlertDialog.Builder(getContext()).setMessage(getString(R.string.ms_message_file_in_progress))
                    .setNegativeButton(getString(R.string.ms_message_failed_no), null)
                    .setPositiveButton(getString(R.string.ms_message_failed_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (inProgressMessages != null) {
                                inProgressMessages.clear();
                            }
                            getActivity().onBackPressed();
                        }
                    })
                    .create().show();
        }
        catch (Exception e) {}
    }

    private void showSettingsPermissionSnack(String message) {
        Snackbar.make(mRootLayout, message, Snackbar.LENGTH_LONG).setAction(getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                getActivity().startActivity(new Intent().setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(uri));
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
