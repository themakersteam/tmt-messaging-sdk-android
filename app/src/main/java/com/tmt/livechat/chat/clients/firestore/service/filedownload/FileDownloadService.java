package com.tmt.livechat.chat.clients.firestore.service.filedownload;

import android.content.Context;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.tmt.livechat.chat.clients.firestore.service.abstraction.AbstractService;
import com.tmt.livechat.chat.model.FileMessage;
import java.io.File;
import java.util.HashMap;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public class FileDownloadService extends AbstractService implements FileDownloadServiceInterface {

    private HashMap<String, String> data;

    private static FileDownloadService fileDownloadService;

    public FileDownloadService() {
        data = new HashMap<>();
    }

    @Override
    public void downloadAndPrepare(final Context context, final FileMessage fileMessage, final Callbacks callbacks) {
        PRDownloader.initialize(context);
        File temp = getFileFromCache(context, "aud_" + fileMessage.getId());
        if (temp != null)
            callbacks.onFileReady(temp);
        if (!data.containsKey(fileMessage.getId())) {
            try {
                data.put(fileMessage.getId(), fileMessage.getId());
                String download_url = fileMessage.getUrl();
                PRDownloader.download(download_url, context.getCacheDir().getPath(), "aud_" +fileMessage.getId()).build().start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        callbacks.onFileReady(getFileFromCache(context, "aud_" + fileMessage.getId()));
                    }

                    @Override
                    public void onError(Error error) {
                        data.remove(fileMessage.getId());
                    }
                });
            }
            catch (Exception e){
                data.remove(fileMessage.getId());
            }
        }
    }

    @Override
    public File getFileFromCache(Context context, String id) {
        File[] files = context.getCacheDir().listFiles();
        for (File f : files) {
            if (f.getName().equals(id))
                return f;
        }
        return null;
    }


    @Override
    public void clearFilesCache(Context context) {
        try {
            File[] files = context.getCacheDir().listFiles();
            for (File file : files) {
                if (file.getName().startsWith("aud_")) {
                    file.delete();
                }
            }
        } catch (Exception e) {}
    }

    @Override
    public void destroy() {
        fileDownloadService = null;
    }

    /**
     **/
    public static FileDownloadService instance() {
        if (fileDownloadService == null)
            fileDownloadService = new FileDownloadService();
        return fileDownloadService;
    }
}
