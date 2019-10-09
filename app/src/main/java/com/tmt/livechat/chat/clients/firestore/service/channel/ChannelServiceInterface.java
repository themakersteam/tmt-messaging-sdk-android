package com.tmt.livechat.chat.clients.firestore.service.channel;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public interface ChannelServiceInterface {

    void joinChannel(Callbacks callbacks);
    void updateReadAtTimestamp();
    void updateTyping(boolean isTyping);

    interface Callbacks {
        void onChannelFrozen();
        void lastReadAtUpdated(long new_val);
        void onTypingStatusUpdated(String participant, boolean isTyping);
        void onFailedToLoadChannel(Exception e);
    }
}
