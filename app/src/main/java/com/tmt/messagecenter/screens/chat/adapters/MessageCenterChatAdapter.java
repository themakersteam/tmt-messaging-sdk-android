package com.tmt.messagecenter.screens.chat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.tmt.messagecenter.R;
import com.tmt.messagecenter.connection_xmpp.XmppClient;
import com.tmt.messagecenter.connection_xmpp.constants.DeliveryReceiptStatus;
import com.tmt.messagecenter.connection_xmpp.extensions.FileExtension;
import com.tmt.messagecenter.connection_xmpp.storage.ReceiptStorage;
import com.tmt.messagecenter.model.UserMessage;
import com.tmt.messagecenter.screens.chat.viewbinders.MyAudioMessageHolder;
import com.tmt.messagecenter.screens.chat.viewbinders.MyFileMessageHolder;
import com.tmt.messagecenter.screens.chat.viewbinders.MyImageFileMessageHolder;
import com.tmt.messagecenter.screens.chat.viewbinders.MyUserMessageHolder;
import com.tmt.messagecenter.screens.chat.viewbinders.OtherAudioMessageHolder;
import com.tmt.messagecenter.screens.chat.viewbinders.OtherFileMessageHolder;
import com.tmt.messagecenter.screens.chat.viewbinders.OtherImageFileMessageHolder;
import com.tmt.messagecenter.screens.chat.viewbinders.OtherUserMessageHolder;
import com.tmt.messagecenter.utils.DownloadUtils;
import java.util.ArrayList;
import java.util.List;


public class MessageCenterChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
    private DownloadUtils downloadUtils;

    private List<UserMessage> mMessageList;

    private OnItemClickListener mItemClickListener;

    private XmppClient xmppClient;

    public interface OnItemClickListener {
        void onUserMessageItemClick(UserMessage message);
    }


    public MessageCenterChatAdapter(Context context, XmppClient xmppClient) {
        mContext = context;
        downloadUtils = new DownloadUtils(context);
        mMessageList = new ArrayList<>();
        this.xmppClient = xmppClient;
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
                return new MyImageFileMessageHolder(myImageFileMsgView, downloadUtils);
            case VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER:
                View otherImageFileMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_file_image_other, parent, false);
                return new OtherImageFileMessageHolder(otherImageFileMsgView, downloadUtils);
            case VIEW_TYPE_FILE_MESSAGE_AUDIO_OTHER:
                View audioFileMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_audio_other, parent, false);
                return new OtherAudioMessageHolder(audioFileMsgView, downloadUtils);
            case VIEW_TYPE_FILE_MESSAGE_AUDIO_ME:
                View audioMyFileMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_audio_me, parent, false);
                return new MyAudioMessageHolder(audioMyFileMsgView, downloadUtils);
            default:
                return null;

        }
    }

    /**
     * Binds variables in the SendBirdMessage to UI components in the ViewHolder.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        UserMessage message = mMessageList.get(position);
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

        UserMessage message = mMessageList.get(position);

        if (!message.isFile()) {
            if (xmppClient.isMyMessage(message.getMessage().getFrom()))
                return VIEW_TYPE_USER_MESSAGE_ME;
            else
                return VIEW_TYPE_USER_MESSAGE_OTHER;
        }
        else {
            FileExtension fileMessage = message.getFile();
            if (fileMessage.getContentType().toLowerCase().contains("image")) {
                if (xmppClient.isMyMessage(message.getMessage().getFrom())) {
                    return VIEW_TYPE_FILE_MESSAGE_IMAGE_ME;
                } else {
                    return VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER;
                }
            }
            else if (fileMessage.getContentType().toLowerCase().contains("audio") || fileMessage.getContentType().toLowerCase().contains("3gpp")) {
                if (xmppClient.isMyMessage(message.getMessage().getFrom())) {
                    return VIEW_TYPE_FILE_MESSAGE_AUDIO_ME;
                } else {
                    return VIEW_TYPE_FILE_MESSAGE_AUDIO_OTHER;
                }
            }
            else {
                if (xmppClient.isMyMessage(message.getMessage().getFrom())) {
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
            mMessageList.addAll(xmppClient.loadPrevMessages(PAGE_SIZE));
            xmppClient.seenTheMessages();
            notifyDataSetChanged();
        } catch(Exception e) {
        }
    }

    public void updateStatus(String id, String status) {
        for (UserMessage u : mMessageList) {
            if (u.getMessage().getStanzaId() != null && u.getMessage().getStanzaId().equals(id)) {
                u.setStatus(status);
            }
        }
        notifyDataSetChanged();
    }

    public void updateStatus(String room_id) {
        ReceiptStorage receiptStorage = new ReceiptStorage(mContext);
        for (UserMessage u : mMessageList) {
            u.setStatus(u.getTimeStamp() <= receiptStorage.getLastReceivedSeenStamp(room_id) ? DeliveryReceiptStatus.SEEN : DeliveryReceiptStatus.RECEIVED);
        }
        notifyDataSetChanged();
    }


    public void loadPrev() {
        try {
            mMessageList.addAll(xmppClient.loadPrevMessages(PAGE_SIZE));
            notifyDataSetChanged();
        } catch(Exception e) {
        }
    }


    public boolean isFailedMessage(UserMessage message) {
        return message.getStatus().equals(DeliveryReceiptStatus.FAILED);
    }


    public void removeFailedMessage(UserMessage message) {
        if (message instanceof UserMessage) {
            removeMessageFromArrayList(message);
        }
        notifyDataSetChanged();
    }

    private boolean removeMessageFromArrayList(UserMessage baseMessage) {
        for (UserMessage userMessage : mMessageList) {
            if (userMessage != null && userMessage.equals(baseMessage)) {
                return mMessageList.remove(userMessage);
            }
        }
        return false;
    }

    public void setFileProgressPercent(UserMessage message, int percent) {
        for (UserMessage userMessage : mMessageList) {
            if (message.getMessage().getStanzaId().equals(userMessage.getMessage().getStanzaId())) {
                userMessage.setProgress(percent);
            }
        }
        notifyDataSetChanged();
    }

    public void addFirst(UserMessage message) {
        mMessageList.add(0, message);
        notifyDataSetChanged();
    }

    public void setItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }
}
