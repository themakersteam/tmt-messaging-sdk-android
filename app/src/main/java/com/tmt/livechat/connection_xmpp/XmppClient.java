package com.tmt.livechat.connection_xmpp;

import android.content.Context;
import android.os.Handler;
import com.tmt.livechat.connection_xmpp.constants.ChatErrorCodes;
import com.tmt.livechat.connection_xmpp.constants.DeliveryReceiptStatus;
import com.tmt.livechat.connection_xmpp.extensions.DataExtension;
import com.tmt.livechat.client.Livechat;
import com.tmt.livechat.connection_xmpp.extensions.LocationExtension;
import com.tmt.livechat.connection_xmpp.extensions.SeenReceiptExtension;
import com.tmt.livechat.connection_xmpp.interfaces.SendFileInterface;
import com.tmt.livechat.connection_xmpp.interfaces.XmppChatCallbacks;
import com.tmt.livechat.connection_xmpp.interfaces.XmppClientInterface;
import com.tmt.livechat.connection_xmpp.manegers.S3FileUploadManager;
import com.tmt.livechat.connection_xmpp.network.XmppNetwork;
import com.tmt.livechat.connection_xmpp.storage.ReceiptStorage;
import com.tmt.livechat.model.OpenChatRequest;
import com.tmt.livechat.model.UserMessage;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.ReconnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.muc.MucEnterConfiguration;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohammednabil on 2019-09-03.
 */
public class XmppClient implements XmppClientInterface, StanzaListener, ReconnectionListener, ConnectionListener, MessageListener {

    private MultiUserChat chat;
    private XmppChatCallbacks chatCallbacks;
    private Resourcepart resourcepart;
    private String lastPageUUID;
    private OpenChatRequest chatRequest;
    private Context context;

    private int connection_retries = 0;

    @Override
    public void onCreate(Context context, final XmppChatCallbacks chatCallbacks, OpenChatRequest chatRequest) {
        this.context = context;
        this.chatRequest = chatRequest;
        if (chatCallbacks != null) {
            this.chatCallbacks = chatCallbacks;
            if (Livechat.instance().getConnection() == null || context == null)
                chatCallbacks.onConnectionError(ChatErrorCodes.NULL_CONNECTION, new Exception("Connection is null"));
            else {
                setupResourcePart();
                registerStanzaFilter();
                registerConnectionListener();
                joinRoom(chatRequest.getChatId());
            }
        }
    }

    @Override
    public void setTypingStatus(boolean typing) {
        if (chat != null) {
            Message message = new Message();
            ChatStateExtension extension = new ChatStateExtension(typing ? ChatState.composing : ChatState.paused);
            message.addExtension(extension);
            message.setType(Message.Type.groupchat);
            try {
                chat.sendMessage(message);
            }
            catch (Exception e){}
        }
    }

    /**
     *
     * @return true if the message belongs to the sender
     */
    @Override
    public boolean isMyMessage(Jid from) {
        if (from == null)
            return true;
        return from.getResourceOrEmpty().toString().equals(resourcepart.toString());
    }

    @Override
    public void sendUserMessage(String message_) {
        if (chat != null) {
            Message message = new Message();
            message.setBody(message_);
            DataExtension dataExtension = new DataExtension();
            dataExtension.setTimestamp(System.currentTimeMillis());
            message.addExtension(dataExtension);
            chatCallbacks.sendingMessage(new UserMessage(message));
            try {
                chat.sendMessage(message);
            }
            catch (InterruptedException | SmackException.NotConnectedException e) {
                messageStatusUpdated(message.getStanzaId(), DeliveryReceiptStatus.FAILED);
            }
        }
    }


    @Override
    public void sendLocationMessage(String lat, String lng) {
        if (chat != null) {
            Message message = new Message();
            message.setBody("<empty>");
            DataExtension dataExtension = new DataExtension();
            dataExtension.setTimestamp(System.currentTimeMillis());
            dataExtension.setLocation(new LocationExtension(lat, lng));
            message.addExtension(dataExtension);
            message.setType(Message.Type.groupchat);
            chatCallbacks.sendingMessage(new UserMessage(message));
            try {
                chat.sendMessage(message);
            }
            catch (InterruptedException | SmackException.NotConnectedException e) {
                messageStatusUpdated(message.getStanzaId(), DeliveryReceiptStatus.FAILED);
            }
        }
    }

    @Override
    public void sendFileMessage(Context context, File file, String caption, SendFileInterface sendFileInterface) {
        try {
            S3FileUploadManager.getInstanceFor(Livechat.instance().getConnection()).sendFile(context, chat, file, caption, sendFileInterface);
        }
        catch (Exception e){
            sendFileInterface.onSent(null, e);
        }
    }

    @Override
    public void seenTheMessages() {
        if (chat != null) {
            try {
                Message message = new Message();
                message.addExtension(new SeenReceiptExtension());
                chat.sendMessage(message);
            }
            catch (Exception e){
            }
        }
    }

