package com.tmt.livechat.chat.model;

import com.google.firebase.firestore.Exclude;

/**
 * Created by mohammednabil on 2019-10-08.
 */
public class FileData {

    private String file_type = null;
    private String url = null;
    private String label = null;
    private String path = null;

    @Exclude
    private int progress = 100;

    @Exclude
    private String file_uri;

    /**
     **/
    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }
    public String getFile_type() {
        return file_type;
    }

    /**
     **/
    public void setUrl(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }

    /**
     **/
    public void setLabel(String label) {
        this.label = label;
    }
    public String getLabel() {
        return label;
    }

    /**
     **/
    public void setPath(String path) {
        this.path = path;
    }
    public String getPath() {
        return path;
    }

    /**
     **/
    public void setProgress(int progress) {
        this.progress = progress;
    }
    @Exclude
    public int getProgress() {
        return progress;
    }

    /**
     **/
    public void setFile_uri(String file_uri) {
        this.file_uri = file_uri;
    }
    @Exclude
    public String getFile_uri() {
        return file_uri;
    }

}
