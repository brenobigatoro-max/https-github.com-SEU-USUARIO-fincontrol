package br.com.fincontrol.ui;

import br.com.fincontrol.dados.ErroBanco;
import br.com.fincontrol.dados.UsuarioDAO;
import br.com.fincontrol.model.Usuario;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/** Tela 5 — Gerenciar usuários (RF008), exclusiva do administrador (RN07). */
public class TelaUsuarios extends JPanel {

    private final AreaLogada area;
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nome", "E-mail", "Perfil", "Transações"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);
    private final transient UsuarioDAO dao = new UsuarioDAO();
    private transient List<Usuario> exibidos = List.of();
    private transient Map<Integer, Integer> contagem = Map.of();

    public TelaUsuarios(AreaLogada area) {
        super(new BorderLayout(0, 18));
        this.area = area;
        setBackground(Tema.GELO);
        setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(Tema.titulo("Gerenciar usuários"));
        textos.add(Tema.subtitulo("Área exclusiva do administrador. Selecione um usuário na tabela para editar ou excluir."));

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.TRAILING, 10, 0));
        botoes.setOpaque(false);
        JButton novo = Tema.botaoPrincipal("+ Novo", KeyEvent.VK_O);
        JButton editar = Tema.botaoSecundario("Editar", KeyEvent.VK_E);
        JButton excluir = Tema.botaoPerigo("Excluir", KeyEvent.VK_X);
        novo.addActionListener(e -> novo());
        editar.addActionListener(e -> editar());
        excluir.addActionListener(e -> excluir());
        botoes.add(novo);
        botoes.add(editar);
        botoes.add(excluir);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.add(textos, BorderLayout.WEST);
        topo.add(botoes, BorderLayout.EAST);
        add(topo, BorderLayout.NORTH);

        tabela.setFont(Tema.fonte(Font.PLAIN, 15));
        tabela.setRowHeight(38);
        tabela.setShowVerticalLines(false);
        tabela.setGridColor(new Color(0xE1E8E4));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setSelectionBackground(new Color(0xD9EDE3));
        tabela.setSelectionForeground(Tema.GRAFITE);
        tabela.setFillsViewportHeight(true);
        tabela.getAccessibleContext().setAccessibleName("Usuários do sistema");
        tabela.setDefaultRenderer(Object.class, Tema.celula());
        tabela.getTableHeader().setFont(Tema.fonte(Font.BOLD, 13));
        tabela.getTableHeader().setBackground(Tema.CABECALHO);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(240);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(260);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(140);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(110);
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editar(); // duplo clique também edita
                }
            }
        });

        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setBorder(BorderFactory.createLineBorder(Tema.LINHA));
        rolagem.getViewport().setBackground(Tema.BRANCO);
        add(rolagem, BorderLayout.CENTER);
    }

    public void atualizar() {
        try {
            exibidos = dao.listarTodos();
            contagem = dao.contarTransacoes();
        } catch (ErroBanco ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro no banco de dados", JOptionPane.ERROR_MESSAGE);
            return;
        }
        modelo.setRowCount(0);
        for (Usuario u : exibidos) {
            modelo.addRow(new Object[]{u.getId(), u.getNome(), u.getEmail(), u.getPerfil(),
                contagem.getOrDefault(u.getId(), 0)});
        }
    }

    private Usuario selecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário na tabela primeiro.",
                    "Nenhum usuário selecionado", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return exibidos.get(tabela.convertRowIndexToModel(linha));
    }

    private void novo() {
        DialogUsuario d = new DialogUsuario(this, null);
        d.setVisible(true);
        if (d.foiSalvo()) {
            atualizar();
        }
    }

    private void editar() {
        Usuario u = selecionado();
        if (u == null) {
            return;
        }
        DialogUsuario d = new DialogUsuario(this, u);
        d.setVisible(true);
        if (d.foiSalvo()) {
            Usuario logado = area.getUsuario();
            if (u.getId() == logado.getId()) { // editou a própria conta: atualiza o nome no menu
                logado.setNome(u.getNome());
                logado.setEmail(u.getEmail());
                area.atualizarRodape();
            }
            atualizar();
        }
    }

    private void excluir() {
        Usuario u = selecionado();
        if (u == null) {
            return;
        }
        if (u.getId() == area.getUsuario().getId()) {
            JOptionPane.showMessageDialog(this, "Você não pode excluir o próprio usuário enquanto está conectado.",
                    "Exclusão não permitida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int qtd = contagem.getOrDefault(u.getId(), 0);
        String aviso = "Excluir o usuário " + u.getNome() + "?\n"
                + (qtd > 0 ? "As " + qtd + " transação(ões) dele também serão removidas.\n" : "")
                + "Esta ação não pode ser desfeita.";
        Object[] opcoes = {"Excluir", "Cancelar"};
        int resposta = JOptionPane.showOptionDialog(this, aviso, "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, opcoes, opcoes[1]);
        if (resposta == 0) {
            try {
                dao.excluir(u.getId()); // o banco apaga as transações junto (RN09)
            } catch (ErroBanco ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro no banco de dados", JOptionPane.ERROR_MESSAGE);
            }
            atualizar();
        }
    }
}
