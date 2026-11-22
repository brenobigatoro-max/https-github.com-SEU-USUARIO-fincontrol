package br.com.fincontrol.ui;

import br.com.fincontrol.model.Transacao;
import br.com.fincontrol.model.Usuario;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/** Tela 3 — Painel financeiro: saldo (RF005), listagem (RF006) e filtro por período (RF007). */
public class TelaPainel extends JPanel {

    private final AreaLogada area;
    private final JLabel ola = Tema.titulo("Olá");
    private final JLabel periodo = Tema.subtitulo(" ");
    private final JLabel saldo = valorCartao(Tema.GRAFITE);
    private final JLabel receitas = valorCartao(Tema.RECEITA);
    private final JLabel despesas = valorCartao(Tema.DESPESA);
    private final JTextField de = Tema.campo(9);
    private final JTextField ate = Tema.campo(9);
    private final JLabel erroFiltro = Tema.erro();
    private final JButton filtrar = Tema.botaoSecundario("Filtrar", KeyEvent.VK_F);
    private final JLabel vazio = Tema.dica("Nenhuma transação no período. Use \"+ Nova transação\" para registrar a primeira.");
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Data", "Descrição", "Categoria", "Tipo", "Valor"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);

    public TelaPainel(AreaLogada area) {
        this.area = area;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Tema.GELO);
        setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));

        add(alinhado(ola));
        add(alinhado(periodo));
        add(Box.createVerticalStrut(18));

        JPanel cartoes = new JPanel(new GridLayout(1, 3, 16, 0));
        cartoes.setOpaque(false);
        cartoes.add(cartao("SALDO ATUAL", saldo));
        cartoes.add(cartao("RECEITAS NO PERÍODO", receitas));
        cartoes.add(cartao("DESPESAS NO PERÍODO", despesas));
        add(alinhado(cartoes));
        add(Box.createVerticalStrut(18));

        add(alinhado(criarFiltro()));
        add(Box.createVerticalStrut(16));
        add(alinhado(criarTabela()));
        add(Box.createVerticalStrut(6));
        add(alinhado(vazio));
    }

    private static JLabel valorCartao(Color cor) {
        JLabel l = new JLabel("R$ 0,00");
        l.setFont(Tema.fonte(Font.BOLD, 28));
        l.setForeground(cor);
        return l;
    }

    private JPanel cartao(String titulo, JLabel valor) {
        JPanel p = Tema.cartao();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(titulo);
        t.setFont(Tema.fonte(Font.BOLD, 13));
        t.setForeground(Tema.CINZA);
        p.add(t);
        p.add(Box.createVerticalStrut(6));
        p.add(valor);
        return p;
    }

    private JPanel criarFiltro() {
        JPanel p = Tema.cartao();
        p.setLayout(new BorderLayout());
        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 0));
        esquerda.setOpaque(false);
        esquerda.add(Tema.rotulo("De", de, KeyEvent.VK_D));
        esquerda.add(de);
        esquerda.add(Tema.rotulo("Até", ate, KeyEvent.VK_A));
        esquerda.add(ate);
        filtrar.addActionListener(e -> atualizar());
        esquerda.add(filtrar);
        JButton limpar = Tema.botaoSecundario("Ver tudo", KeyEvent.VK_V);
        limpar.setToolTipText("Mostrar todas as transações, sem filtro de data");
        limpar.addActionListener(e -> {
            limparFiltro();
            atualizar();
        });
        esquerda.add(limpar);
        esquerda.add(Tema.dica("Formato: dd/mm/aaaa"));

        JButton nova = Tema.botaoPrincipal("+ Nova transação", KeyEvent.VK_N);
        nova.addActionListener(e -> area.irPara(AreaLogada.NOVA));
        JPanel direita = new JPanel(new FlowLayout(FlowLayout.TRAILING, 0, 0));
        direita.setOpaque(false);
        direita.add(nova);

        p.add(esquerda, BorderLayout.WEST);
        p.add(direita, BorderLayout.EAST);
        erroFiltro.setBorder(BorderFactory.createEmptyBorder(6, 10, 0, 0));
        p.add(erroFiltro, BorderLayout.SOUTH);
        de.setToolTipText("Data inicial, no formato dd/mm/aaaa");
        ate.setToolTipText("Data final, no formato dd/mm/aaaa");
        return p;
    }

    private JScrollPane criarTabela() {
        tabela.setFont(Tema.fonte(Font.PLAIN, 15));
        tabela.setRowHeight(38);
        tabela.setShowVerticalLines(false);
        tabela.setGridColor(new Color(0xE1E8E4));
        tabela.setSelectionBackground(new Color(0xEEF6F2));
        tabela.setSelectionForeground(Tema.GRAFITE);
        tabela.setFillsViewportHeight(true);
        tabela.getAccessibleContext().setAccessibleName("Transações do período");
        tabela.setDefaultRenderer(Object.class, Tema.celula());
        tabela.getTableHeader().setFont(Tema.fonte(Font.BOLD, 13));
        tabela.getTableHeader().setBackground(Tema.CABECALHO);
        tabela.getTableHeader().setForeground(Tema.GRAFITE);
        tabela.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer tipo = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foco, int l, int c) {
                super.getTableCellRendererComponent(t, v, sel, foco, l, c);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                setFont(Tema.fonte(Font.BOLD, 14));
                setForeground("Receita".equals(v) ? Tema.RECEITA : Tema.DESPESA);
                return this;
            }
        };
        DefaultTableCellRenderer valor = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foco, int l, int c) {
                super.getTableCellRendererComponent(t, v, sel, foco, l, c);
                setHorizontalAlignment(SwingConstants.RIGHT);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                setFont(Tema.fonte(Font.BOLD, 15));
                // Cor + sinal: quem não distingue verde de vermelho entende pelo + e pelo −
                setForeground(String.valueOf(v).startsWith("+") ? Tema.RECEITA : Tema.DESPESA);
                return this;
            }
        };
        tabela.getColumnModel().getColumn(3).setCellRenderer(tipo);
        tabela.getColumnModel().getColumn(4).setCellRenderer(valor);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(110);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(320);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(160);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(110);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(150);

        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setBorder(BorderFactory.createLineBorder(Tema.LINHA));
        rolagem.getViewport().setBackground(Tema.BRANCO);
        return rolagem;
    }

    private static JPanel alinhado(java.awt.Component c) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(c, BorderLayout.CENTER);
        p.setAlignmentX(LEFT_ALIGNMENT);
        if (!(c instanceof JScrollPane)) {
            p.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        }
        return p;
    }

    public void limparFiltro() {
        de.setText("");
        ate.setText("");
        Tema.marcarErro(de, erroFiltro, null);
    }

    /** Recalcula os cartões e a tabela de acordo com o filtro (RF005, RF006, RF007). */
    public void atualizar() {
        Usuario u = area.getUsuario();
        if (u == null) {
            return;
        }
        ola.setText("Olá, " + u.getNome().split(" ")[0]);

        LocalDate inicio;
        LocalDate fim;
        try {
            inicio = de.getText().isBlank() ? null : LocalDate.parse(de.getText().trim(), Tema.DATA);
            Tema.marcarErro(de, erroFiltro, null);
        } catch (DateTimeParseException ex) {
            Tema.marcarErro(de, erroFiltro, "Data inicial inválida. Use o formato dd/mm/aaaa.");
            return;
        }
        try {
            fim = ate.getText().isBlank() ? null : LocalDate.parse(ate.getText().trim(), Tema.DATA);
            Tema.marcarErro(ate, erroFiltro, null);
        } catch (DateTimeParseException ex) {
            Tema.marcarErro(ate, erroFiltro, "Data final inválida. Use o formato dd/mm/aaaa.");
            return;
        }
        if (inicio != null && fim != null && fim.isBefore(inicio)) {
            Tema.marcarErro(ate, erroFiltro, "A data final precisa ser igual ou depois da inicial.");
            return;
        }

        List<Transacao> lista = new ArrayList<>();
        for (Transacao t : u.getTransacoes()) {
            boolean depois = inicio == null || !t.getData().isBefore(inicio);
            boolean antes = fim == null || !t.getData().isAfter(fim);
            if (depois && antes) {
                lista.add(t);
            }
        }
        lista.sort(Comparator.comparing(Transacao::getData).thenComparing(Transacao::getId));

        double totalReceitas = 0;
        double totalDespesas = 0;
        modelo.setRowCount(0);
        for (Transacao t : lista) {
            boolean receita = t.getTipo().equalsIgnoreCase("Receita");
            if (receita) {
                totalReceitas += t.getValor();
            } else {
                totalDespesas += t.getValor();
            }
            modelo.addRow(new Object[]{
                t.getData().format(Tema.DATA), t.getDescricao(), t.getCategoria().getNome(), t.getTipo(),
                (receita ? "+ " : "− ") + Tema.moeda(t.getValor())
            });
        }

        saldo.setText(Tema.moeda(u.calcularSaldo()));
        receitas.setText("+ " + Tema.moeda(totalReceitas));
        despesas.setText("− " + Tema.moeda(totalDespesas));
        if (inicio == null && fim == null) {
            periodo.setText("Mostrando todas as transações");
        } else {
            periodo.setText("Período: " + (inicio == null ? "início" : inicio.format(Tema.DATA))
                    + " até " + (fim == null ? "hoje" : fim.format(Tema.DATA)));
        }
        vazio.setVisible(lista.isEmpty());
    }

    public JButton botaoPadrao() {
        return filtrar;
    }
}
