package com.tmt.livechat.chat.clients.firestore.service.message;

import com.tmt.livechat.chat.ChatInterface;
import com.tmt.livechat.chat.model.BaseMessage;
import java.io.File;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public interface MessageServiceInterface {

    void sendMessage(String text, MessageSendCallback messageSendCallback);
    void sendLocation(Double lat, Double lng, MessageSendCallback messageSendCallback);
    void sendFile(String type, String label, File file, ChatInterface.SendFileInterface sendFileInterface);

    interface MessageSendCallback {
        void sent(BaseMessage baseMessage);
        void onStatusUpdate(String id, String status);
    }
}
