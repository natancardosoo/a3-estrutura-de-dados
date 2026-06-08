package br.com.faculdade.chamados.server;

import br.com.faculdade.chamados.model.Chamado;
import br.com.faculdade.chamados.model.Usuario;
import br.com.faculdade.chamados.service.ChamadoService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Rotas:
 *   GET    /api/chamados                  → lista todos (admin) ou por solicitante (?solicitante=Nome)
 *   POST   /api/chamados                  → cria chamado
 *   GET    /api/chamados/{id}             → busca por id
 *   PUT    /api/chamados/{id}/atuar       → admin assume chamado
 *   PUT    /api/chamados/{id}/salvar      → salva dados de atendimento
 *   PUT    /api/chamados/{id}/finalizar   → finaliza chamado
 */
public class ChamadosHandler implements HttpHandler {

    private final ChamadoService service;

    public ChamadosHandler(ChamadoService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (ChamadoServer.tratarOptions(ex)) return;

        String path   = ex.getRequestURI().getPath(); // /api/chamados[/id[/acao]]
        String method = ex.getRequestMethod().toUpperCase();
        String query  = ex.getRequestURI().getQuery(); // solicitante=Nome

        // Remove prefixo /api/chamados
        String resto = path.replaceFirst("^/api/chamados", ""); // "", "/5", "/5/atuar" …

        String[] partes = resto.isEmpty() ? new String[0] : resto.substring(1).split("/");
        // partes[0] = id  (se existir)
        // partes[1] = acao (se existir)

        try {
            if (partes.length == 0) {
                // /api/chamados
                if ("GET".equals(method))  { listarChamados(ex, query); return; }
                if ("POST".equals(method)) { criarChamado(ex);          return; }
            }

            if (partes.length == 1) {
                // /api/chamados/{id}
                int id = Integer.parseInt(partes[0]);
                if ("GET".equals(method)) { buscarChamado(ex, id); return; }
            }

            if (partes.length == 2) {
                // /api/chamados/{id}/{acao}
                int id     = Integer.parseInt(partes[0]);
                String acao = partes[1];
                if ("PUT".equals(method)) {
                    switch (acao) {
                        case "atuar":    atuarChamado(ex, id);    return;
                        case "salvar":   salvarChamado(ex, id);   return;
                        case "finalizar":finalizarChamado(ex, id);return;
                        case "cancelar": cancelarChamado(ex, id); return;
                    }
                }
            }

            ChamadoServer.responder(ex, 404, "{\"erro\":\"Rota não encontrada\"}");

        } catch (NumberFormatException e) {
            ChamadoServer.responder(ex, 400, "{\"erro\":\"ID inválido\"}");
        } catch (Exception e) {
            ChamadoServer.responder(ex, 500, "{\"erro\":\"" + ChamadoServer.esc(e.getMessage()) + "\"}");
        }
    }

    // ── GET /api/chamados[?solicitante=Nome] ──────────────────────────────
    private void listarChamados(HttpExchange ex, String query) throws IOException {
        Chamado[] lista;

        if (query != null && query.startsWith("solicitante=")) {
            String nome = query.substring("solicitante=".length())
                               .replace("+", " ")
                               .replace("%20", " ");
            lista = service.listarPorSolicitante(nome);
        } else {
            lista = service.listarTodos();
        }

        ChamadoServer.responder(ex, 200, chamadosParaJson(lista));
    }

    // ── GET /api/chamados/{id} ─────────────────────────────────────────────
    private void buscarChamado(HttpExchange ex, int id) throws IOException {
        Chamado c = service.buscarPorId(id);
        if (c == null) {
            ChamadoServer.responder(ex, 404, "{\"erro\":\"Chamado não encontrado\"}");
            return;
        }
        ChamadoServer.responder(ex, 200, chamadoParaJson(c));
    }

    // ── POST /api/chamados ────────────────────────────────────────────────
    private void criarChamado(HttpExchange ex) throws IOException {
        String body = ChamadoServer.lerBody(ex);

        String titulo     = ChamadoServer.jsonGet(body, "titulo");
        String aplicativo = ChamadoServer.jsonGet(body, "aplicativo");
        String descricao  = ChamadoServer.jsonGet(body, "descricao");
        String nomeSol    = ChamadoServer.jsonGet(body, "solicitante");
        String loginSol   = ChamadoServer.jsonGet(body, "loginSolicitante");

        if (titulo == null || aplicativo == null || descricao == null || nomeSol == null) {
            ChamadoServer.responder(ex, 400, "{\"erro\":\"Campos obrigatórios ausentes\"}");
            return;
        }

        Usuario usuario = new Usuario(nomeSol, loginSol != null ? loginSol : nomeSol, "", "SOLICITANTE");
        Chamado c = service.criarChamado(titulo, aplicativo, descricao, usuario);
        ChamadoServer.responder(ex, 201, chamadoParaJson(c));
    }

