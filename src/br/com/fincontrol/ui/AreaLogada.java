package br.com.fincontrol.ui;

import br.com.fincontrol.model.Administrador;
import br.com.fincontrol.model.Usuario;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Área depois do login: menu lateral à esquerda e a tela escolhida à direita.
 */
public class AreaLogada extends JPanel {

    public static final String PAINEL = "Painel financeiro";
    public static final String NOVA = "Nova transação";
    public static final String USUARIOS = "Gerenciar usuários";

    private final App app;
    private final CardLayout cartoes = new CardLayout();
    private final JPanel conteudo = new JPanel(cartoes);
    private final Map<String, JButton> itensMenu = new LinkedHashMap<>();
    private final JLabel nomeUsuario = new JLabel();
    private final JLabel perfilUsuario = new JLabel();
    private final TelaPainel painel;
    private final TelaNovaTransacao novaTransacao;
    private final TelaUsuarios usuarios;
    private Usuario usuario;

    public AreaLogada(App app) {
        super(new BorderLayout());
        this.app = app;
        painel = new TelaPainel(this);
        novaTransacao = new TelaNovaTransacao(this);
        usuarios = new TelaUsuarios(this);
        conteudo.add(painel, PAINEL);
        conteudo.add(novaTransacao, NOVA);
        conteudo.add(usuarios, USUARIOS);
        add(criarMenu(), BorderLayout.WEST);
        add(conteudo, BorderLayout.CENTER);
    }

    private JPanel criarMenu() {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(Tema.FLORESTA);
        menu.setPreferredSize(new Dimension(230, 0));
        menu.setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));

        JLabel logo = App.logo(22, Tema.BRANCO, Tema.VERDE_CLARO);
        logo.setBorder(BorderFactory.createEmptyBorder(0, 22, 22, 0));
        logo.setAlignmentX(LEFT_ALIGNMENT);
        menu.add(logo);

        adicionarItem(menu, PAINEL, KeyEvent.VK_P);
        adicionarItem(menu, NOVA, KeyEvent.VK_T);
        adicionarItem(menu, USUARIOS, KeyEvent.VK_G);
        JButton sair = itemMenu("Sair", KeyEvent.VK_R);
        sair.addActionListener(e -> sair());
        menu.add(sair);

        menu.add(Box.createVerticalGlue());

        JPanel rodape = new JPanel();
        rodape.setLayout(new BoxLayout(rodape, BoxLayout.Y_AXIS));
        rodape.setBackground(Tema.FLORESTA);
        rodape.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(0x2C4A41)),
                BorderFactory.createEmptyBorder(14, 22, 16, 22)));
        rodape.setAlignmentX(LEFT_ALIGNMENT);
        rodape.setMaximumSize(new Dimension(230, 80));
        nomeUsuario.setFont(Tema.fonte(Font.BOLD, 14));
        nomeUsuario.setForeground(Tema.BRANCO);
        perfilUsuario.setFont(Tema.fonte(Font.PLAIN, 13));
        perfilUsuario.setForeground(Tema.MENU_TEXTO);
        rodape.add(nomeUsuario);
        rodape.add(perfilUsuario);
        menu.add(rodape);
        return menu;
    }

    private void adicionarItem(JPanel menu, String nome, int atalho) {
        JButton b = itemMenu(nome, atalho);
        b.addActionListener(e -> irPara(nome));
        itensMenu.put(nome, b);
        menu.add(b);
    }

    private JButton itemMenu(String texto, int atalho) {
        JButton b = new JButton(texto);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFont(Tema.fonte(Font.PLAIN, 15));
        b.setForeground(Tema.MENU_TEXTO);
        b.setBackground(Tema.FLORESTA);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setFocusPainted(true);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, Tema.FLORESTA),
                BorderFactory.createEmptyBorder(12, 18, 12, 10)));
        b.setMaximumSize(new Dimension(230, 46));
        b.setAlignmentX(LEFT_ALIGNMENT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMnemonic(atalho);
        return b;
    }

    private void marcarAtivo(String ativo) {
        for (Map.Entry<String, JButton> e : itensMenu.entrySet()) {
            boolean on = e.getKey().equals(ativo);
            JButton b = e.getValue();
            b.setBackground(on ? Tema.PRINCIPAL : Tema.FLORESTA);
            b.setForeground(on ? Tema.BRANCO : Tema.MENU_TEXTO);
            b.setFont(Tema.fonte(on ? Font.BOLD : Font.PLAIN, 15));
            b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 4, 0, 0, on ? Tema.VERDE_CLARO : Tema.FLORESTA),
                    BorderFactory.createEmptyBorder(12, 18, 12, 10)));
        }
    }

    /** Chamado logo depois do login. */
    public void iniciar(Usuario u) {
        this.usuario = u;
        nomeUsuario.setText(u.getNome());
        perfilUsuario.setText(u.getPerfil());
        // RN07: só o administrador vê a área de usuários
        itensMenu.get(USUARIOS).setVisible(u instanceof Administrador);
        painel.limparFiltro();
        irPara(PAINEL);
    }

    /** Atualiza o nome mostrado no rodapé do menu (depois de editar a própria conta). */
    public void atualizarRodape() {
        nomeUsuario.setText(usuario.getNome());
    }

    public void irPara(String tela) {
        if (USUARIOS.equals(tela) && !(usuario instanceof Administrador)) {
            tela = PAINEL;
        }
        marcarAtivo(tela);
        app.setTitle("FinControl — " + tela);
        switch (tela) {
            case NOVA -> {
                novaTransacao.preparar();
                app.definirBotaoPadrao(novaTransacao.botaoPadrao());
            }
            case USUARIOS -> {
                usuarios.atualizar();
                app.definirBotaoPadrao(null);
            }
            default -> {
                painel.atualizar();
                app.definirBotaoPadrao(painel.botaoPadrao());
            }
        }
        cartoes.show(conteudo, tela);
    }

    private void sair() {
        usuario = null;
        app.mostrarLogin();
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
