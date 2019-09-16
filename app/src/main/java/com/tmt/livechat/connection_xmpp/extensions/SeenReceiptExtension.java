package com.tmt.livechat.connection_xmpp.extensions;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;
import org.jivesoftware.smack.util.XmlStringBuilder;
import java.util.List;
import java.util.Map;

/**
 * Created by mohammednabil on 2019-09-11.
 */
public class SeenReceiptExtension implements ExtensionElement {

    public final static String ELEMENT = "seen";
    public final static String NAMESPACE = "messagecenter:seen";

    public SeenReceiptExtension() {}


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
        xml.closeEmptyElement();
        return xml;
    }


    public static class Provider extends EmbeddedExtensionProvider<SeenReceiptExtension> {
        @Override
        protected SeenReceiptExtension createReturnExtension(String currentElement, String currentNamespace, Map<String, String> attributeMap, List<? extends ExtensionElement> content) {
            SeenReceiptExtension repExt = new SeenReceiptExtension();
            return repExt;
        }
    }

}
