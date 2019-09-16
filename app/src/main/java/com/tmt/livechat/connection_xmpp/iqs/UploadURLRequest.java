package com.tmt.livechat.connection_xmpp.iqs;

import com.tmt.livechat.connection_xmpp.manegers.S3FileUploadManager;
import org.jivesoftware.smack.packet.IQ;
import org.jxmpp.jid.impl.JidCreate;

/**
 * Created by mohammednabil on 2019-09-05.
 */
public class UploadURLRequest extends IQ {

    public final static String childElementName = "query";
    public final static String childElementNamespace = S3FileUploadManager.NAMESPACE;

    String md5;


    public UploadURLRequest(String domain, String md5) {
        super(childElementName,childElementNamespace);
        this.md5 = md5;
        this.setType( IQ.Type.set );
        try {
            this.setTo(JidCreate.domainBareFrom(domain));
        }
        catch (Exception e){}
    }



    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.attribute("md5", md5);
        xml.rightAngleBracket();
        return xml;
    }
}
