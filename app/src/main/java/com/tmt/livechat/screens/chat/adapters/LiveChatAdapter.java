package com.tmt.livechat.screens.chat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.tmt.livechat.R;
import com.tmt.livechat.chat.ChatInterface;
import com.tmt.livechat.chat.constants.DeliveryReceiptStatus;
import com.tmt.livechat.chat.clients.firestore.FirestoreClient;
import com.tmt.livechat.chat.model.BaseMessage;
import com.tmt.livechat.chat.model.FileMessage;
import com.tmt.livechat.screens.chat.viewbinders.MyAudioMessageHolder;
import com.tmt.livechat.screens.chat.viewbinders.MyFileMessageHolder;
import com.tmt.livechat.screens.chat.viewbinders.MyImageFileMessageHolder;
import com.tmt.livechat.screens.chat.viewbinders.MyUserMessageHolder;
import com.tmt.livechat.screens.chat.viewbinders.OtherAudioMessageHolder;
import com.tmt.livechat.screens.chat.viewbinders.OtherFileMessageHolder;
import com.tmt.livechat.screens.chat.viewbinders.OtherImageFileMessageHolder;
import com.tmt.livechat.screens.chat.viewbinders.OtherUserMessageHolder;
import com.tmt.livechat.utils.LoadingUtils;

import java.util.ArrayList;
import java.util.List;

