package com.tmt.messagecenter.model;

import com.tmt.messagecenter.connection_xmpp.constants.DeliveryReceiptStatus;
import com.tmt.messagecenter.connection_xmpp.extensions.DataExtension;
import com.tmt.messagecenter.connection_xmpp.extensions.FileExtension;
import com.tmt.messagecenter.connection_xmpp.extensions.LocationExtension;
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
}
