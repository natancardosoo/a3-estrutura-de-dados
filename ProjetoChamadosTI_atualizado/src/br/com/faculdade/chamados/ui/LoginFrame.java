package br.com.faculdade.chamados.ui;

import br.com.faculdade.chamados.model.Usuario;
import br.com.faculdade.chamados.service.ChamadoService;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private final ChamadoService service = new ChamadoService();

    private final Color VERDE = new Color(78, 112, 34);
    private final Color BORDA = new Color(210, 215, 200);
    private final Color FUNDO = new Color(245, 245, 245);
    private final Color TEXTO = new Color(20, 35, 55);

    public LoginFrame() {
        setTitle("Aplicativo de Chamados");
        setSize(520, 620);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel fundo = new JPanel(new GridBagLayout());
        fundo.setBackground(FUNDO);
        add(fundo);

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(440, 540));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        fundo.add(card);

        ImageIcon logoIcon = new ImageIcon(
                getClass().getResource("/br/com/faculdade/chamados/assets/logo.png")
        );

        Image logoImg = logoIcon.getImage().getScaledInstance(
                90,
                90,
                Image.SCALE_SMOOTH
        );

        JLabel logo = new JLabel(new ImageIcon(logoImg));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(logo);

        JLabel appNome = new JLabel("Aplicativo de Chamados");
        appNome.setFont(new Font("Arial", Font.PLAIN, 20));
        appNome.setForeground(TEXTO);
        appNome.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(appNome);

        card.add(Box.createVerticalStrut(35));

        JPanel tituloPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tituloPanel.setBackground(Color.WHITE);
        tituloPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel barra = new JLabel("|");
        barra.setFont(new Font("Arial", Font.BOLD, 26));
        barra.setForeground(VERDE);

        JLabel titulo = new JLabel("Acesso ao Sistema");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setForeground(TEXTO);

        tituloPanel.add(barra);
        tituloPanel.add(Box.createHorizontalStrut(8));
        tituloPanel.add(titulo);
        card.add(tituloPanel);

        card.add(Box.createVerticalStrut(20));

        JLabel lblLogin = criarLabel("LOGIN");
        card.add(lblLogin);
        card.add(Box.createVerticalStrut(6));

        JPanel campoLogin = criarCampoLoginComIcone("Seu usuário", "/br/com/faculdade/chamados/assets/user.png");
        card.add(campoLogin);

        card.add(Box.createVerticalStrut(18));

        JLabel lblSenha = criarLabel("SENHA");
        card.add(lblSenha);
        card.add(Box.createVerticalStrut(6));

        JPanel campoSenha = criarCampoSenhaComIcone("Sua senha", "/br/com/faculdade/chamados/assets/lock.png");
        card.add(campoSenha);

        card.add(Box.createVerticalStrut(28));

        JButton btnEntrar = new JButton("ENTRAR");

        ImageIcon loginIcon = new ImageIcon(
                getClass().getResource("/br/com/faculdade/chamados/assets/login.png")
        );

        Image loginImg = loginIcon.getImage().getScaledInstance(
                16,
                16,
                Image.SCALE_SMOOTH
        );

        btnEntrar.setIcon(new ImageIcon(loginImg));
        btnEntrar.setHorizontalTextPosition(SwingConstants.LEFT);
        btnEntrar.setIconTextGap(10);
        btnEntrar.setFont(new Font("Arial", Font.BOLD, 13));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setBackground(VERDE);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setBorderPainted(false);
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEntrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnEntrar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(btnEntrar);

        card.add(Box.createVerticalStrut(35));

        JLabel linha = new JLabel("────────     ────────");
        linha.setForeground(new Color(210, 210, 210));
        linha.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(linha);

        card.add(Box.createVerticalStrut(18));

        JLabel dica = new JLabel("<html><b>Dica:</b> admin/admin ou solicitante/solicitante</html>", SwingConstants.CENTER);
        dica.setFont(new Font("Arial", Font.PLAIN, 12));
        dica.setOpaque(true);
        dica.setBackground(new Color(235, 235, 235));
        dica.setForeground(TEXTO);
        dica.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        dica.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(dica);

        btnEntrar.addActionListener(e -> entrar());
        getRootPane().setDefaultButton(btnEntrar);
    }

    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(TEXTO);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel criarCampoLoginComIcone(String placeholder, String caminhoIcone) {
        JPanel panel = criarPainelCampo();

        JLabel icone = criarIcone(caminhoIcone);

        txtLogin = new JTextField(placeholder);
        txtLogin.setFont(new Font("Arial", Font.PLAIN, 14));
        txtLogin.setForeground(new Color(90, 100, 115));
        txtLogin.setBorder(BorderFactory.createEmptyBorder());

        txtLogin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (txtLogin.getText().equals(placeholder)) {
                    txtLogin.setText("");
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtLogin.getText().isEmpty()) {
                    txtLogin.setText(placeholder);
                }
            }
        });

        panel.add(icone, BorderLayout.WEST);
        panel.add(txtLogin, BorderLayout.CENTER);

        return panel;
    }

    private void mostrarModalErroLogin() {
        JDialog dialog = new JDialog(this, "Mensagem", true);
        dialog.setSize(330, 270);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout());

        JPanel conteudo = new JPanel();
        conteudo.setBackground(Color.WHITE);
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBorder(BorderFactory.createEmptyBorder(28, 20, 20, 20));

        ImageIcon dangerIcon = new ImageIcon(
                getClass().getResource("/br/com/faculdade/chamados/assets/danger.png")
        );

        Image dangerImg = dangerIcon.getImage().getScaledInstance(
                35,
                35,
                Image.SCALE_SMOOTH
        );

        JLabel icone = new JLabel(new ImageIcon(dangerImg));
        icone.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel bolinha = new JPanel(new GridBagLayout());
        bolinha.setBackground(new Color(255, 205, 205));
        bolinha.setMaximumSize(new Dimension(58, 58));
        bolinha.setPreferredSize(new Dimension(58, 58));
        bolinha.add(icone);
        bolinha.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("Ops! Algo deu errado");
        titulo.setFont(new Font("Arial", Font.BOLD, 15));
        titulo.setForeground(new Color(70, 70, 70));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel mensagem = new JLabel("Login ou senha inválidos.");
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
        ok.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
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

    private JPanel criarCampoSenhaComIcone(String placeholder, String caminhoIcone) {
        JPanel panel = criarPainelCampo();

        JLabel icone = criarIcone(caminhoIcone);

        txtSenha = new JPasswordField(placeholder);
        txtSenha.setEchoChar((char) 0);
        txtSenha.setFont(new Font("Arial", Font.PLAIN, 14));
        txtSenha.setForeground(new Color(90, 100, 115));
        txtSenha.setBorder(BorderFactory.createEmptyBorder());

        txtSenha.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (new String(txtSenha.getPassword()).equals(placeholder)) {
                    txtSenha.setText("");
                    txtSenha.setEchoChar('•');
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (new String(txtSenha.getPassword()).isEmpty()) {
                    txtSenha.setText(placeholder);
                    txtSenha.setEchoChar((char) 0);
                }
            }
        });

        panel.add(icone, BorderLayout.WEST);
        panel.add(txtSenha, BorderLayout.CENTER);

        return panel;
    }

    private JPanel criarPainelCampo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        panel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 44));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        return panel;
    }

    private JLabel criarIcone(String caminhoIcone) {
        JLabel icone = new JLabel();

        java.net.URL url = getClass().getResource(caminhoIcone);

        if (url != null) {
            ImageIcon icon = new ImageIcon(url);
            Image imagem = icon.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
            icone.setIcon(new ImageIcon(imagem));
        } else {
            icone.setText("•");
            icone.setFont(new Font("Arial", Font.BOLD, 18));
            icone.setForeground(VERDE);
        }

        icone.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 10));
        return icone;
    }

    private void entrar() {
        String login = txtLogin.getText().trim();
        String senha = new String(txtSenha.getPassword()).trim();

        if (login.equals("Seu usuário")) {
            login = "";
        }

        if (senha.equals("Sua senha")) {
            senha = "";
        }

        Usuario usuario = service.autenticar(login, senha);

        if (usuario == null) {
            mostrarModalErroLogin();
            return;
        }

        dispose();

        if (usuario.isAdministrador()) {
            new AdminFrame(service, usuario).setVisible(true);
        } else {
            new SolicitanteFrame(service, usuario).setVisible(true);
        }
    }
}