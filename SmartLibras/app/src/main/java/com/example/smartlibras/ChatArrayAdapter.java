package com.example.smartlibras;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private TextView chatText;
    private TextView dataText;
    private TextView nameText;
    private WebView wb;
    private RelativeLayout mainLayout;
    private List<ChatMessage> chatMessageList = new ArrayList<>();

    @Override
    public void add(ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessageObj = getItem(position);
        View row;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (chatMessageObj.isLeft()) {
            row = inflater.inflate(R.layout.message2, parent, false);
        }else{
            row = inflater.inflate(R.layout.message, parent, false);
        }

        this.inicializarComponentes(row);
        chatText.setText(chatMessageObj.getMessage());
        dataText.setText(chatMessageObj.getData());

        if(chatMessageObj.isTela2()) {
            if (chatMessageObj.getMessage() != null && !chatMessageObj.getMessage().isEmpty()) {
                String url = "http://www.innovative.inf.br/"+chatMessageObj.getMessage()+".gif";
                mainLayout.removeView(chatText);
                wb.setBackgroundColor(Color.TRANSPARENT);
                wb.getSettings().setLoadWithOverviewMode(true);
                wb.getSettings().setUseWideViewPort(true);
                wb.loadUrl(url);
                wb.setVisibility(View.VISIBLE);

            } else {
                mainLayout.removeView(wb);
            }
        }else{
            mainLayout.removeView(wb);
        }
        return row;
    }

    private void inicializarComponentes(View row){
        chatText = row.findViewById(R.id.message_text);
        dataText = row.findViewById(R.id.message_date);
        nameText = row.findViewById(R.id.message_nome);
        wb = row.findViewById(R.id.wb_imagem);
        mainLayout = row.findViewById(R.id.main);
    }


    public List<ChatMessage> getChatMessageList() {
        return chatMessageList;
    }

    public void setChatMessageList(List<ChatMessage> chatMessageList) {
        this.chatMessageList = chatMessageList;
    }
}