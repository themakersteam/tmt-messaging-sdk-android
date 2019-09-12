package com.tmt.messagecenter.client;


import android.app.Activity;
import android.content.Context;
import com.tmt.messagecenter.client.interfaces.ConnectionInterface;
import com.tmt.messagecenter.client.interfaces.OpenChatInterface;
import com.tmt.messagecenter.client.interfaces.UnreadCountInterface;
import com.tmt.messagecenter.model.ConnectionRequest;
import com.tmt.messagecenter.model.Theme;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

abstract class MessageCenterInterface {

    abstract public void connect(ConnectionRequest connectionRequest, ConnectionInterface connectionInterface);
    abstract public boolean isConnected();
    abstract public XMPPTCPConnection getConnection();
    abstract public void openChatView(Activity context, String chat_id, Theme theme, OpenChatInterface openChatInterface);
    abstract public void getUnreadMessageCount(Context context, String chat_id, UnreadCountInterface unreadCountInterface);
    abstract public void disconnect();
}
