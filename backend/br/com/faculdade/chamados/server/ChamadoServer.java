package br.com.faculdade.chamados.server;

import br.com.faculdade.chamados.service.ChamadoService;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class ChamadoServer {

    private static final int PORT = 8080;
    private static final ChamadoService service = new ChamadoService();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/api/login",      new LoginHandler(service));
        server.createContext("/api/chamados",   new ChamadosHandler(service));
        server.createContext("/api/aplicativos",new AplicativosHandler(service));
        server.createContext("/api/historico",  new HistoricoHandler(service));

        server.setExecutor(null);
        server.start();

        System.out.println("==============================================");
        System.out.println(" Servidor rodando em http://localhost:" + PORT);
        System.out.println(" Abra o frontend/index.html no navegador");
        System.out.println("==============================================");
    }

    // ── utilitário: lê body da requisição ──────────────────────────────────
    public static String lerBody(HttpExchange ex) throws IOException {
        try (InputStream is = ex.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    // ── utilitário: envia resposta JSON ────────────────────────────────────
    public static void responder(HttpExchange ex, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    // ── utilitário: trata preflight CORS ───────────────────────────────────
    public static boolean tratarOptions(HttpExchange ex) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            responder(ex, 204, "");
            return true;
        }
        return false;
    }

    // ── utilitário: escapa string para JSON ────────────────────────────────
    public static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // ── utilitário: lê campo de um JSON simples (apenas strings/números) ───
    public static String jsonGet(String json, String chave) {
        String busca = "\"" + chave + "\"";
        int idx = json.indexOf(busca);
        if (idx < 0) return null;
        int colon = json.indexOf(':', idx + busca.length());
        if (colon < 0) return null;
        int start = colon + 1;
        while (start < json.length() && json.charAt(start) == ' ') start++;
        if (start >= json.length()) return null;
        char first = json.charAt(start);
        if (first == '"') {
            int end = start + 1;
            while (end < json.length() && json.charAt(end) != '"') {
                if (json.charAt(end) == '\\') end++;
                end++;
            }
            return json.substring(start + 1, end);
        } else {
            int end = start;
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
            return json.substring(start, end).trim();
        }
    }

    // ─── Handler: não encontrado ──────────────────────────────────────────
    public static class NotFoundHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            responder(ex, 404, "{\"erro\":\"Rota não encontrada\"}");
        }
    }
}
