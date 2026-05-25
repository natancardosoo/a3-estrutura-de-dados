package br.com.faculdade.chamados.ui;

import br.com.faculdade.chamados.model.Chamado;
import br.com.faculdade.chamados.model.Historico;
import br.com.faculdade.chamados.model.Usuario;
import br.com.faculdade.chamados.service.ChamadoService;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;

public class DetalheChamadoDialog extends JDialog {
    private ChamadoService service;
    private Usuario usuario;
    private int chamadoId;
    private boolean admin;

    private JTextField txtAtendente = new JTextField();
    private JFormattedTextField txtDataPrevista;
    private JTextArea txtSolucao = new JTextArea();
    private JTextField txtHoras = new JTextField();
    private JTextArea areaHistorico = new JTextArea();

    private final Color FUNDO = new Color(245, 246, 248);
    private final Color CARD = Color.WHITE;
    private final Color VERDE = new Color(78, 112, 34);
    private final Color TEXTO = new Color(20, 35, 55);
    private final Color BORDA = new Color(215, 224, 235);

    public DetalheChamadoDialog(ChamadoService service, Usuario usuario, int chamadoId, boolean admin) {
        this.service = service;
        this.usuario = usuario;
        this.chamadoId = chamadoId;
        this.admin = admin;

        setTitle("Detalhes do Chamado #" + chamadoId);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());

        try {
            txtDataPrevista = new JFormattedTextField(new javax.swing.text.MaskFormatter("##/##/####"));
            txtDataPrevista.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
        } catch (ParseException e) {
            txtDataPrevista = new JFormattedTextField();
        }

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Dados do Chamado", criarAbaDados());
        abas.addTab("Histórico", criarAbaHistorico());

