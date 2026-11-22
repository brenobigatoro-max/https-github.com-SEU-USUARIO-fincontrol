package br.com.fincontrol.ui;

import br.com.fincontrol.dados.CategoriaDAO;
import br.com.fincontrol.dados.ErroBanco;
import br.com.fincontrol.dados.TransacaoDAO;
import br.com.fincontrol.model.Categoria;
import br.com.fincontrol.model.Usuario;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/** Tela 4 — Nova transação: cadastro de receitas (RF003) e despesas (RF004). */
public class TelaNovaTransacao extends JPanel {

    private final AreaLogada area;
    private final JRadioButton receita = radio("Receita", KeyEvent.VK_R);
    private final JRadioButton despesa = radio("Despesa", KeyEvent.VK_D);
    private final JTextField descricao = Tema.campo(30);
    private final JTextField valor = Tema.campo(12);
    private final JTextField data = Tema.campo(12);
    private final JComboBox<Categoria> categoria = new JComboBox<>();
    private final JLabel erroDescricao = Tema.erro();
    private final JLabel erroValor = Tema.erro();
    private final JLabel erroData = Tema.erro();
    private final JLabel erroCategoria = Tema.erro();
    private final JButton salvar = Tema.botaoPrincipal("Salvar", KeyEvent.VK_S);

