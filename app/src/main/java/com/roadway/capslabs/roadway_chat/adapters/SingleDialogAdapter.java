package com.roadway.capslabs.roadway_chat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.roadway.capslabs.roadway_chat.models.ChatMessage;
import com.roadway.capslabs.roadway_chat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by konstantin on 15.09.16
 */
public class SingleDialogAdapter extends BaseAdapter {
    private List<ChatMessage> chatMessagesList = new ArrayList<>();
    private TextView textView;
    private Context context;

    public SingleDialogAdapter(Context context) {
        this.context = context;
    }

    public void add(ChatMessage obj){
        chatMessagesList.add(0, obj);
    }

    public void copyArrayList(List<ChatMessage> list){
        chatMessagesList = new ArrayList<>(list);
    }

    public void addArrayList(List<ChatMessage> list){
        chatMessagesList.addAll(0,list);
    }

    public List<ChatMessage> getList(){
        return chatMessagesList;
    }

    public ChatMessage getMessage(int position){
        return chatMessagesList.get(position - 1);
    }

    public void deleteMessage(int position){
        chatMessagesList.remove(position);
    }

    @Override
    public int getCount() {
        return chatMessagesList.size();
    }

    @Override
    public ChatMessage getItem(int position) {
        return this.chatMessagesList.get( getCount() - position - 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.dialog_in, parent, false);
        textView = (TextView) rowView.findViewById(R.id.msg);
        ChatMessage chatMessageObj = getItem(position);
        View row;
        int dialogViewId = R.layout.dialog_in;
        if (chatMessageObj.getOut()) {
            dialogViewId = R.layout.dialog_out;
        }
        row = inflater.inflate(dialogViewId, null);
        textView = (TextView) row.findViewById(R.id.msg);
        textView.setText(chatMessageObj.getMsg());

        return row;
    }

}
