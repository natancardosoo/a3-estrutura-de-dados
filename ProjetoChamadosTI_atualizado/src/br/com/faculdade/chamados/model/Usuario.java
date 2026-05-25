package br.com.faculdade.chamados.model;

public class Usuario {
    private String nome;
    private String login;
    private String senha;
    private String perfil;

    public Usuario(String nome, String login, String senha, String perfil) {
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.perfil = perfil;
    }

    public String getNome() { return nome; }
    public String getLogin() { return login; }
    public String getSenha() { return senha; }
    public String getPerfil() { return perfil; }

    public boolean isAdministrador() {
        return "ADMINISTRADOR".equalsIgnoreCase(perfil);
    }

    public String toFileLine() {
        return nome + ";" + login + ";" + senha + ";" + perfil;
    }

    public static Usuario fromFileLine(String linha) {
        String[] p = linha.split(";", -1);
        if (p.length < 4) return null;
        return new Usuario(p[0], p[1], p[2], p[3]);
    }
}
