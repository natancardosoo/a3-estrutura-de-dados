package br.com.faculdade.chamados.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Historico {
    private int chamadoId;
    private String dataHora;
    private String usuario;
    private String acao;

    public Historico(int chamadoId, String usuario, String acao) {
        this.chamadoId = chamadoId;
        this.usuario = usuario;
        this.acao = acao;
        this.dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public Historico(int chamadoId, String dataHora, String usuario, String acao) {
        this.chamadoId = chamadoId;
        this.dataHora = dataHora;
        this.usuario = usuario;
        this.acao = acao;
    }

    public int getChamadoId() { return chamadoId; }
    public String getDataHora() { return dataHora; }
    public String getUsuario() { return usuario; }
    public String getAcao() { return acao; }

    public String toFileLine() {
        return chamadoId + ";" + dataHora + ";" + usuario + ";" + acao.replace(";", ",");
    }

    public static Historico fromFileLine(String linha) {
        String[] p = linha.split(";", -1);
        if (p.length < 4) return null;
        return new Historico(Integer.parseInt(p[0]), p[1], p[2], p[3]);
    }
}
