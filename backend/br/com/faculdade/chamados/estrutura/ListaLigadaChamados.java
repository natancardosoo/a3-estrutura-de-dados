package br.com.faculdade.chamados.estrutura;

import br.com.faculdade.chamados.model.Chamado;

public class ListaLigadaChamados {

    private No inicio;
    private int tamanho;

    private static class No {
        Chamado valor;
        No proximo;

        No(Chamado valor) {
            this.valor = valor;
        }
    }

    public void adicionar(Chamado chamado) {

        No novo = new No(chamado);

        if (inicio == null) {
            inicio = novo;
            tamanho++;
            return;
        }

        No atual = inicio;

        while (atual.proximo != null) {
            atual = atual.proximo;
        }

        atual.proximo = novo;
        tamanho++;
    }

    public Chamado[] listar() {

        Chamado[] vetor = new Chamado[tamanho];

        No atual = inicio;
        int indice = 0;

        while (atual != null) {
            vetor[indice] = atual.valor;
            indice++;
            atual = atual.proximo;
        }

        return vetor;
    }

    public Chamado buscarPorId(int id) {

        No atual = inicio;

        while (atual != null) {

            if (atual.valor.getId() == id) {
                return atual.valor;
            }

            atual = atual.proximo;
        }

        return null;
    }

    public int tamanho() {
        return tamanho;
    }
}