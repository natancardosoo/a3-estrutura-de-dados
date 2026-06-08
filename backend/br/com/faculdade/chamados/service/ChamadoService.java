package br.com.faculdade.chamados.service;

import br.com.faculdade.chamados.estrutura.*;
import br.com.faculdade.chamados.model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChamadoService {
    private ArquivoService arquivoService = new ArquivoService();
    private ListaLigadaChamados listaLigada = new ListaLigadaChamados();
    private ArvoreChamados arvore = new ArvoreChamados();
    private FilaChamados fila = new FilaChamados();
    private List<Chamado> chamados;
    private List<Historico> historicos;

    public ChamadoService() {
        chamados = arquivoService.carregarChamados();
        historicos = arquivoService.carregarHistoricos();
        reconstruirEstruturas();
    }

    private void reconstruirEstruturas() {
        listaLigada = new ListaLigadaChamados();
        arvore = new ArvoreChamados();
        fila = new FilaChamados();
        for (Chamado c : chamados) {
            listaLigada.adicionar(c);
            arvore.inserir(c);
            if ("Aberto".equalsIgnoreCase(c.getStatus())) fila.enfileirar(c);
        }
    }

    public Usuario autenticar(String login, String senha) {
        for (Usuario u : arquivoService.carregarUsuarios()) {
            if (u.getLogin().equals(login) && u.getSenha().equals(senha)) return u;
        }
        return null;
    }

    public Chamado criarChamado(String titulo, String aplicativo, String descricao, Usuario solicitante) {
        int id = chamados.stream().mapToInt(Chamado::getId).max().orElse(0) + 1;
        String agora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        Chamado chamado = new Chamado(id, titulo, aplicativo, descricao, solicitante.getNome(), agora, "", "", "", 0, "Aberto");
        chamados.add(chamado);
        registrarHistorico(id, solicitante.getNome(), "Chamado criado com status Aberto");
        salvarTudo();
        reconstruirEstruturas();
        return chamado;
    }

    public List<String> listarAplicativos() { return arquivoService.carregarAplicativos(); }

    public List<Chamado> listarTodos() { return new ArrayList<>(chamados); }

    public List<Chamado> listarPorSolicitante(String solicitante) {
        return chamados.stream().filter(c -> c.getSolicitante().equals(solicitante)).collect(Collectors.toList());
    }

    public Chamado buscarPorId(int id) { return arvore.buscar(id); }

    public void atuarNoChamado(int id, Usuario admin) {
        Chamado c = buscarPorId(id);
        if (c != null && "Aberto".equalsIgnoreCase(c.getStatus())) {
            c.setAtendente(admin.getNome());
            c.setStatus("Em Atendimento");
            registrarHistorico(id, admin.getNome(), "Chamado assumido. Status alterado para Em Atendimento");
            salvarTudo();
            reconstruirEstruturas();
        }
        // Se cancelado ou finalizado, não faz nada (retorna silenciosamente)
    }

    public void salvarDadosAtendimento(int id, String atendente, String dataPrevista, String solucao, double horas, Usuario usuario) {
        Chamado c = buscarPorId(id);
        if (c != null) {
            c.setAtendente(atendente);
            c.setDataPrevistaEncerramento(dataPrevista);
            c.setSolucao(solucao);
            c.setHorasGastas(horas);
            registrarHistorico(id, usuario.getNome(), "Dados do atendimento atualizados");
            salvarTudo();
            reconstruirEstruturas();
        }
    }

    public void finalizarChamado(int id, String atendente, String dataPrevista, String solucao, double horas, Usuario usuario) {
        Chamado c = buscarPorId(id);
        if (c != null) {
            c.setAtendente(atendente);
            c.setDataPrevistaEncerramento(dataPrevista);
            c.setSolucao(solucao);
            c.setHorasGastas(horas);
            c.setStatus("Finalizado");
            registrarHistorico(id, usuario.getNome(), "Chamado finalizado");
            salvarTudo();
            reconstruirEstruturas();
        }
    }

    public void cancelarChamado(int id, Usuario usuario) {
        Chamado c = buscarPorId(id);
        if (c != null && !"Finalizado".equalsIgnoreCase(c.getStatus()) && !"Cancelado".equalsIgnoreCase(c.getStatus())) {
            c.setStatus("Cancelado");
            registrarHistorico(id, usuario.getNome(), "Chamado cancelado pelo usuário");
            salvarTudo();
            reconstruirEstruturas();
        }
    }

    public Historico[] historicoDoChamado(int chamadoId) {
        PilhaHistorico pilha = new PilhaHistorico();

        for (Historico h : historicos) {
            if (h.getChamadoId() == chamadoId) {
                pilha.empilhar(h);
            }
        }

        return pilha.listarMaisRecentePrimeiro();
    }

    private void registrarHistorico(int chamadoId, String usuario, String acao) {
        historicos.add(new Historico(chamadoId, usuario, acao));
    }

    private void salvarTudo() {
        arquivoService.salvarChamados(chamados);
        arquivoService.salvarHistoricos(historicos);
    }
}
