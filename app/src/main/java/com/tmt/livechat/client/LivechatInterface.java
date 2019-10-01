package com.tmt.livechat.client;


import android.app.Activity;
import android.content.Context;
import com.tmt.livechat.client.interfaces.ConnectionInterface;
import com.tmt.livechat.client.interfaces.OpenChatInterface;
import com.tmt.livechat.client.interfaces.UnreadCountInterface;
import com.tmt.livechat.model.ConnectionRequest;
import com.tmt.livechat.model.OpenChatRequest;
import com.tmt.livechat.model.Theme;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

abstract class LivechatInterface {

    abstract public void connect(ConnectionRequest connectionRequest, ConnectionInterface connectionInterface);
    abstract public boolean isConnected();
    abstract public XMPPTCPConnection getConnection();
    abstract public void openChatView(Activity context, OpenChatRequest openChatRequest, Theme theme, OpenChatInterface openChatInterface);
    abstract public void getUnreadMessageCount(Context context, String chat_id, UnreadCountInterface unreadCountInterface);
    abstract public void disconnect();
}
