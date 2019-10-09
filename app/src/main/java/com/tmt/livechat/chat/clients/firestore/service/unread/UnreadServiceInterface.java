package com.tmt.livechat.chat.clients.firestore.service.unread;


import com.tmt.livechat.app_client.interfaces.UnreadCountInterface;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public interface UnreadServiceInterface {
    void getUnreadMessageCount(UnreadCountInterface unreadCountInterface);
}
