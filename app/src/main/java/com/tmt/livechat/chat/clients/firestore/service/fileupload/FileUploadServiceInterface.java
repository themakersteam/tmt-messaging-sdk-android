package com.tmt.livechat.chat.clients.firestore.service.fileupload;


import java.io.File;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public interface FileUploadServiceInterface {

    void uploadFile(String id, File file, Callbacks callbacks);

    interface Callbacks {
        void onSuccess(String download_url, String path);
        void onProgress(int percentage);
        void onFailure(Exception e);
    }
}
