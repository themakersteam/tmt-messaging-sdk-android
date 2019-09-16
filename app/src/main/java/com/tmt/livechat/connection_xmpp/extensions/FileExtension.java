package com.tmt.livechat.connection_xmpp.extensions;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.util.XmlStringBuilder;

/**
 * Created by mohammednabil on 2019-09-09.
 */
public class FileExtension implements ExtensionElement {

    public static final String NAMESPACE = "messagecenter:file";
    public static final String ELEMENT = "file";

    private String id;
    private String content_type;
    private String caption;

    public FileExtension(String id, String content_type){
        this.id = id;
        this.content_type = content_type;
    }

    public FileExtension(String id, String content_type, String caption){
        this.id = id;
        this.content_type = content_type;
        this.caption = caption;
    }


    public String getId() {
        return id;
    }

    public String getContentType() {
        return content_type;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
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
        xml.attribute("id", id);
        xml.attribute("content-type", content_type);
        xml.append(">");
        if (caption != null && !caption.isEmpty())
            xml.element("caption", caption);
        xml.closeElement(ELEMENT);
        return xml;
    }

}
