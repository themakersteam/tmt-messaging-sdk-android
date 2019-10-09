package com.tmt.livechat.app_client.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Theme implements Parcelable {

    private String app_name = null;
    private String toolbar_title = null;
    private String toolbar_subtitle = null;
    private String welcome_message = null;

    //Empty  Constructor
    public Theme() {
    }

    //Theme Constructor
    public Theme(String app_name, String toolbar_title, String toolbar_subtitle, String welcome_message) {
        this.app_name = app_name;
        this.toolbar_title = toolbar_title;
        this.toolbar_subtitle = toolbar_subtitle;
        this.welcome_message = welcome_message;
    }

    //Parcelable Constructor
    public Theme(Parcel in){
        this.app_name = in.readString();
        this.toolbar_title = in.readString();
        this.toolbar_subtitle = in.readString();
        this.welcome_message =  in.readString();
    }

    public void setAppName(String app_name) {
        this.app_name = app_name;
    }
    public String getAppName() {
        return app_name;
    }

    /**
     **/
    public void setToolbarTitle(String toolbar_title) {
        this.toolbar_title = toolbar_title;
    }
    public String getToolbarTitle() {
        return toolbar_title;
    }


    /**
     **/
    public void setToolbarSubtitle(String toolbar_subtitle) {
        this.toolbar_subtitle = toolbar_subtitle;
    }
    public String getToolbarSubtitle() {
        return toolbar_subtitle;
    }

    /**
     **/
    public void setWelcomeMessage(String welcome_message) {
        this.welcome_message = welcome_message;
    }
    public String getWelcomeMessage() {
        return welcome_message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.app_name);
        dest.writeString(this.toolbar_title);
        dest.writeString(this.toolbar_subtitle);
        dest.writeString(this.welcome_message);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Theme createFromParcel(Parcel in) {
            return new Theme(in);
        }

        public Theme[] newArray(int size) {
            return new Theme[size];
        }
    };
}
