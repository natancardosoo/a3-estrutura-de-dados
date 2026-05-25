package br.com.faculdade.chamados.estrutura;

import br.com.faculdade.chamados.model.Chamado;
import java.util.LinkedList;
import java.util.Queue;

public class FilaChamados {
    private Queue<Chamado> fila = new LinkedList<>();

    public void enfileirar(Chamado chamado) { fila.add(chamado); }
    public Chamado proximo() { return fila.poll(); }
    public boolean vazia() { return fila.isEmpty(); }
    public int tamanho() { return fila.size(); }
}
