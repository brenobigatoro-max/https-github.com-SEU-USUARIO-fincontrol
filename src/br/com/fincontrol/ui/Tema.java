package br.com.fincontrol.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 * Cores, fontes e componentes padronizados, conforme o guia visual da Etapa 2.
 * Todas as telas usam esta classe, para manter o visual igual em todo o sistema.
 */
public final class Tema {

    // Esquema de cores (contraste AA medido na Etapa 2)
    public static final Color FLORESTA = new Color(0x17332B);
    public static final Color PRINCIPAL = new Color(0x1F5C4A);
    public static final Color RECEITA = new Color(0x1F7A4A);
    public static final Color DESPESA = new Color(0xB3362B);
    public static final Color GRAFITE = new Color(0x1C2622);
    public static final Color CINZA = new Color(0x5A6863);
    public static final Color GELO = new Color(0xF3F6F5);
    public static final Color BORDA = new Color(0x9AAAA3);
    public static final Color LINHA = new Color(0xD3DDD8);
    public static final Color VERDE_CLARO = new Color(0x8FD3B4);
    public static final Color CABECALHO = new Color(0xE4EFEA);
    public static final Color MENU_TEXTO = new Color(0xC9D6D1);
    public static final Color BRANCO = Color.WHITE;

    public static final Locale BRASIL = Locale.forLanguageTag("pt-BR");
    public static final DateTimeFormatter DATA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private static final String FONTE = escolherFonte();

