package com.tmt.livechat.chat.clients.firestore.service.fileupload;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tmt.livechat.chat.clients.firestore.service.abstraction.AbstractService;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public class FileUploadService extends AbstractService implements FileUploadServiceInterface {

    private static FileUploadService fileUploadService;

    public FileUploadService(String channel_id) {
        super(channel_id);
    }

    @Override
    public void uploadFile(final String id, File file, final Callbacks callbacks) {
        String date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
        try {
            InputStream stream = new FileInputStream(file);
            final StorageReference reference =  getStorage().getReference("channel_files/" + date + "/" + getChannelId() + "/" + getUserId() + "/" + id);
            UploadTask uploadTask = reference.putStream(stream);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callbacks.onFailure(e);
                }
            });
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    int percent = (int)(taskSnapshot.getBytesTransferred() * 100) / (int)(taskSnapshot.getTotalByteCount() - taskSnapshot.getBytesTransferred());
                    callbacks.onProgress(Math.abs(percent));
                }
            });
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        callbacks.onFailure(task.getException());
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        callbacks.onSuccess(downloadUri.toString(), reference.getPath());
                    }
                    else {
                        callbacks.onFailure(task.getException());
                    }
                }
            });
        }
        catch (Exception e){
            callbacks.onFailure(e);
        }
    }

    @Override
    public void destroy() {
        fileUploadService = null;
    }

    /**
     **/
    public static FileUploadService instance(String channel_id) {
        if (fileUploadService == null)
            fileUploadService = new FileUploadService(channel_id);
        return fileUploadService;
    }
}
