package com.tmt.livechat.chat.model;

import com.google.firebase.firestore.GeoPoint;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public class LocationMessage extends BaseMessage {

    private GeoPoint location = null;


    public void setLocation(GeoPoint location) {
        this.location = location;
    }
    public GeoPoint getLocation() {
        return location;
    }

}