    public TelaNovaTransacao(AreaLogada area) {
        super(new BorderLayout());
        this.area = area;
        setBackground(Tema.GELO);
        setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));

        JPanel topo = new JPanel();
        topo.setOpaque(false);
        topo.setLayout(new javax.swing.BoxLayout(topo, javax.swing.BoxLayout.Y_AXIS));
        topo.add(Tema.titulo("Nova transação"));
        topo.add(Tema.subtitulo("Registre uma receita ou uma despesa."));
        topo.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        add(topo, BorderLayout.NORTH);

        JPanel form = Tema.cartao();
        form.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.anchor = GridBagConstraints.LINE_START;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridwidth = 2;
        g.weightx = 1;
        int y = 0;

        // Tipo: escolhido primeiro, porque define as categorias (RN04)
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(receita);
        grupo.add(despesa);
        receita.addActionListener(e -> carregarCategorias());
        despesa.addActionListener(e -> carregarCategorias());
        JPanel tipos = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        tipos.setOpaque(false);
        tipos.add(receita);
        tipos.add(javax.swing.Box.createHorizontalStrut(12));
        tipos.add(despesa);
        JLabel rotuloTipo = Tema.rotulo("Tipo", receita, 0);
        y = linha(form, y, rotuloTipo, tipos, null, null);

        y = linha(form, y, Tema.rotulo("Descrição", descricao, KeyEvent.VK_C), descricao, erroDescricao, null);

        // Valor e data lado a lado
        g.gridy = y++;
        g.insets = new Insets(8, 0, 0, 0);
        JPanel duplo = new JPanel(new GridBagLayout());
        duplo.setOpaque(false);
        GridBagConstraints d = new GridBagConstraints();
        d.anchor = GridBagConstraints.LINE_START;
        d.fill = GridBagConstraints.HORIZONTAL;
        d.gridx = 0;
        d.weightx = 0.5;
        d.insets = new Insets(0, 0, 0, 16);
        duplo.add(bloco(Tema.rotulo("Valor (R$)", valor, KeyEvent.VK_V), valor, erroValor, "Exemplo: 180,50"), d);
        d.gridx = 1;
        d.insets = new Insets(0, 0, 0, 0);
        duplo.add(bloco(Tema.rotulo("Data", data, KeyEvent.VK_A), data, erroData, "dd/mm/aaaa"), d);
        form.add(duplo, g);

        categoria.setFont(Tema.fonte(Font.PLAIN, 15));
        categoria.setBackground(Tema.BRANCO);
        y = linha(form, y, Tema.rotulo("Categoria", categoria, KeyEvent.VK_G), categoria, erroCategoria,
                "Só aparecem categorias do tipo escolhido.");

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.TRAILING, 10, 0));
        botoes.setOpaque(false);
        JButton limpar = Tema.botaoSecundario("Limpar", KeyEvent.VK_L);
        JButton cancelar = Tema.botaoSecundario("Cancelar", KeyEvent.VK_N);
        limpar.addActionListener(e -> limpar());
        cancelar.addActionListener(e -> area.irPara(AreaLogada.PAINEL));
        salvar.addActionListener(e -> salvar());
        botoes.add(limpar);
        botoes.add(cancelar);
        botoes.add(salvar);
        g.gridy = y;
        g.insets = new Insets(14, 0, 0, 0);
        form.add(botoes, g);

        JPanel limite = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        limite.setOpaque(false);
        form.setPreferredSize(new java.awt.Dimension(640, form.getPreferredSize().height));
        limite.add(form);
        add(limite, BorderLayout.CENTER);
    }

    private static JRadioButton radio(String texto, int atalho) {
        JRadioButton r = new JRadioButton(texto);
        r.setFont(Tema.fonte(Font.BOLD, 15));
        r.setForeground(Tema.GRAFITE);
        r.setOpaque(false);
        r.setMnemonic(atalho);
        return r;
    }

    private JPanel bloco(JLabel rotulo, JComponent campo, JLabel erro, String dica) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        int y = linha(p, 0, rotulo, campo, erro, dica);
        return p;
    }

    private int linha(JPanel p, int y, JLabel rotulo, JComponent campo, JLabel erro, String dica) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridwidth = 2;
        g.weightx = 1;
        g.anchor = GridBagConstraints.LINE_START;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridy = y++;
        g.insets = new Insets(y == 1 ? 0 : 8, 0, 6, 0);
        p.add(rotulo, g);
        g.gridy = y++;
        g.insets = new Insets(0, 0, 2, 0);
        p.add(campo, g);
        if (dica != null) {
            g.gridy = y++;
            p.add(Tema.dica(dica), g);
        }
        if (erro != null) {
            g.gridy = y++;
            p.add(erro, g);
        }
        return y;
    }

    private String tipoEscolhido() {
        return receita.isSelected() ? "Receita" : "Despesa";
    }

    private void carregarCategorias() {
        categoria.removeAllItems();
        try {
            for (Categoria c : new CategoriaDAO().listarPorTipo(tipoEscolhido())) {
                categoria.addItem(c);
            }
            erroCategoria.setText(" ");
        } catch (ErroBanco ex) {
            erroCategoria.setText(ex.getMessage());
        }
    }

    /** Chamado sempre que a tela aparece. */
    public void preparar() {
        limpar();
        descricao.requestFocusInWindow();
    }

    private void limpar() {
        despesa.setSelected(true);
        descricao.setText("");
        valor.setText("");
        data.setText(LocalDate.now().format(Tema.DATA));
        Tema.marcarErro(descricao, erroDescricao, null);
        Tema.marcarErro(valor, erroValor, null);
        Tema.marcarErro(data, erroData, null);
        carregarCategorias();
    }

    private void salvar() {
        boolean ok = true;
        JComponent primeiroErro = null;

        String desc = descricao.getText().trim();
        if (desc.isEmpty()) {
            Tema.marcarErro(descricao, erroDescricao, "Informe uma descrição.");
            ok = false;
            primeiroErro = descricao;
        } else {
            Tema.marcarErro(descricao, erroDescricao, null);
        }

        double v = 0;
        try {
            v = Tema.lerValor(valor.getText());
            if (v <= 0) {
                Tema.marcarErro(valor, erroValor, "Informe um valor maior que zero."); // RN03
                ok = false;
            } else {
                Tema.marcarErro(valor, erroValor, null);
            }
        } catch (NumberFormatException ex) {
            Tema.marcarErro(valor, erroValor, ex.getMessage());
            ok = false;
        }
        if (!ok && primeiroErro == null) {
            primeiroErro = valor;
        }

        LocalDate d = null;
        try {
            d = LocalDate.parse(data.getText().trim(), Tema.DATA);
            Tema.marcarErro(data, erroData, null);
        } catch (DateTimeParseException ex) {
            Tema.marcarErro(data, erroData, "Data inválida. Use dd/mm/aaaa.");
            ok = false;
            if (primeiroErro == null) {
                primeiroErro = data;
            }
        }

        Categoria cat = (Categoria) categoria.getSelectedItem();
        if (cat == null || !cat.getTipo().equalsIgnoreCase(tipoEscolhido())) { // RN04
            erroCategoria.setText("Escolha uma categoria do tipo " + tipoEscolhido() + ".");
            ok = false;
        } else {
            erroCategoria.setText(" ");
        }

        if (!ok) {
            if (primeiroErro != null) {
                primeiroErro.requestFocusInWindow();
            }
            return;
        }

        Usuario u = area.getUsuario();
        try {
            new TransacaoDAO().inserir(u, desc, v, d, tipoEscolhido(), cat);
        } catch (ErroBanco ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro no banco de dados", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, tipoEscolhido() + " de " + Tema.moeda(v) + " salva.",
                "Transação salva", JOptionPane.INFORMATION_MESSAGE);
        area.irPara(AreaLogada.PAINEL);
    }

    public JButton botaoPadrao() {
        return salvar;
    }
}
