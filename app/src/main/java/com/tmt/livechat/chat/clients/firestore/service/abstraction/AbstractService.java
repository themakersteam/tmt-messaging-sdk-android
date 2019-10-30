package com.tmt.livechat.chat.clients.firestore.service.abstraction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.tmt.livechat.app_client.Livechat;
import com.tmt.livechat.chat.clients.firestore.service.message.constants.MessageConstants;
import com.tmt.livechat.chat.model.BaseMessage;
import com.tmt.livechat.chat.model.FileMessage;
import com.tmt.livechat.chat.model.LocationMessage;
import com.tmt.livechat.chat.model.TextMessage;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public abstract class AbstractService {

    private String channelId = null;

    public AbstractService(){

    }

    public AbstractService(String channelId) {
        this.channelId = channelId;
    }

    public abstract void destroy();

    protected FirebaseFirestore getDb() {
        return FirebaseFirestore.getInstance(Livechat.instance().getFirebaseApp());
    }

    protected FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance(Livechat.instance().getFirebaseApp());
    }

    protected String getUserId() {
        return getAuth().getCurrentUser().getUid();
    }

    protected String getChannelId() {
        return channelId;
    }

    protected BaseMessage pojo(QueryDocumentSnapshot document) {
        if (document.get("type").equals(MessageConstants.Type.TEXT))
            return document.toObject(TextMessage.class);
        else if (document.get("type").equals(MessageConstants.Type.LOCATION))
            return document.toObject(LocationMessage.class);
        else if (document.get("type").equals(MessageConstants.Type.FILE))
            return document.toObject(FileMessage.class);
        else
            return document.toObject(BaseMessage.class);
    }

    protected FirebaseStorage getStorage() {
        return FirebaseStorage.getInstance(Livechat.instance().getFirebaseApp());
    }

    protected FirebaseFunctions getFunctions() {
        return FirebaseFunctions.getInstance(Livechat.instance().getFirebaseApp(), "us-central1");
    }

    protected FirebaseInstanceId getInstanceId() {
        return FirebaseInstanceId.getInstance(Livechat.instance().getFirebaseApp());
    }
}
