package com.tmt.livechat.screens.chat.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.tmt.livechat.R;
import com.tmt.livechat.chat.ChatInterface;
import com.tmt.livechat.chat.constants.DeliveryReceiptStatus;
import com.tmt.livechat.chat.clients.firestore.FirestoreClient;
import com.tmt.livechat.chat.model.BaseMessage;
import com.tmt.livechat.chat.model.FileMessage;
import com.tmt.livechat.chat.model.LocationMessage;
import com.tmt.livechat.chat.model.TextMessage;
import com.tmt.livechat.app_client.model.Theme;
import com.tmt.livechat.screens.chat.activity.view.LiveChatActivity;
import com.tmt.livechat.screens.chat.adapters.LiveChatAdapter;
import com.tmt.livechat.screens.sendfile.view.SendFileActivity;
import com.tmt.livechat.utils.AudioPlayerUtils;
import com.tmt.livechat.utils.AudioRecorderUtil;
import com.tmt.livechat.utils.FileUtils;
import com.tmt.livechat.utils.LocationUtils;
import java.io.File;
import java.util.Hashtable;


public class LiveChatFragment extends BaseChatFragment implements ChatInterface.Callbacks, LiveChatAdapter.OnItemClickListener {

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
    private LinearLayout groupChatBox;
    private Theme theme;
    private boolean screenVisable = true;

    private boolean showWelcome = true;

    private FirestoreClient chatClient;

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
        chatClient = new FirestoreClient();
        mChatAdapter = new LiveChatAdapter(getActivity(), chatClient);
        setUpChatListAdapter();
        chatClient.onCreate(getContext(), this, getArguments().getString("CHANNEL_ID"));

        // Load messages from cache.
        mChatAdapter.load();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_live_chat, container, false);
        setRetainInstance(true);
        mRecyclerView = rootView.findViewById(R.id.recycler_group_chat);
        mCurrentEventLayout = rootView.findViewById(R.id.layout_group_chat_current_event);
        mCurrentEventText =  rootView.findViewById(R.id.text_group_chat_current_event);
        mMessageEditText = rootView.findViewById(R.id.edittext_group_chat_message);
        mMessageSendButton = rootView.findViewById(R.id.button_group_chat_send);
        mMessageCameraButton = rootView.findViewById(R.id.button_camera_send);
        mEditTextContainer = rootView.findViewById(R.id.editTextContainer);
        mRecordButton = rootView.findViewById(R.id.record_button);
        mRecordView = rootView.findViewById(R.id.record_view);
        groupChatBox = rootView.findViewById(R.id.layout_group_chat_chatbox);
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
                    chatClient.setTypingStatus(false);
                }
                else if (s.length() == 1){
                    chatClient.setTypingStatus(true);
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
                    sendFile(Uri.fromFile(new File(fileName)),"audio", null);
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
    public void onChannelFrozen() {
        if (groupChatBox != null) {
            groupChatBox.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnectionError(int code,final Exception e) {
        Toast.makeText(getContext(), getString(R.string.ms_connection_failed), Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

    @Override
    public void newIncomingMessage(final BaseMessage message) {
        ((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(150);
        if (isVisible() && screenVisable)
            chatClient.updateReadAtTimestamp();
        mChatAdapter.addFirst(message);
    }


    @Override
    public void sendingMessage(final BaseMessage userMessage) {
        mChatAdapter.addFirst(userMessage);
    }

    @Override
    public void onTypingStatusUpdated(final boolean isTyping,final String from) {
        mCurrentEventLayout.setVisibility(isTyping ? View.VISIBLE : View.GONE);
        mCurrentEventText.setText(String.format(getString(R.string.is_typing), from));
    }


    @Override
    public void onUserMessageItemClick(BaseMessage message) {
        if (mChatAdapter.isFailedMessage(message)) {
            retryFailedMessage(message);
            return;
        }
        if (message.isLocationMessage()) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(LocationUtils.getLocationUrlMessageIfExists(message)));
            try {
                startActivity(browserIntent);
            }
            catch (ActivityNotFoundException e){
            }
        }
        else if (message.isFileMessage()) {
            onFileMessageClicked(message);
        }
    }

    @Override
    public void onMessageStatusUpdated(final String id, final String status) {
        mChatAdapter.updateStatus(id, status);
    }

    @Override
    public void lastReadAtUpdated(final long newVal) {
        mChatAdapter.updateStatus(newVal);
    }

    @Override
    void retrySendingMessage(BaseMessage message) {
        if (message.isTextMessage()) {
            String userInput = ((TextMessage)message).getText();
            sendUserMessage(userInput);
        }
        else if (message.isLocationMessage()) {
            chatClient.sendLocationMessage(((LocationMessage)message).getLocation().getLatitude(), ((LocationMessage)message).getLocation().getLongitude());
        }
        else if (message.isFileMessage()) {
            FileMessage fileMessage = (FileMessage)message;
            sendFile(Uri.fromFile(new File(fileMessage.getFile_uri())), fileMessage.getFile_type(), fileMessage.getLabel());
        }
        mChatAdapter.removeFailedMessage(message);
    }


    private void onRecording(boolean on) {
        mMessageCameraButton.setVisibility(on ? View.INVISIBLE : View.VISIBLE);
        mUploadFileButton.setVisibility(on ? View.INVISIBLE : View.VISIBLE);
        mMessageEditText.setVisibility(on ? View.INVISIBLE : View.VISIBLE);
        mEditTextContainer.setBackgroundResource(on ? 0 : R.drawable.send_message_edit_text_design);
    }

    @Override
    public void onResume() {
        super.onResume();
        chatClient.updateReadAtTimestamp();
        screenVisable = true;
        mChatAdapter.setContext(getActivity());
    }

    @Override
    public void onPause() {
        screenVisable = false;
        chatClient.setTypingStatus(false);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        screenVisable = false;
        AudioPlayerUtils.destroy();
        chatClient.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == INTENT_REQUEST_CHOOSE_MEDIA || requestCode == SEND_FILE_ACTIVITY_RESULT) && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            sendFile(data.getData(), "image", data.getStringExtra("CAPTION"));
        }
        else if (requestCode == OPEN_LOCATION_ACTIVITY_RESULT && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("lat") && data.hasExtra("lng")) {
                chatClient.sendLocationMessage(data.getDoubleExtra("lat", 0) , data.getDoubleExtra("lng", 0));
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

    private void setUpChatListAdapter() {
        mChatAdapter.setItemClickListener(this);
    }


    private void sendUserMessage(String text) {
        chatClient.sendUserMessage(text);
    }


    private void sendFile(Uri uri, String type,  final String caption) {

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
            ((LiveChatActivity)getActivity()).addProgress(file.getName(), false);
            chatClient.sendFileMessage(file,type, caption, new ChatInterface.SendFileInterface() {
                @Override
                public void onReady(FileMessage userMessage) {
                    mChatAdapter.addFirst(userMessage);
                    mChatAdapter.setFileProgressPercent(userMessage, 0);
                }

                @Override
                public void onProgress(final FileMessage userMessage,final int parentage) {
                    if (getActivity() != null && isVisible()) {
                        mChatAdapter.setFileProgressPercent(userMessage, parentage);
                    }
                }

                @Override
                public void onSent(final FileMessage message,final Exception e) {
                    ((LiveChatActivity)getActivity()).removeProgress(file.getName());
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
    }

}
