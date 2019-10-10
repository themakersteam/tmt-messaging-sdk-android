package com.tmt.livechat.chat.clients.firestore;

import android.content.Context;
import com.tmt.livechat.chat.ChatInterface;
import com.tmt.livechat.chat.clients.firestore.service.filedownload.FileDownloadService;
import com.tmt.livechat.chat.clients.firestore.service.fileupload.FileUploadService;
import com.tmt.livechat.chat.clients.firestore.service.notification.NotificationService;
import com.tmt.livechat.chat.clients.firestore.service.unread.UnreadService;
import com.tmt.livechat.chat.constants.ChatErrorCodes;
import com.tmt.livechat.chat.clients.firestore.service.archiving.ArchivingService;
import com.tmt.livechat.chat.clients.firestore.service.archiving.ArchivingServiceInterface;
import com.tmt.livechat.chat.clients.firestore.service.channel.ChannelService;
import com.tmt.livechat.chat.clients.firestore.service.channel.ChannelServiceInterface;
import com.tmt.livechat.chat.clients.firestore.service.message.MessageService;
import com.tmt.livechat.chat.clients.firestore.service.message.MessageServiceInterface;
import com.tmt.livechat.chat.model.BaseMessage;
import com.tmt.livechat.chat.clients.firestore.service.message_listener.MessageListenerService;
import com.tmt.livechat.chat.clients.firestore.service.message_listener.MessageListenerServiceInterface;
import com.tmt.livechat.chat.clients.firestore.service.user.UserService;
import com.tmt.livechat.chat.clients.firestore.service.user_status.UserStatusService;
import java.io.File;
import java.util.List;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public class FirestoreClient implements ChatInterface, ChannelServiceInterface.Callbacks {

    private String channel_name;
    private Callbacks chatCallbacks;

    @Override
    public void onCreate(Context context, final Callbacks chatCallbacks, String channel_name) {
        this.channel_name = channel_name;
        if (chatCallbacks != null) {
            this.chatCallbacks = chatCallbacks;
            ChannelService.instance(channel_name).joinChannel( this);
            MessageListenerService.instance(channel_name).register(new MessageListenerServiceInterface.Callbacks() {
                @Override
                public void newIncomingMessage(BaseMessage message) {
                    chatCallbacks.newIncomingMessage(message);
                }

                @Override
                public void updateMessageStatus(String id, String status) {
                    messageStatusUpdated(id, status);
                }
            });
        }
    }

    @Override
    public void setTypingStatus(boolean typing) {
        ChannelService.instance(channel_name).updateTyping(typing);
    }

    @Override
    public void onChannelFrozen() {
        chatCallbacks.onChannelFrozen();
    }


    @Override
    public void onFailedToLoadChannel(Exception e) {
        chatCallbacks.onConnectionError(ChatErrorCodes.FAILED_TO_LOAD, e);
    }

    @Override
    public boolean isMyMessage(String user_id) {
        return UserService.instance().isMyMessage(user_id);
    }

    @Override
    public void sendUserMessage(String message_) {
        MessageService.instance(channel_name).sendMessage(message_, new MessageServiceInterface.MessageSendCallback() {
            @Override
            public void sent(BaseMessage baseMessage) {
                chatCallbacks.sendingMessage(baseMessage);
            }

            @Override
            public void onStatusUpdate(String id, String status) {
                messageStatusUpdated(id, status);
            }
        });
    }

    @Override
    public void updateUserStatus(boolean online) {
        UserStatusService.instance().updateStatus(online);
    }


    @Override
    public void sendLocationMessage(double lat, double lng) {
        MessageService.instance(channel_name).sendLocation(lat, lng, new MessageServiceInterface.MessageSendCallback() {
            @Override
            public void sent(BaseMessage baseMessage) {
                chatCallbacks.sendingMessage(baseMessage);
            }

            @Override
            public void onStatusUpdate(String id, String status) {
                messageStatusUpdated(id, status);
            }
        });
    }

    @Override
    public void sendFileMessage(File file, String type, String caption, SendFileInterface sendFileInterface) {
        MessageService.instance(channel_name).sendFile(type, caption, file, sendFileInterface);
    }

    @Override
    public void messageStatusUpdated(String id, String status) {
        chatCallbacks.onMessageStatusUpdated(id, status);
    }

    @Override
    public void loadPrevMessages(int page_limit,long last_read_at_timestamp,  final LoadPrevInterface loadPrevInterface) {
        ArchivingService.instance(channel_name).load(page_limit,last_read_at_timestamp,  new ArchivingServiceInterface.OnLoadCompleted() {
            @Override
            public void result(List<BaseMessage> messageList) {
                loadPrevInterface.onReady(messageList);
            }

            @Override
            public void onFailedToLoad(Exception e) {
                chatCallbacks.onConnectionError(ChatErrorCodes.FAILED_TO_LOAD, e);
            }
        });
    }

    @Override
    public void updateReadAtTimestamp() {
        ChannelService.instance(channel_name).updateReadAtTimestamp();
    }

    @Override
    public void lastReadAtUpdated(long new_val) {
        chatCallbacks.lastReadAtUpdated(new_val);
    }

    @Override
    public void onTypingStatusUpdated(String participant, boolean isTyping) {
        chatCallbacks.onTypingStatusUpdated(isTyping, participant);
    }

    @Override
    public void onDestroy() {
        ChannelService.instance(channel_name).destroy();
        ArchivingService.instance(channel_name).destroy();
        FileDownloadService.instance().destroy();
        FileUploadService.instance(channel_name).destroy();
        MessageService.instance(channel_name).destroy();
        MessageListenerService.instance(channel_name).destroy();
        UnreadService.instance(channel_name).destroy();
        UserService.instance().destroy();
        UserStatusService.instance().destroy();
        NotificationService.instance().destroy();
    }
}
