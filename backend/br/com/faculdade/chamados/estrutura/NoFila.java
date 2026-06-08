package br.com.faculdade.chamados.estrutura;

import br.com.faculdade.chamados.model.Chamado;

public class NoFila {

    private Chamado chamado;
    private NoFila proximo;

    public NoFila(Chamado chamado) {
        this.chamado = chamado;
    }

    public Chamado getChamado() {
        return chamado;
    }

    public NoFila getProximo() {
        return proximo;
    }

    public void setProximo(NoFila proximo) {
        this.proximo = proximo;
    }
}