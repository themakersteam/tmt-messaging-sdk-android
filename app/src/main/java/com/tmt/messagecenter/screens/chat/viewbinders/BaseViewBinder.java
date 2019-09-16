package com.tmt.messagecenter.screens.chat.viewbinders;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tmt.messagecenter.R;
import com.tmt.messagecenter.connection_xmpp.constants.DeliveryReceiptStatus;
import com.tmt.messagecenter.model.UserMessage;

/**
 * Created by mohammednabil on 2019-09-16.
 */
public class BaseViewBinder extends RecyclerView.ViewHolder {

    public BaseViewBinder(View itemView) {
        super(itemView);
    }


    protected void processReadReceipt(Context context, ImageView readReceiptImg, TextView chatTime, UserMessage message) {
        if (message.getStatus().equals(DeliveryReceiptStatus.FAILED)) {
            readReceiptImg.setImageResource(R.drawable.ic_sendfail);
            readReceiptImg.setColorFilter(Color.parseColor("#FFDD2C00"));
            chatTime.setTextColor(Color.parseColor("#fb2b2b"));
            chatTime.setText(context.getString(R.string.ms_chat_failed_to_send));
        } else if (message.getStatus().equals(DeliveryReceiptStatus.RECEIVED)) {
            readReceiptImg.setImageResource(R.drawable.ic_msgdelivered);
            readReceiptImg.setColorFilter(Color.parseColor("#9b9b9b"));
        } else if (message.getStatus().equals(DeliveryReceiptStatus.SEEN)) {
            readReceiptImg.setImageResource(R.drawable.ic_msgdelivered);
            readReceiptImg.setColorFilter(Color.parseColor("#00c269"));
        } else {
            readReceiptImg.setImageResource(R.drawable.ic_msg_progress);
            readReceiptImg.setColorFilter(Color.parseColor("#9b9b9b"));
        }
    }

}
