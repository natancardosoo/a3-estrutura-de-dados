package br.com.faculdade.chamados.ui;

import br.com.faculdade.chamados.model.Usuario;
import br.com.faculdade.chamados.service.ChamadoService;

import javax.swing.*;

public class AdminFrame extends JFrame {
    public AdminFrame(ChamadoService service, Usuario usuario) {
        setTitle("Administrador - " + usuario.getNome());
        setSize(950, 560);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        add(new TabelaChamadosPanel(service, usuario, true));
    }
}
