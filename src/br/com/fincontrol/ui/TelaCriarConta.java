package br.com.fincontrol.ui;

import br.com.fincontrol.dados.ErroBanco;
import br.com.fincontrol.dados.UsuarioDAO;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/** Tela 2 — Criar conta (RF001). */
public class TelaCriarConta extends JPanel {

    private final App app;
    private final FormUsuario form = new FormUsuario(false);
    private final JButton cadastrar = Tema.botaoPrincipal("Cadastrar", KeyEvent.VK_C);

    public TelaCriarConta(App app) {
        super(new GridBagLayout());
        this.app = app;
        setBackground(Tema.FLORESTA);

        JPanel cartao = Tema.cartao();
        cartao.setLayout(new GridBagLayout());
        cartao.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 38, 30, 38));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        g.gridy = 0;
        cartao.add(App.logo(26, Tema.GRAFITE, Tema.RECEITA), g);
        g.gridy = 1;
        g.insets = new Insets(8, 0, 14, 0);
        javax.swing.JLabel titulo = Tema.titulo("Criar conta");
        titulo.setFont(Tema.fonte(java.awt.Font.BOLD, 22));
        cartao.add(titulo, g);
        g.gridy = 2;
        g.insets = new Insets(0, 0, 0, 0);
        cartao.add(form, g);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.TRAILING, 10, 0));
        botoes.setOpaque(false);
        JButton voltar = Tema.botaoSecundario("Voltar", KeyEvent.VK_V);
        voltar.addActionListener(e -> app.mostrarLogin());
        cadastrar.addActionListener(e -> cadastrar());
        botoes.add(voltar);
        botoes.add(cadastrar);
        g.gridy = 3;
        g.insets = new Insets(12, 0, 0, 0);
        cartao.add(botoes, g);

        cartao.setPreferredSize(new java.awt.Dimension(480, cartao.getPreferredSize().height));
        add(cartao);
    }

    private void cadastrar() {
        if (!form.validar(null)) {
            return;
        }
        try {
            new UsuarioDAO().inserir(form.getNome(), form.getEmail(), form.getSenha(), false);
        } catch (ErroBanco ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro no banco de dados", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Conta criada. Agora entre com seu e-mail e senha.",
                "Conta criada", JOptionPane.INFORMATION_MESSAGE);
        app.mostrarLogin();
    }

    public void preparar() {
        form.limpar();
        form.focar();
    }

    public JButton botaoPadrao() {
        return cadastrar;
    }
}
