package com.example.smartlibras;

public class Conversa {
    private String foto;
    private String nome;
    private String mensagem;

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Conversa(String foto, String nome, String mensagem) {
        this.foto = foto;
        this.nome = nome;
        this.mensagem = mensagem;
    }

    public Conversa() {
    }

    @Override
    public String toString() {
        return "nome = " + nome + "\nmensagem = " + mensagem + "\nfoto = " + foto;
    }
}
