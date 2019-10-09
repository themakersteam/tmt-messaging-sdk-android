package com.tmt.livechat.chat.clients.firestore.service.user_status.model;

import com.google.firebase.Timestamp;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public class UserStatus {

    private Status status = null;

    public UserStatus() {

    }

    public UserStatus(boolean online, Timestamp updated_at) {
        status = new Status(online, updated_at);
    }


    public void setStatus(Status status) {
        this.status = status;
    }
    public Status getStatus() {
        return status;
    }

    class Status {
        private Boolean online = null;
        private Timestamp updated_at = null;

        public Status() {

        }

        public Status(boolean online, Timestamp updated_at) {
            this.online = online;
            this.updated_at = updated_at;
        }

        /**
         *
         **/
        public void setOnline(Boolean online) {
            this.online = online;
        }

        public Boolean getOnline() {
            return online;
        }

        /**
         *
         **/
        public void setUpdated_at(Timestamp updated_at) {
            this.updated_at = updated_at;
        }

        public Timestamp getUpdated_at() {
            return updated_at;
        }
    }
}
