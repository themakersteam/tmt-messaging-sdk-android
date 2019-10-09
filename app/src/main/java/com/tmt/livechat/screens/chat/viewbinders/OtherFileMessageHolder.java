package com.tmt.livechat.screens.chat.viewbinders;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tmt.livechat.R;
import com.tmt.livechat.chat.model.BaseMessage;
import com.tmt.livechat.screens.chat.adapters.LiveChatAdapter;
import com.tmt.livechat.utils.DateUtils;

/**
 * Created by mohammednabil on 2019-09-16.
 */
public class OtherFileMessageHolder extends BaseViewBinder {

    RelativeLayout mainLayout;
    TextView timeText, fileNameText;

    public OtherFileMessageHolder(View itemView) {
        super(itemView);
        mainLayout = itemView.findViewById(R.id.main_parent);
        timeText = itemView.findViewById(R.id.text_group_chat_time);
        fileNameText = itemView.findViewById(R.id.text_group_chat_file_name);

    }

    public void bind(final BaseMessage message, final LiveChatAdapter.OnItemClickListener listener) {
        fileNameText.setText(message.getId());
        timeText.setText(message.getPosted_at() != null ? DateUtils.formatTime(message.getTimeInMillis()) : "");

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