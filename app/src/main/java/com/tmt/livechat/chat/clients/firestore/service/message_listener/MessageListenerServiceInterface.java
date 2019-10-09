package com.tmt.livechat.chat.clients.firestore.service.message_listener;

import com.tmt.livechat.chat.model.BaseMessage;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public interface MessageListenerServiceInterface {

    void register(Callbacks callbacks);

    interface Callbacks {
        void updateMessageStatus(String id, String status);
        void newIncomingMessage(BaseMessage message);
    }
}
