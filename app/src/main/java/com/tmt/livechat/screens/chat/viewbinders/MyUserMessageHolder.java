package com.tmt.livechat.screens.chat.viewbinders;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.tmt.livechat.R;
import com.tmt.livechat.chat.model.BaseMessage;
import com.tmt.livechat.screens.chat.adapters.LiveChatAdapter;
import com.tmt.livechat.utils.DateUtils;
import com.tmt.livechat.utils.LocationUtils;

/**
 * Created by mohammednabil on 2019-09-16.
 */
public class MyUserMessageHolder extends BaseViewBinder {
    RelativeLayout mainLayout;
    TextView messageText, editedText, timeText;
    ImageView readReceipt;
    ViewGroup urlPreviewContainer;
    ImageView urlPreviewMainImageView;

    public MyUserMessageHolder(View itemView) {
        super(itemView);

        messageText = itemView.findViewById(R.id.text_group_chat_message);
        editedText = itemView.findViewById(R.id.text_group_chat_edited);
        timeText = itemView.findViewById(R.id.text_group_chat_time);
        readReceipt = itemView.findViewById(R.id.img_group_chat_read_receipt);
        mainLayout = itemView.findViewById(R.id.main_parent);
        urlPreviewContainer = itemView.findViewById(R.id.url_preview_container);
        urlPreviewMainImageView = itemView.findViewById(R.id.image_url_preview_main);

    }

    public void bind(final Context context, final BaseMessage message, final LiveChatAdapter.OnItemClickListener clickListener, final int position) {
        String mMessage = LocationUtils.getLocationUrlMessageIfExists(message);
        messageText.setText(mMessage);
        timeText.setText(message.getPosted_at() != null ? DateUtils.formatTime(message.getTimeInMillis()) : "");
        timeText.setTextColor(Color.parseColor("#9b9b9b"));

        processReadReceipt(context, readReceipt, timeText, message);
        urlPreviewContainer.setVisibility(View.GONE);
        if (message.isLocationMessage()){
            try {
                urlPreviewContainer.setVisibility(View.VISIBLE);
                Glide.with(context).load(R.drawable.map_place_holder).into(urlPreviewMainImageView);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            urlPreviewContainer.setVisibility(View.GONE);
        }

        if (clickListener != null) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onUserMessageItemClick(message);
                }
            });
        }
    }

}