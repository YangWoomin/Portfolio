package com.yangproject.embeddedproject.Others;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yangproject.embeddedproject.R;

import java.util.ArrayList;

/**
 * Created by 우민 on 2016-03-29.
 */
public class MessageItemAdapter extends BaseAdapter implements View.OnClickListener {
    private ArrayList<String> messages;
    private BtnClickListener btnClickListener;
    private int resourceId;

    public String getItem(int index) { return messages.get(index); }
    public int getCount() { return messages.size(); }
    public long getItemId(int position) { return position; }
    public void addMessage(String message) { messages.add(message); }
    public void delMessage(int index) { messages.remove(index); }
    public void clearMessages() { messages.clear(); }

    public MessageItemAdapter(BtnClickListener btnClickListener) {
        messages = new ArrayList<>();
        this.btnClickListener = btnClickListener;
    }

    public interface BtnClickListener {
        void onBtnClickedListener(boolean flag, int position);
    }

    public void onClick(View v) {
        if(this.btnClickListener != null) {
            if(v.getId() == R.id.addBtn) {
                this.btnClickListener.onBtnClickedListener(true, (int)v.getTag());
            }
            else {
                this.btnClickListener.onBtnClickedListener(false, (int)v.getTag());
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        TextView messageTextView;
        TextView addBtn, delBtn;
        CustomHolder customHolder = null;

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.message_item, parent, false);
            messageTextView = (TextView)convertView.findViewById(R.id.messageTextView);

            addBtn = (TextView)convertView.findViewById(R.id.addBtn);
            delBtn = (TextView)convertView.findViewById(R.id.delBtn);
            addBtn.setTag(position);
            delBtn.setTag(position);
            addBtn.setOnClickListener(this);
            delBtn.setOnClickListener(this);

            customHolder = new CustomHolder();
            customHolder.messageTextView = messageTextView;
            convertView.setTag(customHolder);
        }
        else {
            customHolder = (CustomHolder)convertView.getTag();
            messageTextView = customHolder.messageTextView;
        }
        messageTextView.setText(messages.get(position));
        return convertView;
    }

    private class CustomHolder {
        TextView messageTextView;
    }
}
