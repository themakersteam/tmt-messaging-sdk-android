package com.tmt.messagecenter.screens.chat.viewbinders;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dinuscxj.progressbar.CircleProgressBar;
import com.tmt.messagecenter.R;
import com.tmt.messagecenter.model.UserMessage;
import com.tmt.messagecenter.screens.chat.adapters.MessageCenterChatAdapter;
import com.tmt.messagecenter.utils.DateUtils;
import com.tmt.messagecenter.utils.DownloadUtils;
import com.tmt.messagecenter.utils.ImageUtils;
import java.io.File;

/**
 * Created by mohammednabil on 2019-09-16.
 */
public class MyImageFileMessageHolder extends BaseViewBinder {
    RelativeLayout mainLayout;
    TextView timeText, caption;
    ImageView fileThumbnailImage, readReceipt;
    CircleProgressBar circleProgressBar;
    private DownloadUtils downloadUtils;

    public MyImageFileMessageHolder(View itemView, DownloadUtils downloadUtils) {
        super(itemView);
        this.downloadUtils = downloadUtils;
        mainLayout =  itemView.findViewById(R.id.main_parent);
        timeText =  itemView.findViewById(R.id.text_group_chat_time);
        fileThumbnailImage =  itemView.findViewById(R.id.image_group_chat_file_thumbnail);
        readReceipt = itemView.findViewById(R.id.img_group_chat_read_receipt);
        circleProgressBar = itemView.findViewById(R.id.circle_progress);
        caption = itemView.findViewById(R.id.text_caption);

    }

    public void bind(final Context context, final UserMessage message, final MessageCenterChatAdapter.OnItemClickListener listener) {
        timeText.setText(message.getTimeStamp() != null ? DateUtils.formatTime(message.getTimeStamp()) : "");
        timeText.setTextColor(Color.parseColor("#9b9b9b"));

        circleProgressBar.setVisibility(message.getProgress() < 100 ? View.VISIBLE : View.GONE);
        circleProgressBar.setProgress(message.getProgress());

        caption.setVisibility(message.getFile().getCaption() != null && !message.getFile().getCaption().isEmpty() ? View.VISIBLE : View.GONE);
        caption.setText(message.getFile().getCaption());

        processReadReceipt(context, readReceipt, timeText, message);

        downloadUtils.downloadFile(message.getFile().getId(), new DownloadUtils.DownloadCallBacks() {
            @Override
            public void onFileReady(File file) {
                ImageUtils.displayImageFromFile(context, file, fileThumbnailImage, null);
            }
        });

        if (listener != null) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onUserMessageItemClick(message);
                }
            });
        }
    }
}
