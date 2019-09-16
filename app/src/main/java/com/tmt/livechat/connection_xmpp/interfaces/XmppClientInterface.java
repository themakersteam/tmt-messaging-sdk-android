package com.tmt.livechat.connection_xmpp.interfaces;

import android.content.Context;
import com.tmt.livechat.model.UserMessage;
import org.jxmpp.jid.Jid;
import java.io.File;
import java.util.List;

/**
 * Created by mohammednabil on 2019-09-05.
 */
public interface XmppClientInterface {
    void onCreate(Context context, final XmppChatCallbacks xmppChatCallbacks, String chatId);
    void setTypingStatus(boolean typing);
    boolean isMyMessage(Jid from);
    void sendUserMessage(String message_);
    void sendLocationMessage(String lat, String lng);
    void sendFileMessage(Context context, File file, String caption, SendFileInterface sendFileInterface);
    void seenTheMessages();
    void messageStatusUpdated(String id, String status);
    List<UserMessage> loadPrevMessages(int page_limit);
}
