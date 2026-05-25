package br.com.faculdade.chamados.estrutura;

import br.com.faculdade.chamados.model.Chamado;

public class ArvoreChamados {
    private No raiz;

    private static class No {
        Chamado chamado;
        No esquerda;
        No direita;
        No(Chamado chamado) { this.chamado = chamado; }
    }

    public void inserir(Chamado chamado) {
        raiz = inserirRec(raiz, chamado);
    }

    private No inserirRec(No atual, Chamado chamado) {
        if (atual == null) return new No(chamado);
        if (chamado.getId() < atual.chamado.getId()) atual.esquerda = inserirRec(atual.esquerda, chamado);
        else if (chamado.getId() > atual.chamado.getId()) atual.direita = inserirRec(atual.direita, chamado);
        return atual;
    }

    public Chamado buscar(int id) {
        No atual = raiz;
        while (atual != null) {
            if (id == atual.chamado.getId()) return atual.chamado;
            atual = id < atual.chamado.getId() ? atual.esquerda : atual.direita;
        }
        return null;
    }
}
