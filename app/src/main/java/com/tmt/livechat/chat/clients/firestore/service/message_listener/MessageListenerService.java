package com.tmt.livechat.chat.clients.firestore.service.message_listener;

import androidx.annotation.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tmt.livechat.chat.constants.DeliveryReceiptStatus;
import com.tmt.livechat.chat.clients.firestore.service.abstraction.AbstractService;
import com.tmt.livechat.chat.model.BaseMessage;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public class MessageListenerService extends AbstractService implements MessageListenerServiceInterface {

    private static MessageListenerService messageListenerService;
    private boolean isFirstLoad = true;
    private ListenerRegistration listenerRegistration;

    public MessageListenerService(String channel_id) {
        super(channel_id);
    }

    @Override
    public void register(final Callbacks callbacks) {
        listenerRegistration = getDb().collection("channels/" + getChannelId() + "/messages").orderBy("posted_at", Query.Direction.DESCENDING).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null && queryDocumentSnapshots != null) {
                    if (!isFirstLoad && queryDocumentSnapshots.getDocuments() != null && queryDocumentSnapshots.getDocuments().size() > 0) {
                        BaseMessage baseMessage = pojo((QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0));
                        baseMessage.setId( queryDocumentSnapshots.getDocuments().get(0).getId());
                        if (baseMessage.getSender().equals(getUserId())) { // Its my message
                            callbacks.updateMessageStatus(baseMessage.getId(),baseMessage.getReceived_at() != null ?  DeliveryReceiptStatus.RECEIVED : DeliveryReceiptStatus.IN_PROGRESS);
                        }
                        else {
                            callbacks.newIncomingMessage(baseMessage);
                        }
                    }
                    isFirstLoad = false;
                }
            }
        });
    }

    @Override
    public void destroy() {
        if (listenerRegistration != null)
            listenerRegistration.remove();
        messageListenerService = null;
    }

    /**
     **/
    public static MessageListenerService instance(String channel_id) {
        if (messageListenerService == null)
            messageListenerService = new MessageListenerService(channel_id);
        return messageListenerService;
    }
}
