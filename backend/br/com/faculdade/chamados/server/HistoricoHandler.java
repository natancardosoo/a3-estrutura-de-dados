package br.com.faculdade.chamados.server;

import br.com.faculdade.chamados.model.Historico;
import br.com.faculdade.chamados.service.ChamadoService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

/**
 * GET /api/historico/{id}  → retorna histórico do chamado em JSON
 */
public class HistoricoHandler implements HttpHandler {

    private final ChamadoService service;

    public HistoricoHandler(ChamadoService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (ChamadoServer.tratarOptions(ex)) return;

        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
            ChamadoServer.responder(ex, 405, "{\"erro\":\"Método não permitido\"}");
            return;
        }

        String path = ex.getRequestURI().getPath(); // /api/historico/{id}
        String resto = path.replaceFirst("^/api/historico", "");

        if (resto.isEmpty() || resto.equals("/")) {
            ChamadoServer.responder(ex, 400, "{\"erro\":\"Informe o ID do chamado\"}");
            return;
        }

        try {
            int id = Integer.parseInt(resto.substring(1));
            List<Historico> historicos = service.historicoDoChamado(id);

            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < historicos.size(); i++) {
                Historico h = historicos.get(i);
                if (i > 0) sb.append(",");
                sb.append(String.format(
                    "{\"chamadoId\":%d,\"dataHora\":\"%s\",\"usuario\":\"%s\",\"acao\":\"%s\"}",
                    h.getChamadoId(),
                    ChamadoServer.esc(h.getDataHora()),
                    ChamadoServer.esc(h.getUsuario()),
                    ChamadoServer.esc(h.getAcao())
                ));
            }
            sb.append("]");

            ChamadoServer.responder(ex, 200, sb.toString());

        } catch (NumberFormatException e) {
            ChamadoServer.responder(ex, 400, "{\"erro\":\"ID inválido\"}");
        }
    }
}
