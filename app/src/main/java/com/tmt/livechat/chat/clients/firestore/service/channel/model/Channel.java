package com.tmt.livechat.chat.clients.firestore.service.channel.model;

import com.google.firebase.Timestamp;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public class Channel {

    private Integer version = null;
    private String type = null;
    private String status = null;
    private Timestamp opened_at = null;
    private Timestamp closed_at = null;
    private Integer message_count = null;
    private List<String> participants = null;
    private HashMap<String, ParticipantData> participants_data = null;

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
    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

    /**
     **/
    public void setOpened_at(Timestamp opened_at) {
        this.opened_at = opened_at;
    }
    public Timestamp getOpened_at() {
        return opened_at;
    }

    /**
     **/
    public void setClosed_at(Timestamp closed_at) {
        this.closed_at = closed_at;
    }
    public Timestamp getClosed_at() {
        return closed_at;
    }

    /**
     **/
    public void setMessage_count(Integer message_count) {
        this.message_count = message_count;
    }
    public Integer getMessage_count() {
        return message_count;
    }

    /**
     **/
    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
    public List<String> getParticipants() {
        return participants;
    }

    /**
     **/
    public void setParticipants_data(HashMap<String, ParticipantData> participants_data) {
        this.participants_data = participants_data;
    }
    public HashMap<String, ParticipantData> getParticipants_data() {
        return participants_data;
    }


    /**
     *
     * @param user_id
     * @return
     */
    public Timestamp getMyReadAtTimeStamp(String user_id) {
        if (participants_data != null) {
            return participants_data.get(user_id).getRead_at();
        }
        return null;
    }

}
