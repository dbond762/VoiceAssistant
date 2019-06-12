package com.example.voiceassistant;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MessageListAddapter extends RecyclerView.Adapter {
    private static final int ASSISTANT_TYPE = 0;
    private static final int USER_TYPE = 1;

    public List<Message> messageList = new ArrayList<>();

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int index) {
        Message message = messageList.get(index);
        ((MessageViewHolder)viewHolder).bind(message);
    }

    public int getItemViewType(int index) {
        Message message = messageList.get(index);
        if (message.isSent) {
            return USER_TYPE;
        } else {
            return ASSISTANT_TYPE;
        }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        if (viewType == USER_TYPE) {
            view = inflater.inflate(R.layout.user_message, viewGroup, false);
        } else {
            view = inflater.inflate(R.layout.assistant_message, viewGroup, false);
        }

        return new MessageViewHolder(view);
    }

    public int getItemCount() {
        return messageList.size();
    }
}
