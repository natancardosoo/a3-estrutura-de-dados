package br.com.faculdade.chamados.service;

import br.com.faculdade.chamados.model.Chamado;
import br.com.faculdade.chamados.model.Historico;
import br.com.faculdade.chamados.model.Usuario;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ArquivoService {
    private static final File DATA_DIR = new File("data");
    private static final File CHAMADOS = new File(DATA_DIR, "chamados.txt");
    private static final File HISTORICOS = new File(DATA_DIR, "historicos.txt");
    private static final File USUARIOS = new File(DATA_DIR, "usuarios.txt");
    private static final File APLICATIVOS = new File(DATA_DIR, "aplicativos.txt");

    public ArquivoService() {
        inicializarArquivos();
    }

    private void inicializarArquivos() {
        try {
            if (!DATA_DIR.exists()) DATA_DIR.mkdirs();
            if (!CHAMADOS.exists()) CHAMADOS.createNewFile();
            if (!HISTORICOS.exists()) HISTORICOS.createNewFile();
            if (!USUARIOS.exists()) {
                USUARIOS.createNewFile();
                List<Usuario> usuariosPadrao = new ArrayList<>();
                usuariosPadrao.add(new Usuario("Administrador", "admin", "admin", "ADMINISTRADOR"));
                usuariosPadrao.add(new Usuario("Solicitante", "solicitante", "solicitante", "SOLICITANTE"));
                salvarUsuarios(usuariosPadrao);
            }
            if (!APLICATIVOS.exists()) {
                APLICATIVOS.createNewFile();
                try (PrintWriter pw = new PrintWriter(new FileWriter(APLICATIVOS))) {
                    pw.println("Sistema de Estoque");
                    pw.println("Sistema Financeiro");
                    pw.println("E-mail Corporativo");
                    pw.println("Impressora");
                    pw.println("Internet/Rede");
                    pw.println("Computador/Notebook");
                    pw.println("Outro");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao inicializar arquivos locais", e);
        }
    }

    public List<Usuario> carregarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        try {
            for (String linha : Files.readAllLines(USUARIOS.toPath())) {
                if (!linha.trim().isEmpty()) {
                    Usuario u = Usuario.fromFileLine(linha);
                    if (u != null) usuarios.add(u);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return usuarios;
    }

    public void salvarUsuarios(List<Usuario> usuarios) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(USUARIOS))) {
            for (Usuario u : usuarios) pw.println(u.toFileLine());
        } catch (IOException e) { e.printStackTrace(); }
    }

    public List<Chamado> carregarChamados() {
        List<Chamado> chamados = new ArrayList<>();
        try {
            for (String linha : Files.readAllLines(CHAMADOS.toPath())) {
                if (!linha.trim().isEmpty()) {
                    Chamado c = Chamado.fromFileLine(linha);
                    if (c != null) chamados.add(c);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return chamados;
    }

    public void salvarChamados(List<Chamado> chamados) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CHAMADOS))) {
            for (Chamado c : chamados) pw.println(c.toFileLine());
        } catch (IOException e) { e.printStackTrace(); }
    }

    public List<Historico> carregarHistoricos() {
        List<Historico> historicos = new ArrayList<>();
        try {
            for (String linha : Files.readAllLines(HISTORICOS.toPath())) {
                if (!linha.trim().isEmpty()) {
                    Historico h = Historico.fromFileLine(linha);
                    if (h != null) historicos.add(h);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return historicos;
    }

    public void salvarHistoricos(List<Historico> historicos) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(HISTORICOS))) {
            for (Historico h : historicos) pw.println(h.toFileLine());
        } catch (IOException e) { e.printStackTrace(); }
    }

    public List<String> carregarAplicativos() {
        List<String> aplicativos = new ArrayList<>();
        try {
            for (String linha : Files.readAllLines(APLICATIVOS.toPath())) {
                if (!linha.trim().isEmpty()) aplicativos.add(linha.trim());
            }
        } catch (IOException e) { e.printStackTrace(); }
        return aplicativos;
    }
}
