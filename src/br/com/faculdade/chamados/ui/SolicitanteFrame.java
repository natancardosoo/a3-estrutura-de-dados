package br.com.faculdade.chamados.ui;

import br.com.faculdade.chamados.model.Usuario;
import br.com.faculdade.chamados.service.ChamadoService;

import javax.swing.*;

public class SolicitanteFrame extends JFrame {
    public SolicitanteFrame(ChamadoService service, Usuario usuario) {
        setTitle("Solicitante - " + usuario.getNome());
        setSize(900, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Solicitar", new SolicitarPanel(service, usuario));
        abas.addTab("Meus Chamados", new TabelaChamadosPanel(service, usuario, false));
        add(abas);
    }
}
