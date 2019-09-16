package com.tmt.livechat.connection_xmpp.manegers;

import android.content.Context;
import android.util.Base64;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import com.tmt.livechat.connection_xmpp.extensions.DataExtension;
import com.tmt.livechat.connection_xmpp.extensions.FileExtension;
import com.tmt.livechat.connection_xmpp.interfaces.SendFileInterface;
import com.tmt.livechat.connection_xmpp.network.requests.ProgressRequestBody;
import com.tmt.livechat.connection_xmpp.iqs.UploadURLRequest;
import com.tmt.livechat.connection_xmpp.network.XmppNetwork;
import com.tmt.livechat.model.UserMessage;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.IQResultReplyFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mohammednabil on 2019-09-05.
 */
public class S3FileUploadManager extends Manager {

    public static final String NAMESPACE = "p1:s3filetransfer";
    private static final Map<XMPPConnection, S3FileUploadManager> INSTANCES = new WeakHashMap<>();
    private boolean s3ServiceDescovered = false;

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            @Override
            public void connectionCreated(XMPPConnection connection) {
                getInstanceFor(connection);
            }
        });
    }

    public static synchronized S3FileUploadManager getInstanceFor(XMPPConnection connection) {
        S3FileUploadManager s3FileUploadManeger = INSTANCES.get(connection);

        if (s3FileUploadManeger == null) {
            s3FileUploadManeger = new S3FileUploadManager(connection);
            INSTANCES.put(connection, s3FileUploadManeger);
        }

        return s3FileUploadManeger;
    }


    private S3FileUploadManager(XMPPConnection connection) {
        super(connection);
        try {
            discovers3Service();
        } catch (XMPPException.XMPPErrorException | SmackException.NotConnectedException
                | SmackException.NoResponseException | InterruptedException e) {
        }
    }


    public boolean discovers3Service() throws XMPPException.XMPPErrorException, SmackException.NotConnectedException,
            InterruptedException, SmackException.NoResponseException {
        ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(connection());
        List<DiscoverInfo> servicesDiscoverInfo = sdm.findServicesDiscoverInfo(NAMESPACE, true, true);
        s3ServiceDescovered = !servicesDiscoverInfo.isEmpty();
        return s3ServiceDescovered;
    }

    public boolean isServiceDiscovered() {
        return s3ServiceDescovered;
    }


    public void sendFile(final Context context, final MultiUserChat chat, final File file, final String caption, final SendFileInterface sendFileInterface) throws Exception {
        final String md5Base64 = md5(file);
        UploadURLRequest uploadURLRequest = new UploadURLRequest(connection().getHost(), md5Base64);
        connection().sendStanzaWithResponseCallback(uploadURLRequest, new IQResultReplyFilter(uploadURLRequest, connection()), new StanzaListener() {
            @Override
            public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {
                if (packet.getError() != null) {
                    sendFileInterface.onSent(null, new Exception(packet.getError().toXML().toString()));
                }
                else {
                    String url = getUrlFromXml(packet.toXML(null).toString());
                    url = url.replaceAll("AWSAccessKeyId", "GoogleAccessId");
                    final String id = getIdFromXml(packet.toXML(null).toString());
                    final Message message = new Message();
                    message.setBody("<empty>");
                    DataExtension dataExtension = new DataExtension();
                    dataExtension.setTimestamp(System.currentTimeMillis());
                    dataExtension.setFile(new FileExtension(id, getMimeType(file), caption));
                    message.addExtension(dataExtension);
                    copyFile(file, new File(context.getCacheDir(), id));
                    sendFileInterface.onReady(new UserMessage(message));
                    XmppNetwork.instance().uploadImage(file, md5Base64, url, new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                try {
                                    chat.sendMessage(message);
                                }
                                catch (Exception e) {
                                    sendFileInterface.onSent(null, new Exception("CHAT IS NOT VALID"));
                                }
                                sendFileInterface.onSent(new UserMessage(message), null);
                            }
                            else {
                                try {
                                    sendFileInterface.onSent(null, new Exception(response.errorBody().string()));
                                } catch (Exception e) {
                                    sendFileInterface.onSent(null, new Exception("IO EXCEPTION"));
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            sendFileInterface.onSent(null, new Exception(t));
                        }
                    }, new ProgressRequestBody.UploadCallbacks() {
                        @Override
                        public void onProgressUpdate(int percentage) {
                            sendFileInterface.onProgress(new UserMessage(message), percentage);
                        }
                    });
                }
            }
        });
    }

    private String md5(File file) throws Exception {
        byte[] val = DigestUtils.md5(new FileInputStream(file));
        return Base64.encodeToString(val, Base64.DEFAULT).trim();
    }

    private String getUrlFromXml(String xml) {
        Document doc = Jsoup.parse(xml);
        return StringEscapeUtils.unescapeHtml4(StringEscapeUtils.unescapeXml(doc.selectFirst("query")
                .attr("upload")
                .replaceAll("'",""))
                .replaceAll("/></query", ""));
    }

    private String getIdFromXml(String xml) {
        Document doc = Jsoup.parse(xml);
        return StringEscapeUtils.unescapeHtml4(StringEscapeUtils.unescapeXml(doc.selectFirst("query")
                .attr("fileid")
                .replaceAll("'",""))
                .replaceAll("/></query", ""));
    }

    public void copyFile(File src, File dst) {
        try {
            try (InputStream in = new FileInputStream(src)) {
                try (OutputStream out = new FileOutputStream(dst)) {
                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                }
            }
        }
        catch (IOException e){}
    }

    @NonNull
    private String getMimeType(@NonNull File file) {
        String type = null;
        final String url = file.toString();
        final String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }
        if (type == null) {
            type = "*/*"; // fallback type. You might set it to */*
        }
        return type;
    }

}