    private static final Border BORDA_NORMAL = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDA, 1, true), BorderFactory.createEmptyBorder(8, 10, 8, 10));
    private static final Border BORDA_FOCO = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRINCIPAL, 2, true), BorderFactory.createEmptyBorder(7, 9, 7, 9));
    private static final Border BORDA_ERRO = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DESPESA, 2, true), BorderFactory.createEmptyBorder(7, 9, 7, 9));

    private Tema() {
    }

    private static String escolherFonte() {
        List<String> instaladas = Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        for (String nome : new String[]{"Segoe UI", "Arial"}) {
            if (instaladas.contains(nome)) {
                return nome;
            }
        }
        return Font.SANS_SERIF;
    }

    public static Font fonte(int estilo, float tamanho) {
        return new Font(FONTE, estilo, Math.round(tamanho));
    }

    public static JLabel titulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fonte(Font.BOLD, 26));
        l.setForeground(GRAFITE);
        return l;
    }

    public static JLabel subtitulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fonte(Font.PLAIN, 14));
        l.setForeground(CINZA);
        return l;
    }

    /** Rótulo sempre visível e ligado ao campo (leitores de tela e Alt + letra). */
    public static JLabel rotulo(String texto, JComponent campo, int atalho) {
        JLabel l = new JLabel(texto);
        l.setFont(fonte(Font.BOLD, 14));
        l.setForeground(GRAFITE);
        l.setLabelFor(campo);
        if (atalho != 0) {
            l.setDisplayedMnemonic(atalho);
        }
        return l;
    }

    public static JLabel dica(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fonte(Font.PLAIN, 13));
        l.setForeground(CINZA);
        return l;
    }

    /** Mensagem de erro em texto, mostrada abaixo do campo. Começa vazia. */
    public static JLabel erro() {
        JLabel l = new JLabel(" ");
        l.setFont(fonte(Font.BOLD, 13));
        l.setForeground(DESPESA);
        return l;
    }

    public static JTextField campo(int colunas) {
        return prepararCampo(new JTextField(colunas));
    }

    public static JPasswordField senha(int colunas) {
        return (JPasswordField) prepararCampo(new JPasswordField(colunas));
    }

    private static JTextField prepararCampo(JTextField c) {
        c.setFont(fonte(Font.PLAIN, 15));
        c.setForeground(GRAFITE);
        c.setBorder(BORDA_NORMAL);
        c.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!temErro(c)) {
                    c.setBorder(BORDA_FOCO);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!temErro(c)) {
                    c.setBorder(BORDA_NORMAL);
                }
            }
        });
        return c;
    }

    private static boolean temErro(JComponent c) {
        return Boolean.TRUE.equals(c.getClientProperty("erro"));
    }

    /** Liga ou desliga o estado de erro de um campo, com a mensagem em texto ao lado. */
    public static void marcarErro(JComponent campo, JLabel mensagem, String texto) {
        boolean erro = texto != null;
        campo.putClientProperty("erro", erro);
        campo.setBorder(erro ? BORDA_ERRO : (campo.isFocusOwner() ? BORDA_FOCO : BORDA_NORMAL));
        mensagem.setText(erro ? texto : " ");
    }

    public static JButton botaoPrincipal(String texto, int atalho) {
        return botao(texto, atalho, PRINCIPAL, BRANCO, PRINCIPAL);
    }

    public static JButton botaoSecundario(String texto, int atalho) {
        return botao(texto, atalho, BRANCO, PRINCIPAL, PRINCIPAL);
    }

    public static JButton botaoPerigo(String texto, int atalho) {
        return botao(texto, atalho, BRANCO, DESPESA, DESPESA);
    }

    private static JButton botao(String texto, int atalho, Color fundo, Color textoCor, Color borda) {
        JButton b = new JButton(texto);
        b.setFont(fonte(Font.BOLD, 15));
        b.setBackground(fundo);
        b.setForeground(textoCor);
        b.setOpaque(true);
        b.setFocusPainted(true);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borda, 1, true), BorderFactory.createEmptyBorder(9, 18, 9, 18)));
        b.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        if (atalho != 0) {
            b.setMnemonic(atalho);
        }
        return b;
    }

    /** Painel branco com borda fina, usado como cartão. */
    public static JPanel cartao() {
        JPanel p = new JPanel();
        p.setBackground(BRANCO);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINHA, 1, true), BorderFactory.createEmptyBorder(18, 20, 18, 20)));
        return p;
    }

    /** Renderizador padrão das tabelas, com espaço interno nas células. */
    public static javax.swing.table.DefaultTableCellRenderer celula() {
        return new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable t, Object v,
                    boolean sel, boolean foco, int l, int c) {
                super.getTableCellRendererComponent(t, v, sel, foco, l, c);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return this;
            }
        };
    }

    public static String moeda(double valor) {
        return String.format(BRASIL, "R$ %,.2f", valor);
    }

    /** Aceita "180,50", "1.234,56", "180.50" e "R$ 180,50". Lança NumberFormatException se não for número. */
    /** Maior valor aceito: o mesmo limite da coluna DECIMAL(10,2) do banco. */
    public static final java.math.BigDecimal VALOR_MAXIMO = new java.math.BigDecimal("99999999.99");

    /**
     * Lê um valor em reais, como 180,50 / 1.250,00 / 75.9 (RN03).
     * Aceita só dígitos, com no máximo 2 casas decimais, e recusa textos como NaN ou Infinity.
     * Lança NumberFormatException com a mensagem que deve aparecer no campo.
     */
    public static double lerValor(String texto) {
        String t = texto.replace("R$", "").replace(" ", "").trim();
        if (t.isEmpty()) {
            throw new NumberFormatException("Informe o valor, como 180,50.");
        }
        String normal;
        if (t.matches("\\d{1,3}(\\.\\d{3})+(,\\d+)?") || t.matches("\\d+(,\\d+)?")) {
            normal = t.replace(".", "").replace(",", "."); // 1.250,00 ou 180,50
        } else if (t.matches("\\d+\\.\\d{1,2}")) {
            normal = t;                                     // 75.9
        } else {
            throw new NumberFormatException("Digite só números, como 180,50.");
        }
        java.math.BigDecimal v = new java.math.BigDecimal(normal);
        if (v.scale() > 2) {
            throw new NumberFormatException("Use no máximo 2 casas decimais (centavos).");
        }
        if (v.compareTo(VALOR_MAXIMO) > 0) {
            throw new NumberFormatException("O valor máximo é R$ 99.999.999,99.");
        }
        return v.doubleValue();
    }
}
