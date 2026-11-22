package br.com.fincontrol.ui;

import br.com.fincontrol.dados.ErroBanco;
import br.com.fincontrol.dados.UsuarioDAO;
import br.com.fincontrol.model.Usuario;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/** Tela 1 — Login (RF002). */
public class TelaLogin extends JPanel {

    private final App app;
    private final JTextField email = Tema.campo(26);
    private final JPasswordField senha = Tema.senha(26);
    private final JLabel erro = Tema.erro();
    private final JButton entrar = Tema.botaoPrincipal("Entrar", KeyEvent.VK_E);

    public TelaLogin(App app) {
        super(new GridBagLayout());
        this.app = app;
        setBackground(Tema.FLORESTA);

        JPanel cartao = Tema.cartao();
        cartao.setLayout(new GridBagLayout());
        cartao.setBorder(javax.swing.BorderFactory.createEmptyBorder(34, 38, 34, 38));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        int y = 0;

        g.gridy = y++;
        cartao.add(App.logo(28, Tema.GRAFITE, Tema.RECEITA), g);
        g.gridy = y++;
        g.insets = new Insets(2, 0, 20, 0);
        cartao.add(Tema.subtitulo("Controle financeiro pessoal"), g);

        y = linha(cartao, y, Tema.rotulo("E-mail", email, KeyEvent.VK_M), email);
        y = linha(cartao, y, Tema.rotulo("Senha", senha, KeyEvent.VK_S), senha);

        g.gridy = y++;
        g.insets = new Insets(4, 0, 4, 0);
        cartao.add(erro, g);

        entrar.addActionListener(e -> entrar());
        g.gridy = y++;
        g.insets = new Insets(4, 0, 16, 0);
        cartao.add(entrar, g);

        JPanel criar = new JPanel();
        criar.setOpaque(false);
        criar.add(Tema.subtitulo("Não tem conta?"));
        JLabel link = new JLabel("<html><u>Criar conta</u></html>");
        link.setFont(Tema.fonte(Font.BOLD, 14));
        link.setForeground(Tema.PRINCIPAL);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.setFocusable(true);
        link.setToolTipText("Abrir o cadastro de nova conta (Enter ou clique)");
        link.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                app.mostrarCriarConta();
            }
        });
        link.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
                    app.mostrarCriarConta();
                }
            }
        });
        criar.add(link);
        g.gridy = y++;
        g.insets = new Insets(0, 0, 0, 0);
        cartao.add(criar, g);

        cartao.setPreferredSize(new java.awt.Dimension(440, cartao.getPreferredSize().height));
        add(cartao);
    }

    private int linha(JPanel p, int y, JLabel rotulo, JComponent campo) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        g.gridy = y++;
        g.insets = new Insets(8, 0, 6, 0);
        p.add(rotulo, g);
        g.gridy = y++;
        g.insets = new Insets(0, 0, 4, 0);
        p.add(campo, g);
        return y;
    }

    private void entrar() {
        String e = email.getText().trim();
        String s = new String(senha.getPassword());
        if (e.isEmpty() || s.isEmpty()) {
            erro.setText("Preencha o e-mail e a senha.");
            (e.isEmpty() ? email : senha).requestFocusInWindow();
            return;
        }
        Usuario u;
        try {
            u = new UsuarioDAO().autenticar(e, s);
        } catch (ErroBanco ex) {
            erro.setText("<html>" + ex.getMessage() + "<br>Verifique se o MySQL está ligado.</html>");
            return;
        }
        if (u == null) {
            erro.setText("E-mail ou senha incorretos.");
            senha.setText("");
            senha.requestFocusInWindow();
            return;
        }
        app.entrar(u);
    }

    /** Chamado sempre que a tela aparece. */
    public void preparar() {
        senha.setText("");
        erro.setText(" ");
        email.requestFocusInWindow();
    }

    public JButton botaoPadrao() {
        return entrar;
    }
}
