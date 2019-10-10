package com.example.smartlibras;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMessage {
    private boolean left;
    private boolean tela2;
    private String message;
    private String data;

    public ChatMessage(boolean left, boolean tela2, String message) {
        super();
        this.left = left;
        this.message = message;
        this.tela2 = tela2;
        this.data = new SimpleDateFormat("HH:mm").format(new Date());
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isTela2() {
        return tela2;
    }

    public void setTela2(boolean tela2) {
        this.tela2 = tela2;
    }

}
