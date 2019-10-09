package com.tmt.livechat.screens.chat.viewbinders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tmt.livechat.R;
import com.tmt.livechat.chat.model.BaseMessage;
import com.tmt.livechat.chat.model.FileMessage;
import com.tmt.livechat.screens.chat.adapters.LiveChatAdapter;
import com.tmt.livechat.utils.DateUtils;
import com.tmt.livechat.utils.ImageUtils;

/**
 * Created by mohammednabil on 2019-09-16.
 */
public class OtherImageFileMessageHolder extends BaseViewBinder {
    RelativeLayout mainLayout;
    TextView timeText, caption;
    ImageView fileThumbnailImage;

    public OtherImageFileMessageHolder(View itemView) {
        super(itemView);
        mainLayout = itemView.findViewById(R.id.main_parent);
        timeText = itemView.findViewById(R.id.text_group_chat_time);
        fileThumbnailImage = itemView.findViewById(R.id.image_group_chat_file_thumbnail);
        caption = itemView.findViewById(R.id.text_caption);
    }

    public void bind(final Context context, final BaseMessage message, final LiveChatAdapter.OnItemClickListener listener) {

        FileMessage fileMessage = (FileMessage) message;
        timeText.setText(message.getPosted_at() != null ? DateUtils.formatTime(message.getTimeInMillis()) : "");

        caption.setVisibility(fileMessage.getLabel() != null && !fileMessage.getLabel().isEmpty() ? View.VISIBLE : View.GONE);
        caption.setText(fileMessage.getLabel());
        ImageUtils.displayImageFromUrl(context, fileMessage.getUrl(), fileThumbnailImage, null);

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