package com.tmt.messagecenter.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mohammednabil on 2019-09-02.
 */
public class ConnectionRequest implements Parcelable {

    private String username;
    private String password;
    private String domain;

    public ConnectionRequest() {

    }

    public ConnectionRequest(String username, String password, String domain) {
        this.username = username;
        this.password = password;
        this.domain = domain;
    }

    public ConnectionRequest(Parcel in){
        this.username = in.readString();
        this.password = in.readString();
        this.domain = in.readString();
    }

    /**
     **/
    public void setUsername(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }

    /**
     **/
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }

    /**
     **/
    public void setDomain(String domain) {
        this.domain = domain;
    }
    public String getDomain() {
        return domain;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.username);
        dest.writeString(this.password);
        dest.writeString(this.domain);
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
