package com.tmt.livechat.utils;

import android.content.Context;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.tmt.livechat.client.Livechat;
import com.tmt.livechat.connection_xmpp.iqs.DownloadUrlRequest;
import org.apache.commons.text.StringEscapeUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.filter.IQResultReplyFilter;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.File;
import java.util.HashMap;

/**
 * Created by mohammednabil on 2019-09-11.
 */
public class DownloadUtils {

    private Context context;

    private HashMap<String, String> data;

    public DownloadUtils(Context context) {
        this.context = context;
        PRDownloader.initialize(context);
        data = new HashMap<>();
    }

    public void downloadFile(final String id, final DownloadCallBacks downloadCallBacks) {
        File temp = getFileFromCache(context, id);
        if (temp != null)
            downloadCallBacks.onFileReady(temp);
        if (!data.containsKey(id)) {
            data.put(id, id);
            XMPPTCPConnection connection = Livechat.instance().getConnection();
            DownloadUrlRequest downloadUrlRequest = new DownloadUrlRequest(connection.getHost(), id);
            try {
                connection.sendStanzaWithResponseCallback(downloadUrlRequest, new IQResultReplyFilter(downloadUrlRequest, connection), new StanzaListener() {
                    @Override
                    public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {
                        if (packet.getError() != null)
                            data.remove(id);
                        else {
                            String download_url = getEscapedDownloadUrl(packet.toXML(null).toString());
                            PRDownloader.download(download_url, context.getCacheDir().getPath(), id).build().start(new OnDownloadListener() {
                                @Override
                                public void onDownloadComplete() {
                                    downloadCallBacks.onFileReady(getFileFromCache(context, id));
                                }

                                @Override
                                public void onError(Error error) {
                                    data.remove(id);
                                }
                            });
                        }

                    }
                });
            } catch (Exception e) {
                data.remove(id);
            }
        }
    }

    public static File getFileFromCache(Context context, String id) {
        File[] files = context.getCacheDir().listFiles();
        for (File f : files) {
            if (f.getName().equals(id))
                return f;
        }
        return null;
    }

    private String getEscapedDownloadUrl(String xml) {
        Document doc = Jsoup.parse(xml);
        return StringEscapeUtils.unescapeHtml4(StringEscapeUtils.unescapeXml(doc.selectFirst("query")
                .attr("download")
                .replaceAll("'", ""))
                .replaceAll("/></query", ""));
    }

    public interface DownloadCallBacks {
        void onFileReady(File file);
    }
}
