package br.com.faculdade.chamados.ui;

import br.com.faculdade.chamados.model.Usuario;
import br.com.faculdade.chamados.service.ChamadoService;

import javax.swing.*;
import java.awt.*;

public class SolicitarPanel extends JPanel {
    private JTextField txtTitulo = new JTextField();
    private JComboBox<String> cbAplicativo;
    private JTextArea txtDescricao = new JTextArea();

    private final Color FUNDO = new Color(245, 246, 248);
    private final Color CARD = Color.WHITE;
    private final Color VERDE = new Color(106, 132, 62);
    private final Color TEXTO = new Color(18, 40, 70);

    public SolicitarPanel(ChamadoService service, Usuario usuario) {
        setLayout(new BorderLayout());
        setBackground(FUNDO);

        cbAplicativo = new JComboBox<>();
        cbAplicativo.addItem("Selecione o aplicativo...");
        for (String app : service.listarAplicativos()) cbAplicativo.addItem(app);

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(FUNDO);
        container.setBorder(BorderFactory.createEmptyBorder(18, 22, 22, 22));

        JLabel breadcrumb = new JLabel("Início  /  Nova Solicitação");
        breadcrumb.setForeground(TEXTO);
        breadcrumb.setFont(new Font("Arial", Font.PLAIN, 11));
        container.add(breadcrumb, BorderLayout.NORTH);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 233, 238)),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JPanel topo = new JPanel(new GridLayout(2, 1));
        topo.setBackground(CARD);
        JLabel titulo = new JLabel("Detalhes do Chamado");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setForeground(TEXTO);
        JLabel subtitulo = new JLabel("Preencha as informações abaixo para abrir um novo pedido de suporte.");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitulo.setForeground(new Color(80, 98, 120));
        topo.add(titulo);
        topo.add(subtitulo);
        card.add(topo, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(CARD);
        form.setBorder(BorderFactory.createEmptyBorder(25, 0, 15, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtSolicitante = criarCampo();
        txtSolicitante.setText(usuario.getNome());
        txtSolicitante.setEditable(false);

        configurarCampo(txtTitulo);
        txtTitulo.setToolTipText("Ex: Erro ao processar pedido no estoque");
        configurarCombo(cbAplicativo);
        configurarArea(txtDescricao);

        addRow(form, gbc, 0, "Solicitante", txtSolicitante);
        addRow(form, gbc, 1, "Título do problema    ", txtTitulo);
        addRow(form, gbc, 2, "Aplicativo", cbAplicativo);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTH;
        form.add(criarLabel("Descrição"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.ipady = 80;
        form.add(new JScrollPane(txtDescricao), gbc);
        gbc.ipady = 0;

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoes.setBackground(CARD);

        JButton btnDeslogar = new JButton("Deslogar");
        btnDeslogar.setFocusPainted(false);
        btnDeslogar.setBackground(Color.WHITE);
        btnDeslogar.setForeground(TEXTO);
        btnDeslogar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220)),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));

        JButton btnEnviar = new JButton("Enviar Solicitação");
        btnEnviar.setBackground(VERDE);
        btnEnviar.setForeground(Color.WHITE);
        btnEnviar.setFocusPainted(false);
        btnEnviar.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));

        botoes.add(btnDeslogar);
        botoes.add(btnEnviar);

        card.add(form, BorderLayout.CENTER);
        card.add(botoes, BorderLayout.SOUTH);
        container.add(card, BorderLayout.CENTER);
        add(container, BorderLayout.CENTER);

        btnEnviar.addActionListener(e -> {
            String aplicativo = String.valueOf(cbAplicativo.getSelectedItem());
            if (txtTitulo.getText().trim().isEmpty() || cbAplicativo.getSelectedIndex() == 0 || txtDescricao.getText().trim().isEmpty()) {
                mostrarModalErro("Ops! Algo deu errado", "Preencha título, aplicativo e descrição.");
                return;
            }
            service.criarChamado(txtTitulo.getText(), aplicativo, txtDescricao.getText(), usuario);
            limparCampos();
            mostrarModalSucesso("Chamado enviado", "Chamado enviado com status Aberto.");
        });

        btnDeslogar.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);

            if (window != null) {
                window.dispose();
            }

            new LoginFrame().setVisible(true);
        });
    }

    private void limparCampos() {
        txtTitulo.setText("");
        cbAplicativo.setSelectedIndex(0);
        txtDescricao.setText("");
    }

    private JTextField criarCampo() {
        JTextField campo = new JTextField();
        configurarCampo(campo);
        return campo;
    }

    private void configurarCampo(JTextField campo) {
        campo.setPreferredSize(new Dimension(420, 34));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 224, 235)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
    }

    private void configurarCombo(JComboBox<String> combo) {
        combo.setPreferredSize(new Dimension(420, 34));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createLineBorder(new Color(215, 224, 235)));
    }

    private void configurarArea(JTextArea area) {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    }

    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(TEXTO);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        return label;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int y, String label, Component field) {
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; gbc.ipady = 0; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(criarLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(field, gbc);
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
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Mensagem", true);
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

        JButton ok = new JButton("OK");
        ok.setFont(new Font("Arial", Font.BOLD, 12));
        ok.setForeground(Color.WHITE);
        ok.setBackground(VERDE);
        ok.setFocusPainted(false);
        ok.setBorderPainted(false);
        ok.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ok.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        ok.addActionListener(e -> dialog.dispose());

        conteudo.add(bolinha);
        conteudo.add(Box.createVerticalStrut(18));
        conteudo.add(titulo);
        conteudo.add(Box.createVerticalStrut(6));
        conteudo.add(mensagem);
        conteudo.add(Box.createVerticalStrut(28));
        conteudo.add(ok);

        dialog.add(conteudo, BorderLayout.CENTER);
        dialog.getRootPane().setDefaultButton(ok);
        dialog.setVisible(true);
    }
}
