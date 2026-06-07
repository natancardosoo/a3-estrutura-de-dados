package br.com.faculdade.chamados.server;

import br.com.faculdade.chamados.service.ChamadoService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

/**
 * GET /api/aplicativos  → retorna lista de aplicativos em JSON
 */
public class AplicativosHandler implements HttpHandler {

    private final ChamadoService service;

    public AplicativosHandler(ChamadoService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (ChamadoServer.tratarOptions(ex)) return;

        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
            ChamadoServer.responder(ex, 405, "{\"erro\":\"Método não permitido\"}");
            return;
        }

        List<String> apps = service.listarAplicativos();

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < apps.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(ChamadoServer.esc(apps.get(i))).append("\"");
        }
        sb.append("]");

        ChamadoServer.responder(ex, 200, sb.toString());
    }
}
