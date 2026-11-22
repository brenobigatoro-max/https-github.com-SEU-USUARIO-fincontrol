package br.com.fincontrol.ui;

import br.com.fincontrol.dados.ErroBanco;
import br.com.fincontrol.dados.UsuarioDAO;
import br.com.fincontrol.model.Usuario;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/** Janela para o administrador cadastrar ou editar um usuário (RF008). */
public class DialogUsuario extends JDialog {

    private final Usuario editando;
    private final FormUsuario form;
    private final JComboBox<String> perfil = new JComboBox<>(new String[]{"Usuário", "Administrador"});
    private boolean salvo = false;

    /** @param editando o usuário a editar, ou null para cadastrar um novo. */
    public DialogUsuario(Component pai, Usuario editando) {
        super(SwingUtilities.getWindowAncestor(pai), editando == null ? "Novo usuário" : "Editar usuário",
                ModalityType.APPLICATION_MODAL);
        this.editando = editando;
        this.form = new FormUsuario(editando != null);

        JPanel corpo = new JPanel(new BorderLayout(0, 12));
        corpo.setBackground(Tema.BRANCO);
        corpo.setBorder(BorderFactory.createEmptyBorder(24, 28, 20, 28));
        corpo.add(Tema.titulo(editando == null ? "Novo usuário" : "Editar usuário"), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        centro.add(form, BorderLayout.CENTER);
        JPanel linhaPerfil = new JPanel(new BorderLayout(0, 6));
        linhaPerfil.setOpaque(false);
        perfil.setFont(Tema.fonte(Font.PLAIN, 15));
        linhaPerfil.add(Tema.rotulo("Perfil", perfil, KeyEvent.VK_P), BorderLayout.NORTH);
        linhaPerfil.add(perfil, BorderLayout.CENTER);
        if (editando != null) {
            form.preencher(editando);
            perfil.setSelectedItem(editando.getPerfil());
            perfil.setEnabled(false); // o perfil é definido no cadastro
            linhaPerfil.add(Tema.dica("O perfil é definido no cadastro e não muda na edição."), BorderLayout.SOUTH);
        }
        centro.add(linhaPerfil, BorderLayout.SOUTH);
        corpo.add(centro, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.TRAILING, 10, 0));
        botoes.setOpaque(false);
        JButton cancelar = Tema.botaoSecundario("Cancelar", KeyEvent.VK_C);
        JButton salvar = Tema.botaoPrincipal("Salvar", KeyEvent.VK_S);
        cancelar.addActionListener(e -> dispose());
        salvar.addActionListener(e -> salvar());
        botoes.add(cancelar);
        botoes.add(salvar);
        corpo.add(botoes, BorderLayout.SOUTH);

        setContentPane(corpo);
        getRootPane().setDefaultButton(salvar);
        pack();
        setSize(Math.max(getWidth(), 460), getHeight());
        setLocationRelativeTo(pai);
    }

    private void salvar() {
        if (!form.validar(editando)) {
            return;
        }
        UsuarioDAO dao = new UsuarioDAO();
        try {
            if (editando == null) {
                dao.inserir(form.getNome(), form.getEmail(), form.getSenha(),
                        "Administrador".equals(perfil.getSelectedItem()));
            } else {
                editando.setNome(form.getNome());
                editando.setEmail(form.getEmail());
                dao.atualizar(editando, form.getSenha()); // senha vazia mantém a atual
            }
        } catch (ErroBanco ex) {
            javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro no banco de dados",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        salvo = true;
        dispose();
    }

    public boolean foiSalvo() {
        return salvo;
    }
}
