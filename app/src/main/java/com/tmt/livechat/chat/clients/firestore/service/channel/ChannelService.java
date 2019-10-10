package com.tmt.livechat.chat.clients.firestore.service.channel;

import androidx.annotation.Nullable;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.tmt.livechat.chat.clients.firestore.service.abstraction.AbstractService;
import com.tmt.livechat.chat.clients.firestore.service.channel.constants.ChannelConsants;
import com.tmt.livechat.chat.clients.firestore.service.channel.model.Channel;
import com.tmt.livechat.chat.clients.firestore.service.channel.model.ParticipantData;
import java.util.Date;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public class ChannelService extends AbstractService implements ChannelServiceInterface {

    private static ChannelService channelService;

    private ListenerRegistration listenerRegistration;

    public ChannelService(String channel_id) {
        super(channel_id);
    }


    @Override
    public void joinChannel(final Callbacks callbacks) {
        listenerRegistration = getDb().document("channels/" + getChannelId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (callbacks == null)
                    return;
                if (e != null) {
                    callbacks.onFailedToLoadChannel(e);
                }
                else {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Channel channel = documentSnapshot.toObject(Channel.class);
                        if (channel.getStatus().equals(ChannelConsants.Status.CLOSED))
                            callbacks.onChannelFrozen();
                        callbacks.lastReadAtUpdated(getMinimumReadAt(channel));
                        checkTypingStatus(channel, callbacks);
                    }
                    else {
                        callbacks.onFailedToLoadChannel(new Exception("Data is null"));
                    }
                }
            }
        });
    }

    @Override
    public void updateReadAtTimestamp() {
        String key = "participants_data." + getUserId() + ".read_at";
        getDb().document("channels/" + getChannelId()).update(key, Timestamp.now());
    }


    @Override
    public void updateTyping(boolean isTyping) {
        String key = "participants_data." + getUserId() + ".typing_at";
        Date now = new Date();
        now.setTime(isTyping ? now.getTime() + 30000 : now.getTime());
        Timestamp timestamp = new Timestamp(now);
        getDb().document("channels/" + getChannelId()).update(key, timestamp);
    }

    @Override
    public void destroy() {
        if (listenerRegistration != null)
            listenerRegistration.remove();
        channelService = null;
    }

    /**
     *
     * @param channel
     * @return
     */
    private Long getMinimumReadAt(Channel channel) {
        if (channel != null && channel.getParticipants_data() != null) {
            long last_read_at = Long.MAX_VALUE;
            for (String id : channel.getParticipants_data().keySet()) {
                if (!id.equals(getUserId())) {
                    if (channel.getParticipants_data().get(id).getRead_at() != null) {
                        long user_last_time = channel.getParticipants_data().get(id).getRead_at().toDate().getTime();
                        last_read_at = (user_last_time < last_read_at) ? user_last_time : last_read_at;
                    }
                }
            }
            return last_read_at;
        }
        else {
            return 0l;
        }
    }

    private void checkTypingStatus(Channel channel, Callbacks callbacks) {
        if (channel != null && channel.getParticipants_data() != null) {
            boolean isTyping = false;
            for (String id : channel.getParticipants_data().keySet()) {
                if (!id.equals(getUserId())) {
                    ParticipantData participantData = channel.getParticipants_data().get(id);
                    if (participantData.getTyping_at() != null) {
                        if ((participantData.getTyping_at().toDate().getTime() - Timestamp.now().toDate().getTime()) > 20000) {
                            callbacks.onTypingStatusUpdated(participantData.getDisplay_name() , true);
                            isTyping = true;
                        }
                    }
                }
            }
            if (!isTyping)
                callbacks.onTypingStatusUpdated("", false);
        }
        else {
            callbacks.onTypingStatusUpdated("", false);
        }
    }

    /**
     **/
    public static ChannelService instance(String channel_id) {
        if (channelService == null)
            channelService = new ChannelService(channel_id);
        return channelService;
    }
}