    @Override
    public List<UserMessage> loadPrevMessages(int page_limit) {
        try {
            MamManager mamManager = MamManager.getInstanceFor(Livechat.instance().getConnection(), chat.getRoom());
            MamManager.MamQueryArgs.Builder mamQueryArgs = MamManager.MamQueryArgs.builder()
                    .setResultPageSizeTo(page_limit);
            if (lastPageUUID == null)
                mamQueryArgs.queryLastPage();
            else
                mamQueryArgs.beforeUid(lastPageUUID);
            MamManager.MamQuery mamQuery = mamManager.queryArchive(mamQueryArgs.build());
            lastPageUUID = mamQuery.getMamResultExtensions().get(0).getId();

            ArrayList<UserMessage> userMessages = new ArrayList<>();
            for (int i = mamQuery.getMessageCount() - 1; i >= 0; i--) {
                UserMessage userMessage = new UserMessage(mamQuery.getMessages().get(i));
                if (userMessage.getTimeStamp() > new ReceiptStorage(context).getLastReceivedSeenStamp(chat.getRoom().asEntityBareJidString())) {
                    userMessage.setStatus(DeliveryReceiptStatus.RECEIVED);
                }
                else {
                    userMessage.setStatus(DeliveryReceiptStatus.SEEN);
                }
                userMessages.add(userMessage);
            }
            return userMessages;
        }
        catch (Exception e){
            return new ArrayList<>();
        }
    }

    @Override
    public void messageStatusUpdated(String id, String status) {
        chatCallbacks.onMessageStatusUpdated(id, status);
    }

    @Override
    public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {
        if (packet.getError() != null && packet.getExtension(DataExtension.NAMESPACE) != null) {
            messageStatusUpdated(packet.getStanzaId(), DeliveryReceiptStatus.FAILED);
        }
        else if (packet.getExtension(ChatStateExtension.NAMESPACE) != null && !isMyMessage(packet.getFrom())) {
            if (packet.getExtension(ChatStateExtension.NAMESPACE).getElementName().equals(ChatState.paused.name()))
                chatCallbacks.onTypingStatusUpdated(false, null);
            else if (packet.getExtension(ChatStateExtension.NAMESPACE).getElementName().equals(ChatState.composing.name()))
                chatCallbacks.onTypingStatusUpdated(true, packet.getFrom().getResourceOrEmpty().toString());
        }
        else if (packet.getExtension(SeenReceiptExtension.NAMESPACE) != null && !isMyMessage(packet.getFrom())) {
            new ReceiptStorage(context).setLastReceivedSeenStamp(chat.getRoom().asEntityBareJidString(), System.currentTimeMillis());
            chatCallbacks.seenStampReceived(chat.getRoom().asEntityBareJidString());
        }
    }

    @Override
    public void processMessage(Message message) {
        if (message.getExtension(SeenReceiptExtension.NAMESPACE) != null) {

        }
        else if (message.getBody() != null && !isMyMessage(message.getFrom()))
            chatCallbacks.newIncomingMessage(new UserMessage(message));
        else if (isMyMessage(message.getFrom()) && message.getBody() != null) {
            chatCallbacks.onMessageStatusUpdated(message.getStanzaId(), DeliveryReceiptStatus.RECEIVED);
            XmppNetwork.instance().sendNotification(chatRequest.getServiceUrl(), chatRequest.getChatId(), new UserMessage(message));
        }
    }

    @Override
    public void connected(XMPPConnection connection) {

    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        chatCallbacks.reconnected();
        connection_retries = 0;
    }

    @Override
    public void reconnectingIn(int seconds) {

    }

    @Override
    public void reconnectionFailed(Exception e) {
        if (connection_retries == 2)
            chatCallbacks.onConnectionError(ChatErrorCodes.THREE_CONNECTIONS_FAILURE, null);
        connection_retries++;
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        chatCallbacks.reconnecting();
    }

    @Override
    public void connectionClosed() {
        chatCallbacks.onConnectionError(ChatErrorCodes.CONNECTION_ERROR, null);
    }

    @Override
    public void onDestroy() {
        try {
            Livechat.instance().getConnection().removeAsyncStanzaListener(this);
            ReconnectionManager.getInstanceFor(Livechat.instance().getConnection()).removeReconnectionListener(this);
            Livechat.instance().getConnection().removeConnectionListener(this);
            chat.removeMessageListener(this);
        }
        catch (Exception e){}
    }

    private void setupResourcePart() {
        try {
            resourcepart = Resourcepart.from(Livechat.instance().getConnection().getUser().asEntityBareJid().getLocalpart().toString());
        }
        catch (Exception e){}
    }

    private void registerStanzaFilter() {
        Livechat.instance().getConnection().addAsyncStanzaListener(this, new StanzaTypeFilter(Message.class));

    }

    private void registerConnectionListener() {
        ReconnectionManager.getInstanceFor(Livechat.instance().getConnection()).addReconnectionListener(this);
        Livechat.instance().getConnection().addConnectionListener(this);
    }

    private void registerIncomingMessageReceiver() {
        chat.addMessageListener(this);
    }

    private void joinRoom(String chat_id) {
        try {
            EntityBareJid jid = JidCreate.entityBareFrom(chat_id);
            chat = MultiUserChatManager.getInstanceFor(Livechat.instance().getConnection()).getMultiUserChat(jid);
            MultiUserChatManager.getInstanceFor(Livechat.instance().getConnection()).setAutoJoinOnReconnect(true);
            try {
                final MucEnterConfiguration.Builder v= chat.getEnterConfigurationBuilder(resourcepart);
                v.requestNoHistory();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            chat.join(v.build());
                            registerIncomingMessageReceiver();
                        }
                        catch (Exception e) {
                            chatCallbacks.onConnectionError(ChatErrorCodes.INVALID_ROOM, e);
                        }
                    }
                }, 1000);
            }
            catch (Exception e){
                chatCallbacks.onConnectionError(ChatErrorCodes.INVALID_ROOM, e);
            }
            chatCallbacks.onChatReady(chat);
        }
        catch (XmppStringprepException e) {
            chatCallbacks.onConnectionError(ChatErrorCodes.INVALID_USER, e);
        }
    }

}
