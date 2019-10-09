package com.tmt.livechat.screens.chat.viewbinders;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.tmt.livechat.R;
import com.tmt.livechat.chat.clients.firestore.service.filedownload.FileDownloadService;
import com.tmt.livechat.chat.clients.firestore.service.filedownload.FileDownloadServiceInterface;
import com.tmt.livechat.chat.model.BaseMessage;
import com.tmt.livechat.chat.model.FileMessage;
import com.tmt.livechat.utils.AudioPlayerUtils;
import com.tmt.livechat.utils.DateUtils;
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

    public OtherAudioMessageHolder(View itemView) {
        super(itemView);
        mainLayout = itemView.findViewById(R.id.main_parent);
        timer = itemView.findViewById(R.id.tvTimer);
        mediaButton = itemView.findViewById(R.id.ivMedia);
        seekBar = itemView.findViewById(R.id.seek);
        timeText = itemView.findViewById(R.id.text_group_chat_time);
    }

    public void bind(final Context context, final BaseMessage message) {
        timeText.setText(message.getPosted_at() != null ? DateUtils.formatTime(message.getTimeInMillis()) : "");
        timeText.setTextColor(Color.parseColor("#9b9b9b"));
        mediaButton.setTag("aud_" + message.getId());
        seekBar.setEnabled(false);
        FileDownloadService.instance().downloadAndPrepare(context, (FileMessage) message, new FileDownloadServiceInterface.Callbacks() {
            @Override
            public void onFileReady(final File file) {
                AudioPlayerUtils.setup(context, mediaButton, timer, seekBar, file);
            }
        });
    }
}