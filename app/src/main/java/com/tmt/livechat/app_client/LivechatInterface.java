package com.tmt.livechat.app_client;

import android.app.Activity;
import com.google.firebase.FirebaseApp;
import com.tmt.livechat.app_client.interfaces.ConnectionInterface;
import com.tmt.livechat.app_client.interfaces.OpenChatInterface;
import com.tmt.livechat.app_client.interfaces.UnreadCountInterface;
import com.tmt.livechat.app_client.model.ConnectionRequest;
import com.tmt.livechat.app_client.model.Theme;

abstract class LivechatInterface {

    abstract public void connect(FirebaseApp firebaseApp, ConnectionRequest connectionRequest, ConnectionInterface connectionInterface);
    abstract public boolean isConnected();
    abstract public boolean isPresented();
    abstract public FirebaseApp getFirebaseApp();
    abstract public void openChatView(Activity context, String channel_id, Theme theme, OpenChatInterface openChatInterface);
    abstract public void getUnreadMessageCount(String chat_id, UnreadCountInterface unreadCountInterface);
    abstract public void disconnect();
}
