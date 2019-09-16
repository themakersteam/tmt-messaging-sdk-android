package com.tmt.messagecenter.screens.chat.viewbinders;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tmt.messagecenter.R;
import com.tmt.messagecenter.model.UserMessage;
import com.tmt.messagecenter.screens.chat.adapters.MessageCenterChatAdapter;
import com.tmt.messagecenter.utils.DateUtils;

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

    public void bind(final UserMessage message, final MessageCenterChatAdapter.OnItemClickListener listener) {
        fileNameText.setText(message.getFile().getId());
        timeText.setText(message.getTimeStamp() != null ? DateUtils.formatTime(message.getTimeStamp()) : "");

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