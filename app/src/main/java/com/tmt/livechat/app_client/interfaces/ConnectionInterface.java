package com.tmt.livechat.app_client.interfaces;

/**
 * Created by mohammednabil on 2019-09-03.
 */
public interface ConnectionInterface {
    void onConnected();
    void onConnectionError(int code, Exception e);
}
