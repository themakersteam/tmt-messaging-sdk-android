package com.tmt.livechat.connection_xmpp.extensions;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.util.XmlStringBuilder;

/**
 * Created by mohammednabil on 2019-09-09.
 */
public class LocationExtension implements ExtensionElement {

    public static final String NAMESPACE = "messagecenter:location";
    public static final String ELEMENT = "location";

    private String lat,lng;


    public LocationExtension(String lat, String lng){
        this.lat = lat;
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public CharSequence toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder(this);
        xml.attribute("latitude", lat);
        xml.attribute("longitude", lng);
        xml.closeEmptyElement();
        return xml;
    }

}
