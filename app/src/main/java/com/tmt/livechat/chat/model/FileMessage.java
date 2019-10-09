package com.tmt.livechat.chat.model;


import com.google.firebase.firestore.Exclude;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public class FileMessage extends BaseMessage {

    private FileData file = null;


    public void setFile(FileData file) {
        this.file = file;
    }
    public FileData getFile() {
        return file;
    }


    @Exclude
    public String getFile_type() {
        return file.getFile_type();
    }

    @Exclude
    public String getUrl() {
        return file.getUrl();
    }

    @Exclude
    public String getLabel() {
        return file.getLabel();
    }

    @Exclude
    public String getPath() {
        return file.getPath();
    }

    @Exclude
    public int getProgress() {
        return file.getProgress();
    }

    @Exclude
    public String getFile_uri() {
        return file.getFile_uri();
    }

}
