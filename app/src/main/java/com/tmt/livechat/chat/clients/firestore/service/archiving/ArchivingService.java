package com.tmt.livechat.chat.clients.firestore.service.archiving;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tmt.livechat.chat.constants.DeliveryReceiptStatus;
import com.tmt.livechat.chat.clients.firestore.service.abstraction.AbstractService;
import com.tmt.livechat.chat.model.BaseMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public class ArchivingService extends AbstractService implements ArchivingServiceInterface {

    private static ArchivingService archivingService;
    private DocumentSnapshot lastVisible;

    public ArchivingService(String channel_id) {
        super(channel_id);
    }

    @Override
    public void load(final int size, final long last_read_at_timestamp, final OnLoadCompleted onLoadCompleted) {
        if (lastVisible == null)
            getDb().collection("channels/" + getChannelId() + "/messages")
                    .orderBy("posted_at", Query.Direction.DESCENDING)
                    .limit(size).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    handleLoadTask(task,last_read_at_timestamp, onLoadCompleted);
                }
            });
        else
            getDb().collection("channels/" + getChannelId() + "/messages")
                    .orderBy("posted_at", Query.Direction.DESCENDING)
                    .limit(size).startAfter(lastVisible).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    handleLoadTask(task,last_read_at_timestamp,  onLoadCompleted);
                }
            });
    }

    @Override
    public void destroy() {
        archivingService = null;
    }

    /**
     *
     * @param task
     */
    private void handleLoadTask(@NonNull Task<QuerySnapshot> task, long last_read_at_timestamp,  OnLoadCompleted onLoadCompleted) {
        if (task != null && task.isSuccessful()) {
            if (task.getResult().getDocuments() != null && task.getResult().getDocuments().size() > 0) {
                lastVisible = task.getResult().getDocuments().get(task.getResult().getDocuments().size() - 1);
            }
            QuerySnapshot snapshot = task.getResult();
            List<BaseMessage> baseMessages = new ArrayList<>();
            for (QueryDocumentSnapshot document : snapshot) {
                BaseMessage baseMessage = pojo(document);
                baseMessage.setId(document.getId());
                baseMessage.setStatus(baseMessage.getReceived_at() != null ? DeliveryReceiptStatus.RECEIVED : DeliveryReceiptStatus.IN_PROGRESS);
                if (baseMessage.getStatus().equals(DeliveryReceiptStatus.RECEIVED)) {
                    if (baseMessage.getPosted_at().toDate().getTime() <= last_read_at_timestamp) {
                        baseMessage.setStatus(DeliveryReceiptStatus.SEEN);
                    }
                }
                baseMessages.add(baseMessage);
            }
            onLoadCompleted.result(baseMessages);
        }
        else {
            onLoadCompleted.onFailedToLoad(task.getException());
        }
    }

    /**
     **/
    public static ArchivingService instance(String channel_id) {
        if (archivingService == null)
            archivingService = new ArchivingService(channel_id);
        return archivingService;
    }
}
