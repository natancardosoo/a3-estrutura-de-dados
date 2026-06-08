package br.com.faculdade.chamados.model;

public class Chamado {
    private int id;
    private String titulo;
    private String aplicativo;
    private String descricao;
    private String solicitante;
    private String dataSolicitacao;
    private String atendente;
    private String dataPrevistaEncerramento;
    private String solucao;
    private double horasGastas;
    private String status;

    public Chamado(int id, String titulo, String aplicativo, String descricao,
                   String solicitante, String dataSolicitacao, String atendente,
                   String dataPrevistaEncerramento, String solucao, double horasGastas, String status) {
        this.id = id;
        this.titulo = titulo;
        this.aplicativo = aplicativo;
        this.descricao = descricao;
        this.solicitante = solicitante;
        this.dataSolicitacao = dataSolicitacao;
        this.atendente = atendente;
        this.dataPrevistaEncerramento = dataPrevistaEncerramento;
        this.solucao = solucao;
        this.horasGastas = horasGastas;
        this.status = status;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAplicativo() { return aplicativo; }
    public String getDescricao() { return descricao; }
    public String getSolicitante() { return solicitante; }
    public String getDataSolicitacao() { return dataSolicitacao; }
    public String getAtendente() { return atendente; }
    public String getDataPrevistaEncerramento() { return dataPrevistaEncerramento; }
    public String getSolucao() { return solucao; }
    public double getHorasGastas() { return horasGastas; }
    public String getStatus() { return status; }

    public void setAtendente(String atendente) { this.atendente = atendente; }
    public void setDataPrevistaEncerramento(String dataPrevistaEncerramento) { this.dataPrevistaEncerramento = dataPrevistaEncerramento; }
    public void setSolucao(String solucao) { this.solucao = solucao; }
    public void setHorasGastas(double horasGastas) { this.horasGastas = horasGastas; }
    public void setStatus(String status) { this.status = status; }

    private static String safe(String v) {
        return v == null ? "" : v.replace(";", ",").replace("\n", " ").replace("\r", " ");
    }

    public String toFileLine() {
        return id + ";" + safe(titulo) + ";" + safe(aplicativo) + ";" + safe(descricao) + ";" +
                safe(solicitante) + ";" + safe(dataSolicitacao) + ";" + safe(atendente) + ";" +
                safe(dataPrevistaEncerramento) + ";" + safe(solucao) + ";" + horasGastas + ";" + safe(status);
    }

    public static Chamado fromFileLine(String linha) {
        String[] p = linha.split(";", -1);

        // Formato novo, sem anexo: 11 campos
        if (p.length >= 11) {
            return new Chamado(
                    Integer.parseInt(p[0]), p[1], p[2], p[3], p[4], p[5], p[6], p[7], p[8],
                    Double.parseDouble(p[9]), p[10]
            );
        }
        return null;
    }
}
