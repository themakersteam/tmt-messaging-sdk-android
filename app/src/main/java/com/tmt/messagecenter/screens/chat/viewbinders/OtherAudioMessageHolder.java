package com.tmt.messagecenter.screens.chat.viewbinders;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.tmt.messagecenter.R;
import com.tmt.messagecenter.model.UserMessage;
import com.tmt.messagecenter.utils.AudioPlayerUtils;
import com.tmt.messagecenter.utils.DateUtils;
import com.tmt.messagecenter.utils.DownloadUtils;

import java.io.File;

/**
 * Created by mohammednabil on 2019-09-16.
 */
public class OtherAudioMessageHolder extends BaseViewBinder {
    RelativeLayout mainLayout;
    TextView timer;
    ImageView mediaButton;
    TextView timeText;
    SeekBar seekBar;
    private DownloadUtils downloadUtils;

    public OtherAudioMessageHolder(View itemView, DownloadUtils downloadUtils) {
        super(itemView);
        this.downloadUtils = downloadUtils;
        mainLayout = itemView.findViewById(R.id.main_parent);
        timer = itemView.findViewById(R.id.tvTimer);
        mediaButton = itemView.findViewById(R.id.ivMedia);
        seekBar = itemView.findViewById(R.id.seek);
        timeText = itemView.findViewById(R.id.text_group_chat_time);
    }

    public void bind(final Context context, final UserMessage message) {
        timeText.setText(message.getTimeStamp() != null ? DateUtils.formatTime(message.getTimeStamp()) : "");
        timeText.setTextColor(Color.parseColor("#9b9b9b"));
        mediaButton.setTag(message.getFile().getId());
        seekBar.setEnabled(false);
        downloadUtils.downloadFile(message.getFile().getId(), new DownloadUtils.DownloadCallBacks() {
            @Override
            public void onFileReady(final File file) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        AudioPlayerUtils.setup(context, mediaButton, timer, seekBar, file);
                    }
                });
            }
        });
    }
}