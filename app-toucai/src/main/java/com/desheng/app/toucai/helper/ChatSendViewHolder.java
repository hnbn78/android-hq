package com.desheng.app.toucai.helper;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ab.http.AbHttpAO;
import com.ab.util.Strs;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.desheng.app.toucai.adapter.ChatAdapter;
import com.desheng.app.toucai.consitance.Consitances;
import com.desheng.app.toucai.model.MessageInfo;
import com.desheng.app.toucai.util.Utils;
import com.desheng.app.toucai.view.BubbleImageView;
import com.desheng.app.toucai.view.GifTextView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.shark.tc.R;

/**
 * 作者：Rance on 2016/11/29 10:47
 * 邮箱：rance935@163.com
 */
public class ChatSendViewHolder extends BaseViewHolder<MessageInfo> {

    TextView chatItemDate;
    ImageView chatItemHeader;
    GifTextView chatItemContentText;
    BubbleImageView chatItemContentImage;
    ImageView chatItemFail;

    ProgressBar chatItemProgress;
    ImageView chatItemVoice;
    LinearLayout chatItemLayoutContent;
    TextView chatItemVoiceTime;
    TextView chatItemContent;

    private ChatAdapter.onItemClickListener onItemClickListener;
    private Handler handler;
    private ImageView isReadStatus;

    public ChatSendViewHolder(ViewGroup parent, ChatAdapter.onItemClickListener onItemClickListener, Handler handler) {
        super(parent, R.layout.item_chat_send);
        this.onItemClickListener = onItemClickListener;
        this.handler = handler;
        initView(itemView);
    }

    private void initView(View parent) {
        chatItemDate = ((TextView) parent.findViewById(R.id.chat_item_date));
        chatItemHeader = ((ImageView) parent.findViewById(R.id.chat_item_header));
        chatItemContentText = ((GifTextView) parent.findViewById(R.id.chat_item_content_text));
        chatItemContent = ((TextView) parent.findViewById(R.id.chat_item_content));
        chatItemContentImage = ((BubbleImageView) parent.findViewById(R.id.chat_item_content_image));

        chatItemVoice = ((ImageView) parent.findViewById(R.id.chat_item_voice));
        chatItemFail = ((ImageView) parent.findViewById(R.id.chat_item_fail));
        chatItemLayoutContent = ((LinearLayout) parent.findViewById(R.id.chat_item_layout_content));
        chatItemVoiceTime = ((TextView) parent.findViewById(R.id.chat_item_voice_time));
        chatItemProgress = ((ProgressBar) parent.findViewById(R.id.chat_item_progress));
        isReadStatus = ((ImageView) parent.findViewById(R.id.isReadStatus));

    }

    @Override
    public void setData(MessageInfo data) {
        if (chatItemDate != null) {
            chatItemDate.setText(data.getTime() != null ? data.getTime() : "");
        }

        chatItemHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onItemClickListener.onHeaderClick(getDataPosition());
            }
        });
        if (data.getContent() != null) {
            //chatItemContentText.setSpanText(handler, data.getContent(), true);
            chatItemContent.setText(data.getContent());
            chatItemVoice.setVisibility(View.GONE);
            chatItemContentText.setVisibility(View.GONE);
            chatItemContent.setVisibility(View.VISIBLE);
            chatItemLayoutContent.setVisibility(View.VISIBLE);
            chatItemVoiceTime.setVisibility(View.GONE);
            chatItemContentImage.setVisibility(View.GONE);
        } else if (data.getImageUrl() != null) {
            chatItemVoice.setVisibility(View.GONE);
            chatItemLayoutContent.setVisibility(View.VISIBLE);
            chatItemVoiceTime.setVisibility(View.GONE);
            chatItemContentText.setVisibility(View.GONE);
            chatItemContent.setVisibility(View.GONE);
            chatItemContentImage.setVisibility(View.VISIBLE);
            if (data.getImageUrl().contains("http")) {
                GlideUrl glideUrl = new GlideUrl(data.getImageUrl(), new LazyHeaders.Builder().addHeader("Cookie", AbHttpAO.getIns().getLastCookie()).build());
                Glide.with(getContext()).load(glideUrl).into(chatItemContentImage);
                chatItemContentImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onImageClick(chatItemContentImage, glideUrl,null);
                    }
                });
            } else {
                Glide.with(getContext()).load(data.getImageUrl()).into(chatItemContentImage);
                chatItemContentImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onImageClick(chatItemContentImage, null,data.getImageUrl());
                    }
                });
            }
        } else if (data.getFilepath() != null) {
            chatItemVoice.setVisibility(View.VISIBLE);
            chatItemLayoutContent.setVisibility(View.VISIBLE);
            chatItemContentText.setVisibility(View.GONE);
            chatItemContent.setVisibility(View.GONE);
            chatItemVoiceTime.setVisibility(View.VISIBLE);
            chatItemContentImage.setVisibility(View.GONE);
            chatItemVoiceTime.setText(Utils.formatTime(data.getVoiceTime()));
            chatItemLayoutContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //onItemClickListener.onVoiceClick(chatItemVoice, getDataPosition());
                }
            });
        }
        switch (data.getSendState()) {
            case Consitances.CHAT_ITEM_SENDING:
                chatItemProgress.setVisibility(View.VISIBLE);
                chatItemFail.setVisibility(View.GONE);
                break;
            case Consitances.CHAT_ITEM_SEND_ERROR:
                chatItemProgress.setVisibility(View.GONE);
                chatItemFail.setVisibility(View.VISIBLE);
                break;
            case Consitances.CHAT_ITEM_SEND_SUCCESS:
                chatItemProgress.setVisibility(View.GONE);
                chatItemFail.setVisibility(View.GONE);
                break;
        }

        //Glide.with(getContext()).asBitmap().load(data.getHeader()).into(chatItemHeader);
        if (data.isHuiZhi()) {
            isReadStatus.setVisibility(View.VISIBLE);
            isReadStatus.setBackgroundResource(data.getIsRead() ? R.mipmap.ic_symbol : R.mipmap.ic_symbol_act);
            chatItemProgress.setVisibility(View.GONE);
            chatItemFail.setVisibility(View.GONE);
        } else {
            isReadStatus.setBackgroundResource(0);
            chatItemFail.setVisibility(View.GONE);
            chatItemProgress.setVisibility(View.VISIBLE);
        }
    }
}
