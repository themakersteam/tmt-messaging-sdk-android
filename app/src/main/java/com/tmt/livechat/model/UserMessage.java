package com.tmt.livechat.model;

import com.tmt.livechat.connection_xmpp.constants.DeliveryReceiptStatus;
import com.tmt.livechat.connection_xmpp.constants.MessageTypeStatus;
import com.tmt.livechat.connection_xmpp.extensions.DataExtension;
import com.tmt.livechat.connection_xmpp.extensions.FileExtension;
import com.tmt.livechat.connection_xmpp.extensions.LocationExtension;
import com.tmt.livechat.connection_xmpp.network.requests.SendNotificationBody;

import org.jivesoftware.smack.packet.Message;

/**
 * Created by mohammednabil on 2019-09-01.
 */
public class UserMessage {

    private Message message;

    private int progress = 100;
    private String status = DeliveryReceiptStatus.IN_PROGRESS;


    public UserMessage(){}

    public UserMessage(Message message) {
        this.message = message;
    }

    /**
     **/
    public void setMessage(Message message) {
        this.message = message;
    }
    public Message getMessage() {
        return message;
    }


    public Long getTimeStamp() {
        return ((DataExtension)message.getExtension(DataExtension.NAMESPACE)) != null ? ((DataExtension)message.getExtension(DataExtension.NAMESPACE)).getTimestamp() : 0;
    }

    public boolean isLocation() {
        return ((DataExtension)message.getExtension(DataExtension.NAMESPACE)) != null && ((DataExtension)message.getExtension(DataExtension.NAMESPACE)).getLocation() != null;
    }

    public LocationExtension getLocation() {
        return ((DataExtension)message.getExtension(DataExtension.NAMESPACE)).getLocation();
    }

    public boolean isFile() {
        return (message.getExtension(DataExtension.NAMESPACE)) != null && ((DataExtension)message.getExtension(DataExtension.NAMESPACE)).getFile() != null;
    }

    public FileExtension getFile() {
        return ((DataExtension)message.getExtension(DataExtension.NAMESPACE)).getFile();
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    /**
     *
     * @param room_id
     * @return
     */
    public SendNotificationBody getNotificationRequest(String room_id) {
        SendNotificationBody sendNotificationBody = new SendNotificationBody();
        sendNotificationBody.setRoomId(room_id);
        sendNotificationBody.setId(message != null && message.getStanzaId() != null ? message.getStanzaId() : null);
        sendNotificationBody.setMessageAt(getTimeStamp());
        sendNotificationBody.setPlatform("android");
        if (!isFile() && !isLocation()) {
            sendNotificationBody.setMessage(message.getBody());
            sendNotificationBody.setType(MessageTypeStatus.TEXT);
        }
        else if (isLocation()) {
            sendNotificationBody.setType(MessageTypeStatus.LOCATION);
        }
        else if (isFile()) {
            if (getFile().getContentType().contains("image"))
                sendNotificationBody.setType(MessageTypeStatus.IMAGE);
            else if (getFile().getContentType().contains("3gp") || getFile().getContentType().contains("audio"))
                sendNotificationBody.setType(MessageTypeStatus.AUDIO);
        }
        return sendNotificationBody;
    }
}
