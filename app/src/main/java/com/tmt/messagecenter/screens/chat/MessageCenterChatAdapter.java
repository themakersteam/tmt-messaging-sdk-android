package com.tmt.messagecenter.screens.chat;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dinuscxj.progressbar.CircleProgressBar;
import com.tmt.messagecenter.R;
import com.tmt.messagecenter.connection_xmpp.XmppClient;
import com.tmt.messagecenter.connection_xmpp.constants.DeliveryReceiptStatus;
import com.tmt.messagecenter.connection_xmpp.extensions.FileExtension;
import com.tmt.messagecenter.connection_xmpp.storage.ReceiptStorage;
import com.tmt.messagecenter.model.UserMessage;
import com.tmt.messagecenter.utils.AudioPlayerUtils;
import com.tmt.messagecenter.utils.DateUtils;
import com.tmt.messagecenter.utils.DownloadUtils;
import com.tmt.messagecenter.utils.ImageUtils;
import com.tmt.messagecenter.utils.TextUtils;


import java.io.File;
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
            // Nothing to load.
        }
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
                ((OtherFileMessageHolder) holder).bind(mContext, message, mItemClickListener);
                break;
            case VIEW_TYPE_FILE_MESSAGE_IMAGE_ME:
                ((MyImageFileMessageHolder) holder).bind(mContext, message, mItemClickListener);
                break;
            case VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER:
                ((OtherImageFileMessageHolder) holder).bind(mContext, message, mItemClickListener);
                break;
            case VIEW_TYPE_FILE_MESSAGE_AUDIO_ME:
                ((MyAudioMessageHolder) holder).bind(mContext, message, mItemClickListener);
                break;
            case VIEW_TYPE_FILE_MESSAGE_AUDIO_OTHER:
                ((OtherAudioMessageHolder) holder).bind(mContext, message, mItemClickListener);
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

    /**
     *
     * @param readReceiptImg
     */
    private void processReadReceipt(ImageView readReceiptImg, TextView chatTime, UserMessage message) {
        if (message.getStatus().equals(DeliveryReceiptStatus.FAILED)) {
            readReceiptImg.setImageResource(R.drawable.ic_sendfail);
            readReceiptImg.setColorFilter(Color.parseColor("#FFDD2C00"));
            chatTime.setTextColor(Color.parseColor("#fb2b2b"));
            chatTime.setText(mContext.getString(R.string.ms_chat_failed_to_send));
        } else if (message.getStatus().equals(DeliveryReceiptStatus.RECEIVED)) {
            readReceiptImg.setImageResource(R.drawable.ic_msgdelivered);
            readReceiptImg.setColorFilter(Color.parseColor("#9b9b9b"));
        } else if (message.getStatus().equals(DeliveryReceiptStatus.SEEN)) {
            readReceiptImg.setImageResource(R.drawable.ic_msgdelivered);
            readReceiptImg.setColorFilter(Color.parseColor("#00c269"));
        } else {
            readReceiptImg.setImageResource(R.drawable.ic_msg_progress);
            readReceiptImg.setColorFilter(Color.parseColor("#9b9b9b"));
        }
    }

    private class MyUserMessageHolder extends RecyclerView.ViewHolder {
        RelativeLayout mainLayout;
        TextView messageText, editedText, timeText;
        ImageView readReceipt;
        ViewGroup urlPreviewContainer;
        ImageView urlPreviewMainImageView;

        MyUserMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_group_chat_message);
            editedText = itemView.findViewById(R.id.text_group_chat_edited);
            timeText = itemView.findViewById(R.id.text_group_chat_time);
            readReceipt = itemView.findViewById(R.id.img_group_chat_read_receipt);
            mainLayout = itemView.findViewById(R.id.main_parent);
            urlPreviewContainer = itemView.findViewById(R.id.url_preview_container);
            urlPreviewMainImageView = itemView.findViewById(R.id.image_url_preview_main);

        }

        void bind(final Context context, final UserMessage message, final OnItemClickListener clickListener, final int position) {
            String mMessage = TextUtils.getLocationUrlMessageIfExists(message);
            messageText.setText(mMessage);
            timeText.setText(message.getTimeStamp() != null ? DateUtils.formatTime(message.getTimeStamp()) : "");
            timeText.setTextColor(Color.parseColor("#9b9b9b"));

            processReadReceipt(readReceipt, timeText, message);
            urlPreviewContainer.setVisibility(View.GONE);
            if (message.isLocation()){
                try {
                    urlPreviewContainer.setVisibility(View.VISIBLE);
                    Glide.with(context).load(R.drawable.map_place_holder).into(urlPreviewMainImageView);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                urlPreviewContainer.setVisibility(View.GONE);
            }

            if (clickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onUserMessageItemClick(message);
                    }
                });
            }
        }

    }

    private class OtherUserMessageHolder extends RecyclerView.ViewHolder {
        RelativeLayout mainLayout;
        TextView messageText, editedText, timeText;

        ViewGroup urlPreviewContainer;
        ImageView urlPreviewMainImageView;

        public OtherUserMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_group_chat_message);
            editedText = itemView.findViewById(R.id.text_group_chat_edited);
            timeText = itemView.findViewById(R.id.text_group_chat_time);
            mainLayout = itemView.findViewById(R.id.main_parent);
            urlPreviewContainer = itemView.findViewById(R.id.url_preview_container);
            urlPreviewMainImageView = itemView.findViewById(R.id.image_url_preview_main);
        }


        void bind(final Context context, final UserMessage message, final OnItemClickListener clickListener, final int position) {
            String mMessage = TextUtils.getLocationUrlMessageIfExists(message);
            messageText.setText(mMessage);
            timeText.setText(message.getTimeStamp() != null ? DateUtils.formatTime(message.getTimeStamp()) : "");


            urlPreviewContainer.setVisibility(View.GONE);
            if (message.isLocation()){
                try {
                    urlPreviewContainer.setVisibility(View.VISIBLE);
                    Glide.with(context).load(R.drawable.map_place_holder).into(urlPreviewMainImageView);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else {
                urlPreviewContainer.setVisibility(View.GONE);
            }

            if (clickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onUserMessageItemClick(message);
                    }
                });
            }
        }
    }

    private class MyFileMessageHolder extends RecyclerView.ViewHolder {
        RelativeLayout mainLayout;
        TextView fileNameText, timeText;
        CircleProgressBar circleProgressBar;
        ImageView readReceipt;

        public MyFileMessageHolder(View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.main_parent);
            timeText = itemView.findViewById(R.id.text_group_chat_time);
            fileNameText = itemView.findViewById(R.id.text_group_chat_file_name);
            readReceipt = itemView.findViewById(R.id.img_group_chat_read_receipt);
            circleProgressBar = itemView.findViewById(R.id.circle_progress);
        }

        void bind(Context context, final UserMessage message, final OnItemClickListener listener) {
            fileNameText.setText(message.getFile().getId());
            timeText.setText(message.getTimeStamp() != null ? DateUtils.formatTime(message.getTimeStamp()) : "");
            timeText.setTextColor(Color.parseColor("#9b9b9b"));
            circleProgressBar.setVisibility(message.getProgress() < 100 ? View.VISIBLE : View.GONE);
            circleProgressBar.setProgress(message.getProgress());

            processReadReceipt(readReceipt, timeText, message);

            if (listener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onUserMessageItemClick(message);
                    }
                });
            }
        }
    }

    private class OtherFileMessageHolder extends RecyclerView.ViewHolder {
        RelativeLayout mainLayout;
        TextView timeText, fileNameText;

        public OtherFileMessageHolder(View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.main_parent);
            timeText = itemView.findViewById(R.id.text_group_chat_time);
            fileNameText = itemView.findViewById(R.id.text_group_chat_file_name);

        }

        void bind(Context context, final UserMessage message, final OnItemClickListener listener) {
            fileNameText.setText(message.getFile().getId());
            timeText.setText(message.getTimeStamp() != null ? DateUtils.formatTime(message.getTimeStamp()) : "");

            if (listener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onUserMessageItemClick(message);
                    }
                });
            }
        }
    }

    private class MyAudioMessageHolder extends RecyclerView.ViewHolder {
        RelativeLayout mainLayout;
        TextView timer;
        CircleProgressBar circleProgressBar;
        ImageView readReceipt;
        ImageView mediaButton;
        TextView timeText;
        SeekBar seekBar;

        public MyAudioMessageHolder(View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.main_parent);
            timer = itemView.findViewById(R.id.tvTimer);
            mediaButton = itemView.findViewById(R.id.ivMedia);
            seekBar = itemView.findViewById(R.id.seek);
            circleProgressBar = itemView.findViewById(R.id.circle_progress);
            readReceipt = itemView.findViewById(R.id.img_group_chat_read_receipt);
            timeText = itemView.findViewById(R.id.text_group_chat_time);
        }

        void bind(final Context context, final UserMessage message, final OnItemClickListener listener) {
            timeText.setText(message.getTimeStamp() != null ? DateUtils.formatTime(message.getTimeStamp()) : "");
            timeText.setTextColor(Color.parseColor("#9b9b9b"));

            circleProgressBar.setVisibility(message.getProgress() < 100 ? View.VISIBLE : View.GONE);
            circleProgressBar.setProgress(message.getProgress());

            mediaButton.setTag(message.getFile().getId());
            seekBar.setEnabled(false);
            downloadUtils.downloadFile(message.getFile().getId(), new DownloadUtils.DownloadCallBacks() {
                @Override
                public void onFileReady(final File file) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            AudioPlayerUtils.setup(context, mediaButton, timer, seekBar, file);
                        }
                    });
                }
            });
            processReadReceipt(readReceipt, timeText, message);
        }
    }

    private class OtherAudioMessageHolder extends RecyclerView.ViewHolder {
        RelativeLayout mainLayout;
        TextView timer;
        ImageView mediaButton;
        TextView timeText;
        SeekBar seekBar;

        public OtherAudioMessageHolder(View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.main_parent);
            timer = itemView.findViewById(R.id.tvTimer);
            mediaButton = itemView.findViewById(R.id.ivMedia);
            seekBar = itemView.findViewById(R.id.seek);
            timeText = itemView.findViewById(R.id.text_group_chat_time);
        }

        void bind(final Context context, final UserMessage message, final OnItemClickListener listener) {
            timeText.setText(message.getTimeStamp() != null ? DateUtils.formatTime(message.getTimeStamp()) : "");
            timeText.setTextColor(Color.parseColor("#9b9b9b"));
            mediaButton.setTag(message.getFile().getId());
            seekBar.setEnabled(false);
            downloadUtils.downloadFile(message.getFile().getId(), new DownloadUtils.DownloadCallBacks() {
                @Override
                public void onFileReady(final File file) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            AudioPlayerUtils.setup(context, mediaButton, timer, seekBar, file);
                        }
                    });
                }
            });
        }
    }


    /**
     * A ViewHolder for file messages that are images.
     * Displays only the image thumbnail.
     */
    private class MyImageFileMessageHolder extends RecyclerView.ViewHolder {
        RelativeLayout mainLayout;
        TextView timeText, caption;
        ImageView fileThumbnailImage, readReceipt;
        CircleProgressBar circleProgressBar;

        public MyImageFileMessageHolder(View itemView) {
            super(itemView);
            mainLayout =  itemView.findViewById(R.id.main_parent);
            timeText =  itemView.findViewById(R.id.text_group_chat_time);
            fileThumbnailImage =  itemView.findViewById(R.id.image_group_chat_file_thumbnail);
            readReceipt = itemView.findViewById(R.id.img_group_chat_read_receipt);
            circleProgressBar = itemView.findViewById(R.id.circle_progress);
            caption = itemView.findViewById(R.id.text_caption);

        }

        void bind(final Context context, final UserMessage message, final OnItemClickListener listener) {
            timeText.setText(message.getTimeStamp() != null ? DateUtils.formatTime(message.getTimeStamp()) : "");
            timeText.setTextColor(Color.parseColor("#9b9b9b"));

            circleProgressBar.setVisibility(message.getProgress() < 100 ? View.VISIBLE : View.GONE);
            circleProgressBar.setProgress(message.getProgress());

            caption.setVisibility(message.getFile().getCaption() != null && !message.getFile().getCaption().isEmpty() ? View.VISIBLE : View.GONE);
            caption.setText(message.getFile().getCaption());

            processReadReceipt(readReceipt, timeText, message);

            downloadUtils.downloadFile(message.getFile().getId(), new DownloadUtils.DownloadCallBacks() {
                @Override
                public void onFileReady(File file) {
                    ImageUtils.displayImageFromFile(context, file, fileThumbnailImage, null);
                }
            });

            if (listener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onUserMessageItemClick(message);
                    }
                });
            }
        }
    }

    private class OtherImageFileMessageHolder extends RecyclerView.ViewHolder {
        RelativeLayout mainLayout;
        TextView timeText, caption;
        ImageView fileThumbnailImage;

        public OtherImageFileMessageHolder(View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.main_parent);
            timeText = itemView.findViewById(R.id.text_group_chat_time);
            fileThumbnailImage = itemView.findViewById(R.id.image_group_chat_file_thumbnail);
            caption = itemView.findViewById(R.id.text_caption);
        }

        void bind(final Context context, final UserMessage message, final OnItemClickListener listener) {
            timeText.setText(message.getTimeStamp() != null ? DateUtils.formatTime(message.getTimeStamp()) : "");

            caption.setVisibility(message.getFile().getCaption() != null && !message.getFile().getCaption().isEmpty() ? View.VISIBLE : View.GONE);
            caption.setText(message.getFile().getCaption());

            downloadUtils.downloadFile(message.getFile().getId(), new DownloadUtils.DownloadCallBacks() {
                @Override
                public void onFileReady(File file) {
                    ImageUtils.displayImageFromFile(context, file, fileThumbnailImage, null);
                }
            });

            if (listener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onUserMessageItemClick(message);
                    }
                });
            }
        }
    }

}
