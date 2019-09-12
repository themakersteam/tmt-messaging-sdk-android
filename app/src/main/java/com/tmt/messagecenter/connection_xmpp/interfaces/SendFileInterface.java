package com.tmt.messagecenter.connection_xmpp.interfaces;

import com.tmt.messagecenter.model.UserMessage;

/**
 * Created by mohammednabil on 2019-09-09.
 */
public interface SendFileInterface {
    void onReady(UserMessage userMessage);
    void onProgress(UserMessage userMessage, int parentage);
    void onSent(UserMessage message, Exception e);
}
