package com.tmt.livechat.chat.clients.firestore.service.archiving;

import com.tmt.livechat.chat.model.BaseMessage;
import java.util.List;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public interface ArchivingServiceInterface {

    void load(int size, long last_read_at_timestamp, OnLoadCompleted onLoadCompleted);

    interface OnLoadCompleted {
        void result(List<BaseMessage> messageList);
        void onFailedToLoad(Exception e);
    }
}
