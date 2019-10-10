package com.example.smartlibras;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class VolleyClass {
    private Context context;

    public VolleyClass(Context context){
        this.context = context;
    }

    public void salvar(final String url, final String pergunta){
        String pageUrl = "http://177.17.175.109:8080/SinaisWS/escrever/"+url+"?texto="+pergunta;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, pageUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {}
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(MainActivity.class.getName(),"Error :" + error.toString());
            }
        });

        requestQueue.add(stringRequest);
    }

    public void ler(final boolean s, final String url, final VolleyCallback callback){
        String pageUrl;
        if(url.equals("pergunta")){
            pageUrl = "http://177.17.175.109:8080/SinaisWS/ler/pergunta";
        }else{
            pageUrl = "http://177.17.175.109:8080/SinaisWS/ler/resposta?s="+s;
        }
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, pageUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(MainActivity.class.getName(),"Error :" + error.toString());
            }
        });

        requestQueue.add(stringRequest);
    }

    public void salvarPergunta(final String texto){
        salvar("pergunta", texto);
    }

    public void lerPergunta(final VolleyCallback callback){
        ler(false, "pergunta", callback);
    }

    public void lerResposta(boolean s, final VolleyCallback callback){
        ler(s, "resposta", callback);
    }

}
