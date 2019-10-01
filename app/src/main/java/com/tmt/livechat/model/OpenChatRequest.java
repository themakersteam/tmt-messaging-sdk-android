package com.tmt.livechat.model;

import android.os.Parcel;
import android.os.Parcelable;

public class OpenChatRequest implements Parcelable {

    private String chat_id = null;
    private String service_url = null;

    public OpenChatRequest(String service_url, String chat_id) {
        this.service_url = service_url;
        this.chat_id = chat_id;
    }

    //Parcelable Constructor
    public OpenChatRequest(Parcel in){
        this.service_url = in.readString();
        this.chat_id = in.readString();
    }

    public String getChatId() {
        return chat_id;
    }

    public String getServiceUrl() {
        return service_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.service_url);
        dest.writeString(this.chat_id);
    }

    public static final Creator CREATOR = new Creator() {
        public OpenChatRequest createFromParcel(Parcel in) {
            return new OpenChatRequest(in);
        }

        public OpenChatRequest[] newArray(int size) {
            return new OpenChatRequest[size];
        }
    };
}