public class LiveChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER_MESSAGE_ME = 10;
    private static final int VIEW_TYPE_USER_MESSAGE_OTHER = 11;
    private static final int VIEW_TYPE_FILE_MESSAGE_ME = 20;
    private static final int VIEW_TYPE_FILE_MESSAGE_OTHER = 21;
    private static final int VIEW_TYPE_FILE_MESSAGE_IMAGE_ME = 22;
    private static final int VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER = 23;
    private static final int VIEW_TYPE_FILE_MESSAGE_AUDIO_ME = 26;
    private static final int VIEW_TYPE_FILE_MESSAGE_AUDIO_OTHER = 27;

    private final int PAGE_SIZE = 20;

    private Context mContext;

    private List<BaseMessage> mMessageList;

    private OnItemClickListener mItemClickListener;

    private FirestoreClient chatClient;

    private long last_received_read_at = 0;

    private LoadingUtils loadingUtils;

    public interface OnItemClickListener {
        void onUserMessageItemClick(BaseMessage message);
    }


    public LiveChatAdapter(Context context, FirestoreClient chatClient) {
        mContext = context;
        mMessageList = new ArrayList<>();
        loadingUtils = new LoadingUtils(context);
        loadingUtils.showOnScreenLoading();
        this.chatClient = chatClient;
    }

    /**
     * Inflates the correct layout according to the View Type.
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case VIEW_TYPE_USER_MESSAGE_ME:
                View myUserMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_user_me, parent, false);
                return new MyUserMessageHolder(myUserMsgView);
            case VIEW_TYPE_USER_MESSAGE_OTHER:
                View otherUserMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_user_other, parent, false);
                return new OtherUserMessageHolder(otherUserMsgView);
            case VIEW_TYPE_FILE_MESSAGE_ME:
                View myFileMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_file_me, parent, false);
                return new MyFileMessageHolder(myFileMsgView);
            case VIEW_TYPE_FILE_MESSAGE_OTHER:
                View otherFileMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_file_other, parent, false);
                return new OtherFileMessageHolder(otherFileMsgView);
            case VIEW_TYPE_FILE_MESSAGE_IMAGE_ME:
                View myImageFileMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_file_image_me, parent, false);
                return new MyImageFileMessageHolder(myImageFileMsgView);
            case VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER:
                View otherImageFileMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_file_image_other, parent, false);
                return new OtherImageFileMessageHolder(otherImageFileMsgView);
            case VIEW_TYPE_FILE_MESSAGE_AUDIO_OTHER:
                View audioFileMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_audio_other, parent, false);
                return new OtherAudioMessageHolder(audioFileMsgView);
            case VIEW_TYPE_FILE_MESSAGE_AUDIO_ME:
                View audioMyFileMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_audio_me, parent, false);
                return new MyAudioMessageHolder(audioMyFileMsgView);
            default:
                return null;

        }
    }

    /**
     * Binds variables in the SendBirdMessage to UI components in the ViewHolder.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BaseMessage message = mMessageList.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_USER_MESSAGE_ME:
                ((MyUserMessageHolder) holder).bind(mContext, message, mItemClickListener, position);
                break;
            case VIEW_TYPE_USER_MESSAGE_OTHER:
                ((OtherUserMessageHolder) holder).bind(mContext, message, mItemClickListener, position);
                break;
            case VIEW_TYPE_FILE_MESSAGE_ME:
                ((MyFileMessageHolder) holder).bind(mContext, message, mItemClickListener);
                break;
            case VIEW_TYPE_FILE_MESSAGE_OTHER:
                ((OtherFileMessageHolder) holder).bind(message, mItemClickListener);
                break;
            case VIEW_TYPE_FILE_MESSAGE_IMAGE_ME:
                ((MyImageFileMessageHolder) holder).bind(mContext, message, mItemClickListener);
                break;
            case VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER:
                ((OtherImageFileMessageHolder) holder).bind(mContext, message, mItemClickListener);
                break;
            case VIEW_TYPE_FILE_MESSAGE_AUDIO_ME:
                ((MyAudioMessageHolder) holder).bind(mContext, message);
                break;
            case VIEW_TYPE_FILE_MESSAGE_AUDIO_OTHER:
                ((OtherAudioMessageHolder) holder).bind(mContext, message);
                break;
            default:
                break;
        }
    }

    /**
     * Declares the View Type according to the type of message and the sender.
     */
    @Override
    public int getItemViewType(int position) {

        BaseMessage message = mMessageList.get(position);

        if (!message.isFileMessage()) {
            if (chatClient.isMyMessage(message.getSender()))
                return VIEW_TYPE_USER_MESSAGE_ME;
            else
                return VIEW_TYPE_USER_MESSAGE_OTHER;
        }
        else {
            FileMessage fileMessage = (FileMessage) message;
            if (fileMessage.getFile_type().toLowerCase().contains("image")) {
                if (chatClient.isMyMessage(message.getSender())) {
                    return VIEW_TYPE_FILE_MESSAGE_IMAGE_ME;
                } else {
                    return VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER;
                }
            }
            else if (fileMessage.getFile_type().toLowerCase().contains("audio") || fileMessage.getFile_type().toLowerCase().contains("3gpp")) {
                if (chatClient.isMyMessage(message.getSender())) {
                    return VIEW_TYPE_FILE_MESSAGE_AUDIO_ME;
                } else {
                    return VIEW_TYPE_FILE_MESSAGE_AUDIO_OTHER;
                }
            }
            else {
                if (chatClient.isMyMessage(message.getSender())) {
                    return VIEW_TYPE_FILE_MESSAGE_ME;
                } else {
                    return VIEW_TYPE_FILE_MESSAGE_OTHER;
                }
            }
        }
    }


    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public void load() {
        try {
            mMessageList.clear();
            chatClient.loadPrevMessages(PAGE_SIZE,last_received_read_at, new ChatInterface.LoadPrevInterface() {
                @Override
                public void onReady(List<BaseMessage> baseMessages) {
                    mMessageList.addAll(baseMessages);
                    chatClient.updateReadAtTimestamp();
                    notifyDataSetChanged();
                    loadingUtils.hideOnScreenLoading();
                }
            });
        } catch(Exception e) {
        }
    }

    public void updateStatus(String id, String status) {
        for (BaseMessage u : mMessageList) {
            if (u.getId() != null && u.getId().equals(id)) {
                u.setStatus(status);
            }
        }
        notifyDataSetChanged();
    }

    public void updateStatus(long last_readAt) {
        this.last_received_read_at = last_readAt;
        for (BaseMessage u : mMessageList) {
            if (u.getStatus() != null && u.getStatus().equals(DeliveryReceiptStatus.RECEIVED)) {
                u.setStatus(u.getPosted_at().toDate().getTime() <= last_readAt ? DeliveryReceiptStatus.SEEN : DeliveryReceiptStatus.RECEIVED);
            }
        }
        notifyDataSetChanged();
    }


    public void loadPrev() {
        try {
            chatClient.loadPrevMessages(PAGE_SIZE, last_received_read_at, new ChatInterface.LoadPrevInterface() {
                @Override
                public void onReady(List<BaseMessage> baseMessages) {
                    mMessageList.addAll(baseMessages);
                    notifyDataSetChanged();
                }
            });
        } catch(Exception e) {
        }
    }


    public boolean isFailedMessage(BaseMessage message) {
        return message.getStatus() != null && message.getStatus().equals(DeliveryReceiptStatus.FAILED);
    }


    public void removeFailedMessage(BaseMessage message) {
        removeMessageFromArrayList(message);
        notifyDataSetChanged();
    }

    private boolean removeMessageFromArrayList(BaseMessage baseMessage) {
        for (BaseMessage userMessage : mMessageList) {
            if (userMessage != null && userMessage.equals(baseMessage)) {
                return mMessageList.remove(userMessage);
            }
        }
        return false;
    }

    public void setFileProgressPercent(FileMessage message, int percent) {
        for (BaseMessage userMessage : mMessageList) {
            if (message.getId().equals(userMessage.getId())) {
                ((FileMessage)userMessage).getFile().setProgress(percent);
            }
        }
        notifyDataSetChanged();
    }

    public void addFirst(BaseMessage message) {
        mMessageList.add(0, message);
        notifyDataSetChanged();
    }

    public void setItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }
}
