package com.tmt.livechat.connection_xmpp.storage;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Map;

/**
 * Created by mohammednabil on 2019-09-12.
 */
public class ReceiptStorage {

    private final String TAG = "MESSAGE_CENTER_RECEIPT_STORAGE";
    private Context context;

    public ReceiptStorage(Context context) {
        this.context = context;
    }

    public void setLastReceivedSeenStamp(String room_id, Long time) {
        Map<String, ?> allEntries = context.getSharedPreferences(TAG, context.MODE_PRIVATE).getAll();
        if (allEntries.size() > 5)
            clear();
        context.getSharedPreferences(TAG, context.MODE_PRIVATE).edit().putLong(room_id, time).commit();
    }


    public Long getLastReceivedSeenStamp(String room_id) {
        return context.getSharedPreferences(TAG, context.MODE_PRIVATE).getLong(room_id,0);
    }


    private void clear() {
        SharedPreferences clear = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        clear.edit().clear().commit();
    }
}
