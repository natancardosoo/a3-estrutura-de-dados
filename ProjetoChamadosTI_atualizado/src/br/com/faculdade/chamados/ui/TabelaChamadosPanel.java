package br.com.faculdade.chamados.ui;

import br.com.faculdade.chamados.model.Chamado;
import br.com.faculdade.chamados.model.Usuario;
import br.com.faculdade.chamados.service.ChamadoService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TabelaChamadosPanel extends JPanel {
    private ChamadoService service;
    private Usuario usuario;
    private boolean admin;
    private JTable tabela;
    private DefaultTableModel model;

    private final Color FUNDO = new Color(245, 246, 248);
    private final Color CARD = Color.WHITE;
    private final Color VERDE = new Color(78, 112, 34);
    private final Color TEXTO = new Color(20, 35, 55);
    private final Color BORDA = new Color(225, 228, 232);

    public TabelaChamadosPanel(ChamadoService service, Usuario usuario, boolean admin) {
        this.service = service;
        this.usuario = usuario;
        this.admin = admin;

        setLayout(new BorderLayout());
        setBackground(FUNDO);
        setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        model = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Título",
                        "Solicitante",
                        "Data Solicitação",
                        "Atendente",
                        "Previsão",
                        "Status"
                },
                0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(model);
        configurarTabela();

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(BORDA));
        scroll.getViewport().setBackground(CARD);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        card.add(scroll, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        botoes.setBackground(CARD);

        JButton btnDeslogar = criarBotaoSecundario("Deslogar");
        JButton btnAtualizar = criarBotaoPrincipal("Atualizar");
        JButton btnAbrir = criarBotaoPrincipal(admin ? "Abrir Chamado" : "Ver Chamado");

        botoes.add(btnDeslogar);
        botoes.add(btnAtualizar);
        botoes.add(btnAbrir);

        card.add(botoes, BorderLayout.SOUTH);
        add(card, BorderLayout.CENTER);

        btnDeslogar.addActionListener(e -> deslogar());
        btnAtualizar.addActionListener(e -> carregarTabela());
        btnAbrir.addActionListener(e -> abrirSelecionado());

        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabela.getSelectedRow() >= 0) {
                    abrirSelecionado();
                }
            }
        });

        carregarTabela();
    }

    private void configurarTabela() {
        tabela.setRowHeight(52);
        tabela.setFont(new Font("Arial", Font.PLAIN, 12));
        tabela.setForeground(TEXTO);
        tabela.setBackground(CARD);
        tabela.setGridColor(new Color(230, 230, 230));
        tabela.setShowHorizontalLines(true);
        tabela.setShowVerticalLines(false);
        tabela.setSelectionBackground(new Color(232, 238, 222));
        tabela.setSelectionForeground(TEXTO);
        tabela.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = tabela.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 46));
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(TEXTO);
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDA));

        tabela.getColumnModel().getColumn(0).setPreferredWidth(60);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(230);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(130);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(130);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(130);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(120);
        tabela.getColumnModel().getColumn(6).setPreferredWidth(120);

        DefaultTableCellRenderer alinhamento = new DefaultTableCellRenderer();
        alinhamento.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        alinhamento.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(alinhamento);
        }

        tabela.getColumnModel().getColumn(6).setCellRenderer(new StatusRenderer());
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
        botao.setMaximumSize(new Dimension(130, 28));
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
        botao.setMaximumSize(new Dimension(110, 28));
        return botao;
    }

    private void carregarTabela() {
        model.setRowCount(0);

        List<Chamado> chamados = admin
                ? service.listarTodos()
                : service.listarPorSolicitante(usuario.getNome());

        for (Chamado c : chamados) {
            model.addRow(new Object[]{
                    "#" + c.getId(),
                    c.getTitulo(),
                    c.getSolicitante(),
                    c.getDataSolicitacao(),
                    tratarVazio(c.getAtendente(), "Não atribuído"),
                    tratarVazio(c.getDataPrevistaEncerramento(), "Pendente"),
                    c.getStatus()
            });
        }
    }

    private String tratarVazio(String valor, String textoPadrao) {
        if (valor == null || valor.trim().isEmpty()) {
            return textoPadrao;
        }

        return valor;
    }

    private void abrirSelecionado() {
        int row = tabela.getSelectedRow();

        if (row < 0) {
            mostrarModalErro(
                    "Ops! Algo deu errado",
                    "Selecione um chamado."
            );
            return;
        }

        int modelRow = tabela.convertRowIndexToModel(row);
        String idTexto = model.getValueAt(modelRow, 0).toString().replace("#", "");
        int id = Integer.parseInt(idTexto);

        DetalheChamadoDialog dialog = new DetalheChamadoDialog(service, usuario, id, admin);
        dialog.setVisible(true);

        carregarTabela();
    }

    private void deslogar() {
        Window window = SwingUtilities.getWindowAncestor(this);

        if (window != null) {
            window.dispose();
        }

        new LoginFrame().setVisible(true);
    }

    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            JLabel label = new JLabel(value == null ? "" : value.toString().toUpperCase(), SwingConstants.CENTER);
            label.setOpaque(true);
            label.setFont(new Font("Arial", Font.BOLD, 10));
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            String status = label.getText();

            if (status.contains("FINALIZADO") || status.contains("CONCLU")) {
                label.setBackground(new Color(72, 96, 36));
                label.setForeground(Color.WHITE);
            } else if (status.contains("ATENDIMENTO")) {
                label.setBackground(new Color(72, 96, 36));
                label.setForeground(new Color(210, 230, 170));
            } else if (status.contains("ABERTO")) {
                label.setBackground(new Color(225, 225, 225));
                label.setForeground(new Color(45, 45, 45));
            } else {
                label.setBackground(new Color(230, 230, 230));
                label.setForeground(TEXTO);
            }

            JPanel wrapper = new JPanel(new GridBagLayout());
            wrapper.setBackground(isSelected ? table.getSelectionBackground() : CARD);

            wrapper.setBorder(BorderFactory.createMatteBorder(
                    0,
                    0,
                    1,
                    0,
                    new Color(230, 230, 230)
            ));

            wrapper.add(label);

            return wrapper;
        }
    }

    private void mostrarModalErro(String tituloTexto, String mensagemTexto) {
        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Mensagem",
                true
        );

        dialog.setSize(330, 270);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout());

        JPanel conteudo = new JPanel();
        conteudo.setBackground(Color.WHITE);
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBorder(BorderFactory.createEmptyBorder(28, 20, 20, 20));

        ImageIcon icon = new ImageIcon(
                getClass().getResource("/br/com/faculdade/chamados/assets/danger.png")
        );

        Image img = icon.getImage().getScaledInstance(
                35,
                35,
                Image.SCALE_SMOOTH
        );

        JLabel icone = new JLabel(new ImageIcon(img));
        icone.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel bolinha = new JPanel(new GridBagLayout());
        bolinha.setBackground(new Color(255, 205, 205));
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
        ok.setPreferredSize(new Dimension(100, 28));
        ok.setMaximumSize(new Dimension(100, 28));

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