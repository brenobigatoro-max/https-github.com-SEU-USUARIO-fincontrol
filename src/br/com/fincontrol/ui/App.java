package br.com.fincontrol.ui;

import br.com.fincontrol.model.Usuario;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Janela principal do FinControl. Troca entre as telas com um CardLayout:
 * Login, Criar conta e a área logada (com menu lateral).
 */
public class App extends JFrame {

    private final CardLayout cartoes = new CardLayout();
    private final JPanel raiz = new JPanel(cartoes);
    private final TelaLogin login;
    private final TelaCriarConta criarConta;
    private final AreaLogada area;

    public App() {
        super("FinControl");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setSize(1280, 800);
        setLocationRelativeTo(null);

        login = new TelaLogin(this);
        criarConta = new TelaCriarConta(this);
        area = new AreaLogada(this);
        raiz.add(login, "login");
        raiz.add(criarConta, "criar");
        raiz.add(area, "area");
        setContentPane(raiz);
        mostrarLogin();
        javax.swing.SwingUtilities.invokeLater(this::verificarBanco);
    }

    /** Avisa logo na abertura se o MySQL não estiver acessível. */
    private void verificarBanco() {
        String problema = br.com.fincontrol.dados.ConexaoBD.testar();
        if (problema != null) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Não foi possível conectar ao banco de dados MySQL.\n\n"
                    + "Confira se:\n"
                    + "• o MySQL está ligado;\n"
                    + "• o script sql/fincontrol.sql foi executado no MySQL Workbench;\n"
                    + "• os dados do arquivo db.properties estão corretos.\n\n"
                    + "Detalhe: " + problema,
                    "Banco de dados indisponível", javax.swing.JOptionPane.WARNING_MESSAGE);
        }
    }

    public void mostrarLogin() {
        setTitle("FinControl — Login");
        cartoes.show(raiz, "login");
        definirBotaoPadrao(login.botaoPadrao());
        login.preparar();
    }

    public void mostrarCriarConta() {
        setTitle("FinControl — Criar conta");
        cartoes.show(raiz, "criar");
        definirBotaoPadrao(criarConta.botaoPadrao());
        criarConta.preparar();
    }

    public void entrar(Usuario usuario) {
        area.iniciar(usuario);
        cartoes.show(raiz, "area");
    }

    /** O botão padrão é acionado pela tecla Enter (acessibilidade pelo teclado). */
    public void definirBotaoPadrao(JButton botao) {
        getRootPane().setDefaultButton(botao);
    }

    /** Logo "FinControl", com "Control" em outra cor. */
    public static JLabel logo(int tamanho, Color corFin, Color corControl) {
        String hex = String.format("#%06X", corControl.getRGB() & 0xFFFFFF);
        JLabel l = new JLabel("<html>Fin<span style='color:" + hex + "'>Control</span></html>");
        l.setFont(Tema.fonte(Font.BOLD, tamanho));
        l.setForeground(corFin);
        return l;
    }
}
