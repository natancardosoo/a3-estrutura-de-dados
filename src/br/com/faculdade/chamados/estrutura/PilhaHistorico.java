package br.com.faculdade.chamados.estrutura;

import br.com.faculdade.chamados.model.Historico;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class PilhaHistorico {
    private Stack<Historico> pilha = new Stack<>();

    public void empilhar(Historico historico) { pilha.push(historico); }

    public List<Historico> listarMaisRecentePrimeiro() {
        List<Historico> lista = new ArrayList<>();
        for (int i = pilha.size() - 1; i >= 0; i--) lista.add(pilha.get(i));
        return lista;
    }
}
