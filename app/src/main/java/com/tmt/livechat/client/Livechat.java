package com.tmt.livechat.client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.tmt.livechat.client.interfaces.ConnectionInterface;
import com.tmt.livechat.client.interfaces.OpenChatInterface;
import com.tmt.livechat.client.interfaces.UnreadCountInterface;
import com.tmt.livechat.connection_xmpp.XmppConnectionTask;
import com.tmt.livechat.connection_xmpp.storage.ReceiptStorage;
import com.tmt.livechat.model.ConnectionRequest;
import com.tmt.livechat.model.Theme;
import com.tmt.livechat.model.UserMessage;
import com.tmt.livechat.screens.chat.LiveChatActivity;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.mam.MamManager;
import org.jxmpp.jid.impl.JidCreate;

/**
 * Created by mohammednabil on 2019-09-01.
 */
public class Livechat extends LivechatInterface {

    private static Livechat livechat;
    public static final Integer OPEN_CHAT_VIEW_REQUEST_CODE = 234;
    public static final Integer OPEN_CHAT_VIEW_RESPONSE_CODE = 232;
    private XmppConnectionTask xmppConnectionTask;

    @Override
    public void connect(ConnectionRequest connectionRequest, ConnectionInterface connectionInterface) {
        xmppConnectionTask = new XmppConnectionTask(connectionRequest, connectionInterface);
        xmppConnectionTask.execute();
    }

    @Override
    public boolean isConnected() {
        return getConnection() != null && getConnection().isConnected() && getConnection().isAuthenticated();
    }

    @Override
    public XMPPTCPConnection getConnection() {
        return xmppConnectionTask != null ? xmppConnectionTask.getConnection() : null;
    }

    @Override
    public void openChatView(Activity context, String chat_id, Theme theme, OpenChatInterface openChatInterface) {
        if (isConnected())
            openChatActivity(context, chat_id, theme);
        else {
            if (openChatInterface != null)
                openChatInterface.userNotConnected();
        }
    }

    @Override
    public void getUnreadMessageCount(Context context, String chat_id, UnreadCountInterface unreadCountInterface) {
        if (unreadCountInterface == null)
            return;
        try {
            MamManager mamManager = MamManager.getInstanceFor(getConnection(), JidCreate.entityBareFrom(chat_id));
            MamManager.MamQueryArgs.Builder mamQueryArgs = MamManager.MamQueryArgs.builder()
                    .setResultPageSizeTo(10)
                    .queryLastPage();
            MamManager.MamQuery mamQuery = mamManager.queryArchive(mamQueryArgs.build());
            int count = 0;
            for (Message message : mamQuery.getMessages()) {
                UserMessage userMessage = new UserMessage(message);
                if (userMessage.getTimeStamp() > new ReceiptStorage(context).getLastReceivedSeenStamp(chat_id)) {
                    count++;
                }
            }
            unreadCountInterface.onUnreadMessageCount(count);
        }
        catch (Exception e){
            unreadCountInterface.onUnreadMessageCount(0);
        }
    }

    @Override
    public void disconnect() {
        if (isConnected())
            getConnection().disconnect();
    }

    /**
     *
     * @param context
     * @param chat_url
     * @param theme
     */
    private void openChatActivity(Activity context, String chat_url, Theme theme) {
        Intent a1 = new Intent(context, LiveChatActivity.class);
        if (theme != null) {
            a1.putExtra("THEME", theme);
        }
        a1.putExtra("URL", chat_url);
        a1.putExtra("PACKAGE_NAME", context.getPackageName());
        context.startActivityForResult(a1, Livechat.OPEN_CHAT_VIEW_REQUEST_CODE);
    }
    /**
     **/
    public static Livechat instance() {
        if (livechat == null)
            livechat = new Livechat();
        return livechat;
    }
}
