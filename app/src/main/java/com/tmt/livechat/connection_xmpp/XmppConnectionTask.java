package com.tmt.livechat.connection_xmpp;

import android.os.AsyncTask;
import com.tmt.livechat.connection_xmpp.constants.ChatErrorCodes;
import com.tmt.livechat.connection_xmpp.extensions.DataExtension;
import com.tmt.livechat.client.interfaces.ConnectionInterface;
import com.tmt.livechat.connection_xmpp.extensions.SeenReceiptExtension;
import com.tmt.livechat.model.ConnectionRequest;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.ping.PingManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import java.io.IOException;

/**
 * Created by mohammednabil on 2019-09-01.
 */
public class XmppConnectionTask extends AsyncTask<String, Void, Void> {

    private XMPPTCPConnection mConnection;
    private ConnectionRequest connectionRequest;
    private ConnectionInterface connectionInterface;


    public XmppConnectionTask(ConnectionRequest connectionRequest, ConnectionInterface connectionInterface) {
        this.connectionInterface = connectionInterface;
        this.connectionRequest = connectionRequest;
    }

    public XMPPTCPConnection getConnection() {
        return mConnection;
    }

    protected Void doInBackground(String... urls) {
        DomainBareJid domainBareJid = null;
        try {
            domainBareJid= JidCreate.domainBareFrom(connectionRequest.getDomain());
        }
        catch (XmppStringprepException e) {
            connectionInterface.onConnectionError(ChatErrorCodes.INVALID_DOMAIN, e);
        }

//        System.setProperty("smack.debuggerClass","org.jivesoftware.smack.debugger.ConsoleDebugger");
//        System.setProperty("smack.debugEnabled", "true");
//        SmackConfiguration.DEBUG = true;

        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword(connectionRequest.getUsername(), connectionRequest.getPassword())
                .setXmppDomain(domainBareJid)
                .setHost(connectionRequest.getDomain())
                .setPort(5222)
                .setSendPresence(true)
                .setConnectTimeout(15000)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled) //Disable or enable as per development mode
                .build(); // to view what's happening in detail


        mConnection = new XMPPTCPConnection(config);

        ReconnectionManager.setEnabledPerDefault(true);
        ProviderManager.addExtensionProvider(DataExtension.ELEMENT, DataExtension.NAMESPACE, new DataExtension.Provider());
        ProviderManager.addExtensionProvider(SeenReceiptExtension.ELEMENT, SeenReceiptExtension.NAMESPACE, new SeenReceiptExtension.Provider());

        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(mConnection);
        reconnectionManager.setEnabledPerDefault(true);
        reconnectionManager.enableAutomaticReconnection();

        mConnection.addConnectionListener(new ConnectionListener() {
            @Override
            public void connected(XMPPConnection connection) {
                try {
                    SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
                    SASLAuthentication.blacklistSASLMechanism("PLAIN");
                    SASLAuthentication.unBlacklistSASLMechanism("SCRAM-SHA-1");
                    mConnection.login();
                }
                catch (XMPPException | SmackException | IOException | InterruptedException e) {
                    connectionInterface.onConnectionError(ChatErrorCodes.CONNECTION_ERROR, e);
                }
                mConnection.removeConnectionListener(this);
            }
            @Override
            public void authenticated(XMPPConnection connection, boolean resumed) {
                connectionInterface.onConnected();
                mConnection.removeConnectionListener(this);
                try {
                    PingManager.getInstanceFor(mConnection).setPingInterval(60) ;
                    PingManager.getInstanceFor(mConnection).pingMyServer();
                }
                catch (SmackException.NotConnectedException  | InterruptedException e) {

                }
            }

            @Override
            public void connectionClosed() {
            }

            @Override
            public void connectionClosedOnError(Exception e) {

            }
        });

        try {
            mConnection.connect();
        } catch (XMPPException | SmackException | IOException | InterruptedException e) {
            connectionInterface.onConnectionError(ChatErrorCodes.CONNECTION_ERROR, e);
        }
        return null;
    }

    protected void onPostExecute(Void feed) {

    }
}