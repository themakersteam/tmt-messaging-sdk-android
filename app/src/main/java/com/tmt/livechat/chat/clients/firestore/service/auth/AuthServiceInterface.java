package com.tmt.livechat.chat.clients.firestore.service.auth;

import com.tmt.livechat.app_client.interfaces.ConnectionInterface;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public interface AuthServiceInterface {

    void connect(String authToken, final ConnectionInterface connectionInterface);
    boolean isConnected();
    void disconnect();
}
