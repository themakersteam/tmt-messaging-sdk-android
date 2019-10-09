package com.tmt.livechat.chat.clients.firestore.service.message;

import android.net.Uri;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.GeoPoint;
import com.tmt.livechat.chat.ChatInterface;
import com.tmt.livechat.chat.constants.DeliveryReceiptStatus;
import com.tmt.livechat.chat.clients.firestore.service.fileupload.FileUploadService;
import com.tmt.livechat.chat.model.FileData;
import com.tmt.livechat.chat.model.FileMessage;
import com.tmt.livechat.chat.model.LocationMessage;
import com.tmt.livechat.chat.model.TextMessage;

import java.io.File;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public class MessageService extends FileUploadService implements MessageServiceInterface {

    private static MessageService messageService;

    public MessageService(String channel_id) {
        super(channel_id);
    }


    @Override
    public void sendMessage(String text,final MessageSendCallback messageSendCallback) {
        final CollectionReference collectionReference =  getDb().collection("channels/" + getChannelId() + "/messages");
        String id = collectionReference.document().getId();

        final TextMessage textMessage = new TextMessage();
        textMessage.build(getUserId());
        textMessage.setText(text);
        textMessage.setId(id);

        messageSendCallback.sent(textMessage);

        collectionReference.document(id).set(textMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    messageSendCallback.onStatusUpdate(textMessage.getId(), DeliveryReceiptStatus.FAILED);
                }
            }
        });
    }

    @Override
    public void sendFile(String type, String label, final File file, final ChatInterface.SendFileInterface sendFileInterface) {
        final CollectionReference collectionReference =  getDb().collection("channels/" + getChannelId() + "/messages");
        final String id = collectionReference.document().getId();
        final FileMessage fileMessage = new FileMessage();
        fileMessage.build(getUserId());
        fileMessage.setFile(new FileData());
        fileMessage.getFile().setFile_type(type);
        fileMessage.getFile().setLabel(label != null ? label : "");
        fileMessage.setId(id);
        fileMessage.getFile().setFile_uri(file.getPath());
        fileMessage.getFile().setUrl(Uri.fromFile(file).toString());

        sendFileInterface.onReady(fileMessage);

        uploadFile(id, file, new Callbacks() {
            @Override
            public void onSuccess(String download_url, String path) {
                fileMessage.getFile().setUrl(download_url);
                fileMessage.getFile().setPath(path);

                collectionReference.document(id).set(fileMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            sendFileInterface.onSent(fileMessage, task.getException());
                        }
                        else {
                            sendFileInterface.onSent(fileMessage, null);
                        }
                    }
                });
            }
            @Override
            public void onProgress(int percentage) {
                sendFileInterface.onProgress(fileMessage, percentage);
            }

            @Override
            public void onFailure(Exception e) {
                sendFileInterface.onSent(fileMessage, e);
            }
        });
    }

    @Override
    public void sendLocation(Double lat, Double lng, final MessageSendCallback messageSendCallback) {

        final CollectionReference collectionReference =  getDb().collection("channels/" + getChannelId() + "/messages");
        final String id = collectionReference.document().getId();

        LocationMessage locationMessage = new LocationMessage();
        locationMessage.build(getUserId());
        locationMessage.setLocation(new GeoPoint(lat, lng));
        locationMessage.setId(id);

        messageSendCallback.sent(locationMessage);

        collectionReference.document(id).set(locationMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    messageSendCallback.onStatusUpdate(id, DeliveryReceiptStatus.FAILED);
                }
            }
        });
    }

    @Override
    public void destroy() {
        super.destroy();
        messageService = null;
    }

    /**
     **/
    public static MessageService instance(String channel_id) {
        if (messageService == null)
            messageService = new MessageService(channel_id);
        return messageService;
    }
}
