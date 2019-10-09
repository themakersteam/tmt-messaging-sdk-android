package com.tmt.livechat.chat;

import android.content.Context;
import com.tmt.livechat.chat.model.BaseMessage;
import com.tmt.livechat.chat.model.FileMessage;
import java.io.File;
import java.util.List;

/**
 * Created by mohammednabil on 2019-09-05.
 */
public interface ChatInterface {

    void onCreate(Context context, final ChatInterface.Callbacks callbacks, String channel_id);
    void setTypingStatus(boolean typing);
    boolean isMyMessage(String user_id);
    void sendUserMessage(String message_);
    void sendLocationMessage(double lat, double lng);
    void sendFileMessage(File file, String type,  String caption, ChatInterface.SendFileInterface sendFileInterface);
    void messageStatusUpdated(String id, String status);
    void updateReadAtTimestamp();
    void updateUserStatus(boolean online);
    void loadPrevMessages(int page_limit,long last_read_at_stamp,  LoadPrevInterface loadPrevInterface);
    void onDestroy();

    interface Callbacks {
        void newIncomingMessage(BaseMessage message);
        void sendingMessage(BaseMessage userMessage);
        void onMessageStatusUpdated(String id, String status);
        void onTypingStatusUpdated(boolean isTyping, String from);
        void onChannelFrozen();
        void lastReadAtUpdated(long newVal);
        void onConnectionError(int code, Exception e);
    }

    interface LoadPrevInterface {
        void onReady(List<BaseMessage> baseMessages);
    }

    interface SendFileInterface {
        void onReady(FileMessage userMessage);
        void onProgress(FileMessage userMessage, int parentage);
        void onSent(FileMessage message, Exception e);
    }

}
