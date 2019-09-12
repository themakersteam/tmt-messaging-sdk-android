package com.tmt.messagecenter.connection_xmpp.extensions;


import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.StandardExtensionElement;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;
import org.jivesoftware.smack.util.XmlStringBuilder;

import java.util.List;
import java.util.Map;

/**
 * Created by mohammednabil on 2019-09-02.
 */
public class DataExtension implements ExtensionElement {

    public static final String NAMESPACE = "messagecenter";
    public static final String ELEMENT = "data";

    private Long timestamp = null;
    private LocationExtension location;
    private FileExtension file;



    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    public Long getTimestamp() {
        return timestamp;
    }

    public void setLocation(LocationExtension location) {
        this.location = location;
    }
    public LocationExtension getLocation() {
        return location;
    }

    public void setFile(FileExtension file) {
        this.file = file;
    }
    public FileExtension getFile() {
        return file;
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
        xml.attribute("timestamp", getTimestamp() != null ? getTimestamp().toString() : null);
        xml.append(">");
        if (location != null)
            xml.element(location);
        if (file != null)
            xml.element(file);
        xml.closeElement("data");
        return xml;
    }

    public static class Provider extends EmbeddedExtensionProvider<DataExtension> {
        @Override
        protected DataExtension createReturnExtension(String currentElement, String currentNamespace, Map<String, String> attributeMap, List<? extends ExtensionElement> content) {
            DataExtension repExt = new DataExtension();
            repExt.setTimestamp(Long.parseLong(attributeMap.get("timestamp")));
            for (ExtensionElement e : content) {
                if (e.getNamespace().equals(LocationExtension.NAMESPACE)) {
                    StandardExtensionElement see = (StandardExtensionElement) e;
                    repExt.setLocation(new LocationExtension(see.getAttributeValue("latitude"), see.getAttributeValue("longitude")));
                }
                else if (e.getNamespace().equals(FileExtension.NAMESPACE)) {
                    StandardExtensionElement see = (StandardExtensionElement) e;
                    FileExtension fileExtension = new FileExtension(see.getAttributeValue("id"), see.getAttributeValue("content-type"));
                    for (StandardExtensionElement f : see.getElements()) {
                        fileExtension.setCaption(f.getText());
                    }
                    repExt.setFile(fileExtension);
                }
            }
            return repExt;
        }
    }


}
