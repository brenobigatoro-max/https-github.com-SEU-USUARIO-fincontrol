package br.com.fincontrol.ui;

import br.com.fincontrol.dados.ErroBanco;
import br.com.fincontrol.dados.UsuarioDAO;
import br.com.fincontrol.model.Usuario;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Campos de nome, e-mail e senha, com as validações do cadastro.
 * É usado na tela "Criar conta" e na janela de novo/editar usuário do administrador.
 */
public class FormUsuario extends JPanel {

    private final JTextField nome = Tema.campo(26);
    private final JTextField email = Tema.campo(26);
    private final JPasswordField senha = Tema.senha(26);
    private final JPasswordField confirmar = Tema.senha(26);
    private final JLabel erroNome = Tema.erro();
    private final JLabel erroEmail = Tema.erro();
    private final JLabel erroSenha = Tema.erro();
    private final JLabel erroConfirmar = Tema.erro();
    private final boolean senhaOpcional;
    private int linha = 0;

    /** @param senhaOpcional true na edição: senha em branco mantém a senha atual. */
    public FormUsuario(boolean senhaOpcional) {
        super(new GridBagLayout());
        this.senhaOpcional = senhaOpcional;
        setOpaque(false);
        adicionar("Nome completo", nome, erroNome, null, KeyEvent.VK_N);
        adicionar("E-mail", email, erroEmail, null, KeyEvent.VK_M);
        adicionar("Senha", senha, erroSenha,
                senhaOpcional ? "Deixe em branco para manter a senha atual." : "Mínimo de 6 caracteres.", KeyEvent.VK_H);
        adicionar("Confirmar senha", confirmar, erroConfirmar, null, KeyEvent.VK_F);
    }

    private void adicionar(String texto, JComponent campo, JLabel erro, String dica, int atalho) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        g.anchor = GridBagConstraints.LINE_START;

        g.gridy = linha++;
        g.insets = new Insets(linha == 1 ? 0 : 8, 0, 6, 0);
        add(Tema.rotulo(texto, campo, atalho), g);
        g.gridy = linha++;
        g.insets = new Insets(0, 0, 2, 0);
        add(campo, g);
        if (dica != null) {
            g.gridy = linha++;
            add(Tema.dica(dica), g);
        }
        g.gridy = linha++;
        add(erro, g);
    }

    /** Valida os campos e mostra os erros ao lado de cada um. "atual" é o usuário em edição (ou null). */
    public boolean validar(Usuario atual) {
        boolean ok = true;
        String n = nome.getText().trim();
        String e = email.getText().trim();
        String s = new String(senha.getPassword());
        String c = new String(confirmar.getPassword());

        if (n.isEmpty()) {
            Tema.marcarErro(nome, erroNome, "Informe o nome.");
            ok = false;
        } else {
            Tema.marcarErro(nome, erroNome, null);
        }

        if (e.isEmpty()) {
            Tema.marcarErro(email, erroEmail, "Informe o e-mail.");
            ok = false;
        } else if (!e.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            Tema.marcarErro(email, erroEmail, "Digite um e-mail válido, como nome@gmail.com.");
            ok = false;
        } else if (emailEmUso(e, atual)) {
            Tema.marcarErro(email, erroEmail, "Este e-mail já está cadastrado.");
            ok = false;
        } else if (erroVerificacao != null) {
            Tema.marcarErro(email, erroEmail, erroVerificacao);
            ok = false;
        } else {
            Tema.marcarErro(email, erroEmail, null);
        }

        boolean manterSenha = senhaOpcional && s.isEmpty() && c.isEmpty();
        if (!manterSenha && s.length() < 6) {
            Tema.marcarErro(senha, erroSenha, "A senha precisa ter pelo menos 6 caracteres.");
            ok = false;
        } else {
            Tema.marcarErro(senha, erroSenha, null);
        }

        if (!manterSenha && !s.equals(c)) {
            Tema.marcarErro(confirmar, erroConfirmar, "As senhas não são iguais.");
            ok = false;
        } else {
            Tema.marcarErro(confirmar, erroConfirmar, null);
        }

        if (!ok) {
            primeiroComErro().requestFocusInWindow();
        }
        return ok;
    }

    private String erroVerificacao;

    /** RN06, consultando o banco. Se o banco falhar, guarda a mensagem para mostrar no campo. */
    private boolean emailEmUso(String e, Usuario atual) {
        erroVerificacao = null;
        try {
            return new UsuarioDAO().emailEmUso(e, atual == null ? 0 : atual.getId());
        } catch (ErroBanco ex) {
            erroVerificacao = ex.getMessage();
            return false;
        }
    }

    private JComponent primeiroComErro() {
        for (JComponent c : new JComponent[]{nome, email, senha, confirmar}) {
            if (Boolean.TRUE.equals(c.getClientProperty("erro"))) {
                return c;
            }
        }
        return nome;
    }

    public void preencher(Usuario u) {
        nome.setText(u.getNome());
        email.setText(u.getEmail());
    }

    public void limpar() {
        for (JTextField f : new JTextField[]{nome, email, senha, confirmar}) {
            f.setText("");
        }
        Tema.marcarErro(nome, erroNome, null);
        Tema.marcarErro(email, erroEmail, null);
        Tema.marcarErro(senha, erroSenha, null);
        Tema.marcarErro(confirmar, erroConfirmar, null);
    }

    public void focar() {
        nome.requestFocusInWindow();
    }

    public String getNome() {
        return nome.getText().trim();
    }

    public String getEmail() {
        return email.getText().trim();
    }

    /** Senha digitada, ou vazia se o usuário quis manter a atual (edição). */
    public String getSenha() {
        return new String(senha.getPassword());
    }
}
