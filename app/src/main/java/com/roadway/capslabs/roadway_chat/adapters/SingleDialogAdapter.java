package com.roadway.capslabs.roadway_chat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.roadway.capslabs.roadway_chat.ChatMessage;
import com.roadway.capslabs.roadway_chat.R;

import java.util.ArrayList;

/**
 * Created by konstantin on 15.09.16.
 */
public class SingleDialogAdapter extends BaseAdapter {
    private ArrayList<String> inList, outList;
    private ArrayList<ChatMessage> chatMessagesList = new ArrayList<>();
    private TextView textView;
    private Context context;


    public SingleDialogAdapter(Context context) {
        this.context = context;
    }



    public void add(ChatMessage obj){
        chatMessagesList.add(0, obj);
    }

    public void copyArrayList(ArrayList<ChatMessage> list){
        chatMessagesList = new ArrayList<>(list);
    }

    public void addArrayList(ArrayList<ChatMessage> list){
        chatMessagesList.addAll(0,list);
    }

    public ArrayList<ChatMessage> getArrayList(){
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

        ChatMessage chatMessageObj = getItem(position);
        View row;
        if (chatMessageObj.getOut()) {
            row = inflater.inflate(R.layout.dialog_out, null);
        } else {
            row = inflater.inflate(R.layout.dialog_in, null);
        }

        textView = (TextView) row.findViewById(R.id.msg);
        textView.setText(chatMessageObj.getMsg());

        return row;
    }

}
