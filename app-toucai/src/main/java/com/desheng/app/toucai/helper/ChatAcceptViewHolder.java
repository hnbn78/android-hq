package com.desheng.app.toucai.helper;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.http.AbHttpAO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.desheng.app.toucai.adapter.ChatAdapter;
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
public class ChatAcceptViewHolder extends BaseViewHolder<MessageInfo> {

    TextView chatItemDate;
    ImageView chatItemHeader;
    GifTextView chatItemContentText;
    BubbleImageView chatItemContentImage;
    ImageView chatItemVoice;
    LinearLayout chatItemLayoutContent;
    TextView chatItemVoiceTime;
    TextView chatItemContent;

    private ChatAdapter.onItemClickListener onItemClickListener;
    private Handler handler;
    private TextView viewById;

    public ChatAcceptViewHolder(ViewGroup parent, ChatAdapter.onItemClickListener onItemClickListener, Handler handler) {
        super(parent, R.layout.item_chat_accept);
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
        chatItemLayoutContent = ((LinearLayout) parent.findViewById(R.id.chat_item_layout_content));
        chatItemVoiceTime = ((TextView) parent.findViewById(R.id.chat_item_voice_time));
    }

    @Override
    public void setData(MessageInfo data) {
        chatItemDate.setText(data.getTime() != null ? data.getTime() : "");
        //Glide.with(getContext()).load(data.getHeader()).into(chatItemHeader);
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
            GlideUrl glideUrl = new GlideUrl(data.getImageUrl(), new LazyHeaders.Builder().addHeader("Cookie", AbHttpAO.getIns().getLastCookie()).build());
            Glide.with(getContext()).load(glideUrl).into(chatItemContentImage);
            chatItemContentImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onImageClick(chatItemContentImage, glideUrl,null);
                }
            });
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
    }
}