    // ── PUT /api/chamados/{id}/atuar ──────────────────────────────────────
    private void atuarChamado(HttpExchange ex, int id) throws IOException {
        String body  = ChamadoServer.lerBody(ex);
        String nome  = ChamadoServer.jsonGet(body, "nome");
        String login = ChamadoServer.jsonGet(body, "login");
        String perfil= ChamadoServer.jsonGet(body, "perfil");

        if (nome == null || login == null) {
            ChamadoServer.responder(ex, 400, "{\"erro\":\"Dados do usuário ausentes\"}");
            return;
        }

        // Verifica se o chamado existe e está Aberto antes de atuar
        Chamado c = service.buscarPorId(id);
        if (c == null) {
            ChamadoServer.responder(ex, 404, "{\"erro\":\"Chamado não encontrado\"}");
            return;
        }
        if (!"Aberto".equalsIgnoreCase(c.getStatus())) {
            ChamadoServer.responder(ex, 400,
                "{\"erro\":\"Chamado não pode ser assumido pois está com status: " +
                ChamadoServer.esc(c.getStatus()) + "\"}");
            return;
        }

        Usuario admin = new Usuario(nome, login, "", perfil != null ? perfil : "ADMINISTRADOR");
        service.atuarNoChamado(id, admin);

        ChamadoServer.responder(ex, 200, chamadoParaJson(service.buscarPorId(id)));
    }

    // ── PUT /api/chamados/{id}/salvar ─────────────────────────────────────
    private void salvarChamado(HttpExchange ex, int id) throws IOException {
        String body = ChamadoServer.lerBody(ex);
        salvarOuFinalizar(ex, id, body, false);
    }

    // ── PUT /api/chamados/{id}/finalizar ──────────────────────────────────
    private void finalizarChamado(HttpExchange ex, int id) throws IOException {
        String body = ChamadoServer.lerBody(ex);
        salvarOuFinalizar(ex, id, body, true);
    }

    private void salvarOuFinalizar(HttpExchange ex, int id, String body, boolean finalizar) throws IOException {
        String atendente    = ChamadoServer.jsonGet(body, "atendente");
        String dataPrevista = ChamadoServer.jsonGet(body, "dataPrevista");
        String solucao      = ChamadoServer.jsonGet(body, "solucao");
        String horasStr     = ChamadoServer.jsonGet(body, "horasGastas");
        String nomeUser     = ChamadoServer.jsonGet(body, "nomeUsuario");
        String loginUser    = ChamadoServer.jsonGet(body, "loginUsuario");

        double horas = 0;
        if (horasStr != null && !horasStr.isEmpty()) {
            try { horas = Double.parseDouble(horasStr.replace(",", ".")); }
            catch (NumberFormatException ignored) {}
        }

        Usuario usuario = new Usuario(
            nomeUser  != null ? nomeUser  : "Administrador",
            loginUser != null ? loginUser : "admin",
            "", "ADMINISTRADOR"
        );

        if (finalizar) {
            service.finalizarChamado(id, atendente != null ? atendente : "",
                dataPrevista != null ? dataPrevista : "",
                solucao != null ? solucao : "", horas, usuario);
        } else {
            service.salvarDadosAtendimento(id, atendente != null ? atendente : "",
                dataPrevista != null ? dataPrevista : "",
                solucao != null ? solucao : "", horas, usuario);
        }

        Chamado c = service.buscarPorId(id);
        ChamadoServer.responder(ex, 200, chamadoParaJson(c));
    }

    // ── PUT /api/chamados/{id}/cancelar ──────────────────────────────────
    private void cancelarChamado(HttpExchange ex, int id) throws IOException {
        String body      = ChamadoServer.lerBody(ex);
        String nomeUser  = ChamadoServer.jsonGet(body, "nomeUsuario");
        String loginUser = ChamadoServer.jsonGet(body, "loginUsuario");
        String perfil    = ChamadoServer.jsonGet(body, "perfil");

        Usuario usuario = new Usuario(
            nomeUser  != null ? nomeUser  : "Sistema",
            loginUser != null ? loginUser : "sistema",
            "", perfil != null ? perfil : "SOLICITANTE"
        );

        service.cancelarChamado(id, usuario);

        Chamado c = service.buscarPorId(id);
        if (c == null) {
            ChamadoServer.responder(ex, 404, "{\"erro\":\"Chamado não encontrado\"}");
            return;
        }
        ChamadoServer.responder(ex, 200, chamadoParaJson(c));
    }

    // ── Serialização JSON ─────────────────────────────────────────────────
    public static String chamadoParaJson(Chamado c) {
        return String.format(
            "{\"id\":%d,\"titulo\":\"%s\",\"aplicativo\":\"%s\",\"descricao\":\"%s\"," +
            "\"solicitante\":\"%s\",\"dataSolicitacao\":\"%s\",\"atendente\":\"%s\"," +
            "\"dataPrevistaEncerramento\":\"%s\",\"solucao\":\"%s\"," +
            "\"horasGastas\":%s,\"status\":\"%s\"}",
            c.getId(),
            ChamadoServer.esc(c.getTitulo()),
            ChamadoServer.esc(c.getAplicativo()),
            ChamadoServer.esc(c.getDescricao()),
            ChamadoServer.esc(c.getSolicitante()),
            ChamadoServer.esc(c.getDataSolicitacao()),
            ChamadoServer.esc(c.getAtendente()),
            ChamadoServer.esc(c.getDataPrevistaEncerramento()),
            ChamadoServer.esc(c.getSolucao()),
            c.getHorasGastas(),
            ChamadoServer.esc(c.getStatus())
        );
    }

    public static String chamadosParaJson(Chamado[] lista) {
        StringBuilder sb = new StringBuilder("[");

        for (int i = 0; i < lista.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(chamadoParaJson(lista[i]));
        }

        sb.append("]");
        return sb.toString();
    }
}
