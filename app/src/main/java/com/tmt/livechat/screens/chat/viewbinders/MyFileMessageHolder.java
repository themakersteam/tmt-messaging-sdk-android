package com.tmt.livechat.screens.chat.viewbinders;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dinuscxj.progressbar.CircleProgressBar;
import com.tmt.livechat.R;
import com.tmt.livechat.chat.model.BaseMessage;
import com.tmt.livechat.chat.model.FileMessage;
import com.tmt.livechat.screens.chat.adapters.LiveChatAdapter;
import com.tmt.livechat.utils.DateUtils;

/**
 * Created by mohammednabil on 2019-09-16.
 */
public  class MyFileMessageHolder extends BaseViewBinder {

    RelativeLayout mainLayout;
    TextView fileNameText, timeText;
    CircleProgressBar circleProgressBar;
    ImageView readReceipt;

    public MyFileMessageHolder(View itemView) {
        super(itemView);
        mainLayout = itemView.findViewById(R.id.main_parent);
        timeText = itemView.findViewById(R.id.text_group_chat_time);
        fileNameText = itemView.findViewById(R.id.text_group_chat_file_name);
        readReceipt = itemView.findViewById(R.id.img_group_chat_read_receipt);
        circleProgressBar = itemView.findViewById(R.id.circle_progress);
    }

    public void bind(Context context, final BaseMessage message, final LiveChatAdapter.OnItemClickListener listener) {
        fileNameText.setText(message.getId());
        FileMessage fileMessage = (FileMessage)message;
        timeText.setText(message.getPosted_at() != null ? DateUtils.formatTime(message.getTimeInMillis()) : "");
        timeText.setTextColor(Color.parseColor("#9b9b9b"));
        circleProgressBar.setVisibility(fileMessage.getProgress() < 100 ? View.VISIBLE : View.GONE);
        circleProgressBar.setProgress(fileMessage.getProgress());

        processReadReceipt(context, readReceipt, timeText, message);

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