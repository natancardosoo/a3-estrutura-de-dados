package br.com.faculdade.chamados.server;

import br.com.faculdade.chamados.model.Usuario;
import br.com.faculdade.chamados.service.ChamadoService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class LoginHandler implements HttpHandler {

    private final ChamadoService service;

    public LoginHandler(ChamadoService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (ChamadoServer.tratarOptions(ex)) return;

        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            ChamadoServer.responder(ex, 405, "{\"erro\":\"Método não permitido\"}");
            return;
        }

        String body = ChamadoServer.lerBody(ex);
        String login = ChamadoServer.jsonGet(body, "login");
        String senha = ChamadoServer.jsonGet(body, "senha");

        Usuario usuario = service.autenticar(login, senha);

        if (usuario == null) {
            ChamadoServer.responder(ex, 401, "{\"erro\":\"Login ou senha inválidos\"}");
            return;
        }

        String json = String.format(
            "{\"nome\":\"%s\",\"login\":\"%s\",\"perfil\":\"%s\"}",
            ChamadoServer.esc(usuario.getNome()),
            ChamadoServer.esc(usuario.getLogin()),
            ChamadoServer.esc(usuario.getPerfil())
        );

        ChamadoServer.responder(ex, 200, json);
    }
}
