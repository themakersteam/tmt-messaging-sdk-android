package com.tmt.livechat.chat.model;

/**
 * Created by mohammednabil on 2019-10-06.
 */
public class TextMessage extends BaseMessage {

    private String text = null;

    /**
     **/
    public void setText(String text) {
        this.text = text;
    }
    public String getText() {
        return text;
    }
}
