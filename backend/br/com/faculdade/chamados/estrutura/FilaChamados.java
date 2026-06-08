package br.com.faculdade.chamados.estrutura;

import br.com.faculdade.chamados.model.Chamado;

public class FilaChamados {

    private NoFila inicio;
    private NoFila fim;
    private int tamanho;

    public void enfileirar(Chamado chamado) {

        NoFila novo = new NoFila(chamado);

        if (inicio == null) {
            inicio = novo;
            fim = novo;
        } else {
            fim.setProximo(novo);
            fim = novo;
        }

        tamanho++;
    }

    public Chamado proximo() {

        if (vazia()) {
            return null;
        }

        Chamado chamado = inicio.getChamado();

        inicio = inicio.getProximo();

        if (inicio == null) {
            fim = null;
        }

        tamanho--;

        return chamado;
    }

    public boolean vazia() {
        return tamanho == 0;
    }

    public int tamanho() {
        return tamanho;
    }
}