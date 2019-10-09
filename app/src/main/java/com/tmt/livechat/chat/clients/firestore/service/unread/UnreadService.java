package com.tmt.livechat.chat.clients.firestore.service.unread;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tmt.livechat.app_client.interfaces.UnreadCountInterface;
import com.tmt.livechat.chat.clients.firestore.service.abstraction.AbstractService;
import com.tmt.livechat.chat.clients.firestore.service.channel.model.Channel;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public class UnreadService extends AbstractService implements UnreadServiceInterface {

    private static UnreadService unreadService;

    public UnreadService(String channel_id) {
        super(channel_id);
    }

    @Override
    public void getUnreadMessageCount(final UnreadCountInterface unreadCountInterface) {
        getDb().collection("channels").document(getChannelId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Channel channel = task.getResult().toObject(Channel.class);
                    Timestamp myLastReadAt = channel.getMyReadAtTimeStamp(getUserId());
                    if (myLastReadAt != null) {
                        getDb().collection("channels/" + getChannelId() + "/messages")
                                .whereGreaterThanOrEqualTo("posted_at", myLastReadAt)
                                .limit(10)
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    unreadCountInterface.onUnreadMessageCount(task.getResult().getDocuments().size());
                                }
                                else {
                                    unreadCountInterface.onUnreadMessageCount(0);
                                }
                            }
                        });
                    }
                    else {
                        unreadCountInterface.onUnreadMessageCount(0);
                    }
                }
                else {
                    unreadCountInterface.onUnreadMessageCount(0);
                }
            }
        });
    }

    @Override
    public void destroy() {
        unreadService = null;
    }

    /**
     **/
    public static UnreadService instance(String channel_id) {
        if (unreadService == null)
            unreadService = new UnreadService(channel_id);
        return unreadService;
    }
}
