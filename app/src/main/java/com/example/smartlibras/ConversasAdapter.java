package com.example.smartlibras;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ConversasAdapter extends ArrayAdapter<Conversa> {

    private TextView messageName;
    private TextView messageDate;
    private TextView messageText;
    private TextView messageFoto;
    private List<Conversa> conversas = new ArrayList<>();

    @Override
    public void add(Conversa object) {
        conversas.add(object);
        super.add(object);
    }

    public ConversasAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public int getCount() {
        return this.conversas.size();
    }

    public Conversa getItem(int index) {
        return this.conversas.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Conversa conversa = getItem(position);
        View row;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.lista_conversas, parent, false);

        this.inicializarComponentes(row);

        messageName.setText(conversa.getNome());
        String mensagem = conversa.getMensagem();
        if(mensagem.length() > 32){
            mensagem = mensagem.substring(0,32).concat("...");
        }
        messageText.setText(mensagem);
        messageDate.setText("01/01/2019");
        return row;
    }

    private void inicializarComponentes(View row){
        messageText = row.findViewById(R.id.message_text);
        messageDate = row.findViewById(R.id.message_date);
        messageName = row.findViewById(R.id.message_nome);
        messageFoto = row.findViewById(R.id.message_foto);
    }


    public List<Conversa> getConversas() {
        return conversas;
    }

    public void setConversas(List<Conversa> conversas) {
        this.conversas = conversas;
    }
}