        add(abas, BorderLayout.CENTER);
    }

    private JPanel criarAbaDados() {
        Chamado c = service.buscarPorId(chamadoId);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(FUNDO);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JPanel dados = new JPanel(new GridBagLayout());
        dados.setBackground(CARD);
        dados.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 233, 238)),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addLabel(dados, gbc, 0, "ID:", String.valueOf(c.getId()));
        addLabel(dados, gbc, 1, "Status:", c.getStatus());
        addLabel(dados, gbc, 2, "Título:", c.getTitulo());
        addLabel(dados, gbc, 3, "Aplicativo:", c.getAplicativo());
        addLabel(dados, gbc, 4, "Solicitante:", c.getSolicitante());
        addLabel(dados, gbc, 5, "Data da Solicitação:", c.getDataSolicitacao());
        addTextAreaReadOnly(dados, gbc, 6, "Descrição:", c.getDescricao());

        txtAtendente.setText(valorSeguro(c.getAtendente()));
        txtDataPrevista.setText(valorSeguro(c.getDataPrevistaEncerramento()));
        txtSolucao.setText(valorSeguro(c.getSolucao()));
        txtHoras.setText(c.getHorasGastas() == 0 ? "" : String.valueOf(c.getHorasGastas()));

        configurarCampo(txtAtendente);
        configurarCampo(txtDataPrevista);
        configurarCampo(txtHoras);
        configurarArea(txtSolucao);

        addField(dados, gbc, 7, "Atendente:", txtAtendente);
        addField(dados, gbc, 8, "Data prevista:", txtDataPrevista);
        addTextArea(dados, gbc, 9, "Solução:", txtSolucao);
        addField(dados, gbc, 10, "Horas gastas:", txtHoras);

        txtAtendente.setEditable(admin);
        txtDataPrevista.setEditable(admin);
        txtSolucao.setEditable(admin);
        txtHoras.setEditable(admin);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        botoes.setBackground(CARD);

        JButton btnFechar = criarBotaoSecundario("Fechar");
        JButton btnAtuar = criarBotaoPrincipal("Atuar no Chamado");
        JButton btnSalvar = criarBotaoPrincipal("Salvar");
        JButton btnFinalizar = criarBotaoPrincipal("Finalizar Chamado");

        btnAtuar.setEnabled(admin && "Aberto".equalsIgnoreCase(c.getStatus()));
        btnSalvar.setEnabled(admin && !"Finalizado".equalsIgnoreCase(c.getStatus()));
        btnFinalizar.setEnabled(admin && !"Finalizado".equalsIgnoreCase(c.getStatus()));

        botoes.add(btnFechar);
        botoes.add(btnAtuar);
        botoes.add(btnSalvar);
        botoes.add(btnFinalizar);

        btnFechar.addActionListener(e -> dispose());

        btnAtuar.addActionListener(e -> {
            service.atuarNoChamado(chamadoId, usuario);
            txtAtendente.setText(usuario.getNome());
            btnAtuar.setEnabled(false);
            atualizarHistorico();
            mostrarModalSucesso("Chamado assumido", "Você está atuando neste chamado.");
        });

        btnSalvar.addActionListener(e -> salvar(false));
        btnFinalizar.addActionListener(e -> salvar(true));

        panel.add(new JScrollPane(dados), BorderLayout.CENTER);
        panel.add(botoes, BorderLayout.SOUTH);

        return panel;
    }

    private JScrollPane criarAbaHistorico() {
        areaHistorico.setEditable(false);
        areaHistorico.setFont(new Font("Arial", Font.PLAIN, 13));
        areaHistorico.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        atualizarHistorico();

        JScrollPane scroll = new JScrollPane(areaHistorico);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }

    private void atualizarHistorico() {
        StringBuilder sb = new StringBuilder();

        for (Historico h : service.historicoDoChamado(chamadoId)) {
            sb.append("● ").append(h.getDataHora()).append("\n");
            sb.append("Usuário: ").append(h.getUsuario()).append("\n");
            sb.append("Ação: ").append(h.getAcao()).append("\n");
            sb.append("------------------------------\n");
        }

        areaHistorico.setText(sb.toString());
        areaHistorico.setCaretPosition(0);
    }

    private void salvar(boolean finalizar) {
        double horas = 0;

        if (finalizar) {

            boolean dataVazia = txtDataPrevista.getText()
                    .trim()
                    .replace("/", "")
                    .isEmpty();

            if (txtAtendente.getText().trim().isEmpty()
                    || dataVazia
                    || txtSolucao.getText().trim().isEmpty()
                    || txtHoras.getText().trim().isEmpty()) {

                mostrarModalErro(
                        "Ops! Algo deu errado",
                        "Preencha os campos antes de finalizar."
                );

                return;
            }
        }

        if (!txtHoras.getText().trim().isEmpty()) {
            try {
                horas = Double.parseDouble(
                        txtHoras.getText()
                                .replace(",", ".")
                );

            } catch (NumberFormatException ex) {

                mostrarModalErro(
                        "Ops! Algo deu errado",
                        "Horas gastas deve ser um número."
                );

                return;
            }
        }

        if (finalizar) {

            service.finalizarChamado(
                    chamadoId,
                    txtAtendente.getText(),
                    txtDataPrevista.getText(),
                    txtSolucao.getText(),
                    horas,
                    usuario
            );

            mostrarModalSucesso(
                    "Chamado finalizado",
                    "Chamado finalizado com sucesso."
            );

            dispose();

        } else {

            service.salvarDadosAtendimento(
                    chamadoId,
                    txtAtendente.getText(),
                    txtDataPrevista.getText(),
                    txtSolucao.getText(),
                    horas,
                    usuario
            );

            mostrarModalSucesso(
                    "Dados salvos",
                    "Dados do chamado salvos com sucesso."
            );
        }

        atualizarHistorico();
    }

    private String valorSeguro(String valor) {
        return valor == null ? "" : valor;
    }

    private void addLabel(JPanel p, GridBagConstraints gbc, int y, String label, String valor) {
        JLabel valorLabel = new JLabel(valor == null ? "" : valor);
        valorLabel.setForeground(TEXTO);
        valorLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        addField(p, gbc, y, label, valorLabel);
    }

    private void addField(JPanel p, GridBagConstraints gbc, int y, String label, Component field) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;

        JLabel lbl = new JLabel(label);
        lbl.setForeground(TEXTO);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        p.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        p.add(field, gbc);
    }

    private void addTextAreaReadOnly(JPanel p, GridBagConstraints gbc, int y, String label, String valor) {
        JTextArea area = new JTextArea(valor == null ? "" : valor, 4, 40);
        area.setEditable(false);
        configurarArea(area);
        addTextArea(p, gbc, y, label, area);
    }

    private void addTextArea(JPanel p, GridBagConstraints gbc, int y, String label, JTextArea area) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;

        JLabel lbl = new JLabel(label);
        lbl.setForeground(TEXTO);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        p.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(420, 90));
        scroll.setBorder(BorderFactory.createLineBorder(BORDA));
        p.add(scroll, gbc);
    }

    private void configurarCampo(JTextField campo) {
        campo.setPreferredSize(new Dimension(420, 34));
        campo.setFont(new Font("Arial", Font.PLAIN, 13));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
    }

    private void configurarArea(JTextArea area) {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Arial", Font.PLAIN, 13));
        area.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    }

    private JButton criarBotaoPrincipal(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Arial", Font.BOLD, 12));
        botao.setForeground(Color.WHITE);
        botao.setBackground(VERDE);
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setPreferredSize(new Dimension(130, 28));
        return botao;
    }

    private JButton criarBotaoSecundario(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Arial", Font.BOLD, 12));
        botao.setForeground(TEXTO);
        botao.setBackground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 220)));
        botao.setPreferredSize(new Dimension(110, 28));
        return botao;
    }

    private void mostrarModalSucesso(String tituloTexto, String mensagemTexto) {
        mostrarModalPersonalizado(
                tituloTexto,
                mensagemTexto,
                "/br/com/faculdade/chamados/assets/success.png",
                new Color(205, 235, 205)
        );
    }

    private void mostrarModalErro(String tituloTexto, String mensagemTexto) {
        mostrarModalPersonalizado(
                tituloTexto,
                mensagemTexto,
                "/br/com/faculdade/chamados/assets/danger.png",
                new Color(255, 205, 205)
        );
    }

    private void mostrarModalPersonalizado(String tituloTexto, String mensagemTexto, String caminhoIcone, Color corBolinha) {
        JDialog dialog = new JDialog(this, "Mensagem", true);
        dialog.setSize(330, 270);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout());

        JPanel conteudo = new JPanel();
        conteudo.setBackground(Color.WHITE);
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBorder(BorderFactory.createEmptyBorder(28, 20, 20, 20));

        ImageIcon icon = new ImageIcon(getClass().getResource(caminhoIcone));
        Image img = icon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);

        JLabel icone = new JLabel(new ImageIcon(img));
        icone.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel bolinha = new JPanel(new GridBagLayout());
        bolinha.setBackground(corBolinha);
        bolinha.setMaximumSize(new Dimension(58, 58));
        bolinha.setPreferredSize(new Dimension(58, 58));
        bolinha.add(icone);
        bolinha.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel(tituloTexto);
        titulo.setFont(new Font("Arial", Font.BOLD, 15));
        titulo.setForeground(new Color(70, 70, 70));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel mensagem = new JLabel(mensagemTexto);
        mensagem.setFont(new Font("Arial", Font.PLAIN, 12));
        mensagem.setForeground(new Color(80, 80, 80));
        mensagem.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton ok = criarBotaoPrincipal("OK");
        ok.addActionListener(e -> dialog.dispose());

        JPanel painelBotao = new JPanel();
        painelBotao.setBackground(Color.WHITE);
        painelBotao.add(ok);

        conteudo.add(bolinha);
        conteudo.add(Box.createVerticalStrut(18));
        conteudo.add(titulo);
        conteudo.add(Box.createVerticalStrut(6));
        conteudo.add(mensagem);
        conteudo.add(Box.createVerticalStrut(28));
        conteudo.add(painelBotao);

        dialog.add(conteudo, BorderLayout.CENTER);
        dialog.getRootPane().setDefaultButton(ok);
        dialog.setVisible(true);
    }
}