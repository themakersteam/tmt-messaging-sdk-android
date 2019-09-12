package com.tmt.messagecenter.connection_xmpp.interfaces;

import com.tmt.messagecenter.model.UserMessage;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smackx.muc.MultiUserChat;

/**
 * Created by mohammednabil on 2019-09-02.
 */
public interface XmppChatCallbacks {

    void onChatReady(MultiUserChat chat);
    void newIncomingMessage(UserMessage message);
    void sendingMessage(UserMessage userMessage);
    void onMessageStatusUpdated(String id, String status);
    void seenStampReceived(String room_id);
    void onTypingStatusUpdated(boolean isTyping, String from);
    void reconnecting();
    void reconnected();
    void onConnectionError(int code, Exception e);
}
