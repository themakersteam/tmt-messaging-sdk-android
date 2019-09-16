package com.tmt.livechat.screens.chat.viewbinders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tmt.livechat.R;
import com.tmt.livechat.model.UserMessage;
import com.tmt.livechat.screens.chat.adapters.LiveChatAdapter;
import com.tmt.livechat.utils.DateUtils;
import com.tmt.livechat.utils.DownloadUtils;
import com.tmt.livechat.utils.ImageUtils;
import java.io.File;

/**
 * Created by mohammednabil on 2019-09-16.
 */
public class OtherImageFileMessageHolder extends BaseViewBinder {
    RelativeLayout mainLayout;
    TextView timeText, caption;
    ImageView fileThumbnailImage;
    private DownloadUtils downloadUtils;

    public OtherImageFileMessageHolder(View itemView, DownloadUtils downloadUtils) {
        super(itemView);
        this.downloadUtils = downloadUtils;
        mainLayout = itemView.findViewById(R.id.main_parent);
        timeText = itemView.findViewById(R.id.text_group_chat_time);
        fileThumbnailImage = itemView.findViewById(R.id.image_group_chat_file_thumbnail);
        caption = itemView.findViewById(R.id.text_caption);
    }

    public void bind(final Context context, final UserMessage message, final LiveChatAdapter.OnItemClickListener listener) {
        timeText.setText(message.getTimeStamp() != null ? DateUtils.formatTime(message.getTimeStamp()) : "");

        caption.setVisibility(message.getFile().getCaption() != null && !message.getFile().getCaption().isEmpty() ? View.VISIBLE : View.GONE);
        caption.setText(message.getFile().getCaption());

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