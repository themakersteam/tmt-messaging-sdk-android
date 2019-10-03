package com.tmt.livechat.connection_xmpp.network.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mohammednabil on 2019-10-01.
 */
public class SendNotificationBody {

    @SerializedName("id")
    private String id = null;
    @SerializedName("message")
    private String message = null;
    @SerializedName("message_at")
    private Long message_at = null;
    @SerializedName("type")
    private String type = null;
    @SerializedName("room_id")
    private String room_id = null;
    @SerializedName("platform")
    private String platform = null;

    /**
     **/
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    /**
     **/
    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    /**
     **/
    public void setMessageAt(Long message_at) {
        this.message_at = message_at;
    }
    public Long getMessageAt() {
        return message_at;
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
    public void setRoomId(String room_id) {
        this.room_id = room_id;
    }
    public String getRoomId() {
        return room_id;
    }

    /**
     **/
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    public String getPlatform() {
        return platform;
    }
}
