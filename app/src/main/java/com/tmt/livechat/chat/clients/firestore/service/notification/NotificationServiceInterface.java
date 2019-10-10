package com.tmt.livechat.chat.clients.firestore.service.notification;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public interface NotificationServiceInterface {

    void registerNotificationToken();
    void getToken(TokenCallbacks tokenCallbacks);
    void unregisterNotificationToken();

    interface TokenCallbacks {
        void onToken(String token);
    }
}
