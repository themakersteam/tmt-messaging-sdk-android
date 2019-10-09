package com.tmt.livechat.chat.clients.firestore.service.channel.model;

import com.google.firebase.Timestamp;

/**
 * Created by mohammednabil on 2019-10-07.
 */
public class ParticipantData {

    private String display_name = null;
    private String avatar = null;
    private Timestamp read_at = null;
    private Timestamp typing_at = null;


    /**
     **/
    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }
    public String getDisplay_name() {
        return display_name;
    }

    /**
     **/
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getAvatar() {
        return avatar;
    }

    /**
     **/
    public void setRead_at(Timestamp read_at) {
        this.read_at = read_at;
    }
    public Timestamp getRead_at() {
        return read_at;
    }

    /**
     **/
    public void setTyping_at(Timestamp typing_at) {
        this.typing_at = typing_at;
    }
    public Timestamp getTyping_at() {
        return typing_at;
    }
}
