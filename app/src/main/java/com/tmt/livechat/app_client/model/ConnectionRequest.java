package com.tmt.livechat.app_client.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mohammednabil on 2019-09-02.
 */
public class ConnectionRequest implements Parcelable {

    private String authToken;

    public ConnectionRequest(String authToken) {
        this.authToken = authToken;
    }

    public ConnectionRequest(Parcel in){
        this.authToken = in.readString();
    }

    /**
     **/
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    public String getAuthToken() {
        return authToken;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.authToken);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ConnectionRequest createFromParcel(Parcel in) {
            return new ConnectionRequest(in);
        }

        public ConnectionRequest[] newArray(int size) {
            return new ConnectionRequest[size];
        }
    };

}
