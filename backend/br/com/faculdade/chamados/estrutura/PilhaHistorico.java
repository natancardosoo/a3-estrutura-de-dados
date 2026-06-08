package br.com.faculdade.chamados.estrutura;

import br.com.faculdade.chamados.model.Historico;

public class PilhaHistorico {

    private Historico[] pilha;
    private int topo;
    private static final int CAPACIDADE_INICIAL = 10;

    public PilhaHistorico() {
        pilha = new Historico[CAPACIDADE_INICIAL];
        topo = -1;
    }

    public void empilhar(Historico historico) {
        if (topo + 1 == pilha.length) {
            aumentarCapacidade();
        }

        topo++;
        pilha[topo] = historico;
    }

    public Historico[] listarMaisRecentePrimeiro() {
        Historico[] lista = new Historico[tamanho()];

        int posicao = 0;

        for (int i = topo; i >= 0; i--) {
            lista[posicao] = pilha[i];
            posicao++;
        }

        return lista;
    }

    public int tamanho() {
        return topo + 1;
    }

    private void aumentarCapacidade() {
        Historico[] novoVetor = new Historico[pilha.length * 2];

        for (int i = 0; i < pilha.length; i++) {
            novoVetor[i] = pilha[i];
        }

        pilha = novoVetor;
    }
}