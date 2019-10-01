package com.tmt.livechat.connection_xmpp.network.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mohammednabil on 2019-10-01.
 */
public class SendNotificationBody {

    @SerializedName("message")
    private String message = null;
    @SerializedName("type")
    private String type = null;
    @SerializedName("room_id")
    private String room_id = null;

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
}
