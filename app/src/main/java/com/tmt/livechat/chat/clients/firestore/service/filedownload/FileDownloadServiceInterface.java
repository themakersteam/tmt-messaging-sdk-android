package com.tmt.livechat.chat.clients.firestore.service.filedownload;


import android.content.Context;
import com.tmt.livechat.chat.model.FileMessage;
import java.io.File;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public interface FileDownloadServiceInterface {

    void downloadAndPrepare(Context context, FileMessage fileMessage, Callbacks callbacks);
    File getFileFromCache(Context context, String id);
    void clearFilesCache(Context context);

    interface Callbacks {
        void onFileReady(File file);
    }
}
