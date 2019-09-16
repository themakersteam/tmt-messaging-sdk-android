package com.tmt.livechat.connection_xmpp.iqs;

import com.tmt.livechat.connection_xmpp.manegers.S3FileUploadManager;

import org.jivesoftware.smack.packet.IQ;
import org.jxmpp.jid.impl.JidCreate;

/**
 * Created by mohammednabil on 2019-09-05.
 */
public class DownloadUrlRequest extends IQ {

    public final static String childElementName = "query";
    public final static String childElementNamespace = S3FileUploadManager.NAMESPACE;

    String fileid;


    public DownloadUrlRequest(String domain, String md5) {
        super(childElementName,childElementNamespace);
        this.fileid = md5;
        this.setType( Type.get);
        try {
            this.setTo(JidCreate.domainBareFrom(domain));
        }
        catch (Exception e){}
    }



    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.attribute("fileid", fileid);
        xml.rightAngleBracket();
        return xml;
    }
}
