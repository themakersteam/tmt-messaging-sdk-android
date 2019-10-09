package com.tmt.livechat.chat.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;
import com.tmt.livechat.chat.constants.DeliveryReceiptStatus;
import com.tmt.livechat.chat.clients.firestore.service.message.constants.MessageConstants;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public class BaseMessage {

    @Exclude
    private String id = null;

    private Integer version = null;
    private String type = null;
    private String sender = null;
    private Timestamp posted_at = null;

    @Exclude
    private String status = null;

    @ServerTimestamp
    private Timestamp received_at = null;

    /**
     **/
    public void setId(String id) {
        this.id = id;
    }
    @Exclude
    public String getId() {
        return id;
    }

    /**
     **/
    public void setVersion(Integer version) {
        this.version = version;
    }
    public Integer getVersion() {
        return version;
    }

    /**
     **/
    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }

    /**
     **/
    public void setSender(String sender) {
        this.sender = sender;
    }
    public String getSender() {
        return sender;
    }

    /**
     **/
    public void setPosted_at(Timestamp posted_at) {
        this.posted_at = posted_at;
    }
    public Timestamp getPosted_at() {
        return posted_at;
    }

    /**
     **/
    public void setStatus(String status) {
        this.status = status;
    }
    @Exclude
    public String getStatus() {
        return status;
    }

    /**
     **/
    public void setReceived_at(Timestamp received_at) {
        this.received_at = received_at;
    }
    public Timestamp getReceived_at() {
        return received_at;
    }

    /**
     *
     * @return true if its a file message
     */
    @Exclude
    public boolean isTextMessage() {
        return type.equals(MessageConstants.Type.TEXT);
    }

    /**
     *
     * @return true if its a location message
     */
    @Exclude
    public boolean isLocationMessage() {
        return type.equals(MessageConstants.Type.LOCATION);
    }

    /**
     *
     * @return true if its a file message
     */
    @Exclude
    public boolean isFileMessage() {
        return type.equals(MessageConstants.Type.FILE);
    }


    @Exclude
    public Long getTimeInMillis() {
        return posted_at.toDate().getTime();
    }

    /**
     *
     * @param user_id
     */
    public void build(String user_id) {
        version = 1;
        type = this instanceof LocationMessage ? MessageConstants.Type.LOCATION : this instanceof FileMessage ? MessageConstants.Type.FILE : MessageConstants.Type.TEXT;
        this.sender = user_id;
        this.status = DeliveryReceiptStatus.IN_PROGRESS;
        posted_at = Timestamp.now();
    }
}
