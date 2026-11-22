package br.com.fincontrol.dados;

import br.com.fincontrol.model.Administrador;
import br.com.fincontrol.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Acesso à tabela usuario. */
public class UsuarioDAO {

    private static final String CAMPOS = "id_usuario, nome, email, senha, perfil";

    private Usuario montar(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_usuario");
        String nome = rs.getString("nome");
        String email = rs.getString("email");
        String senha = rs.getString("senha"); // hash
        return "Administrador".equals(rs.getString("perfil"))
                ? new Administrador(id, nome, email, senha)
                : new Usuario(id, nome, email, senha);
    }

    /** Login (RF002): devolve o usuário com as transações carregadas, ou null se não conferir. */
    public Usuario autenticar(String email, String senha) {
        String sql = "SELECT " + CAMPOS + " FROM usuario WHERE email = ? AND senha = ?";
        Usuario u = null;
        try (Connection c = ConexaoBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            ps.setString(2, Seguranca.hash(senha));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    u = montar(rs);
                }
            }
        } catch (SQLException e) {
            throw new ErroBanco("Não foi possível conectar ao banco de dados para fazer o login.", e);
        }
        if (u != null) {
            new TransacaoDAO().carregar(u);
        }
        return u;
    }

    /** RN06: o e-mail não pode se repetir. idIgnorar = id do próprio usuário na edição (0 no cadastro). */
    public boolean emailEmUso(String email, int idIgnorar) {
        String sql = "SELECT 1 FROM usuario WHERE email = ? AND id_usuario <> ?";
        try (Connection c = ConexaoBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            ps.setInt(2, idIgnorar);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new ErroBanco("Não foi possível verificar o e-mail.", e);
        }
    }

    /** Cadastro de usuário (RF001 e RF008). A senha é gravada como hash (RNF001). */
    public Usuario inserir(String nome, String email, String senha, boolean administrador) {
        String sql = "INSERT INTO usuario (nome, email, senha, perfil) VALUES (?, ?, ?, ?)";
        String hash = Seguranca.hash(senha);
        try (Connection c = ConexaoBD.abrir();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nome.trim());
            ps.setString(2, email.trim());
            ps.setString(3, hash);
            ps.setString(4, administrador ? "Administrador" : "Usuario");
            ps.executeUpdate();
            try (ResultSet chave = ps.getGeneratedKeys()) {
                chave.next();
                int id = chave.getInt(1);
                return administrador
                        ? new Administrador(id, nome.trim(), email.trim(), hash)
                        : new Usuario(id, nome.trim(), email.trim(), hash);
            }
        } catch (SQLException e) {
            throw new ErroBanco("Não foi possível cadastrar o usuário.", e);
        }
    }

    /** Edição pelo administrador (RF008). novaSenha vazia mantém a senha atual. */
    public void atualizar(Usuario u, String novaSenha) {
        boolean trocaSenha = novaSenha != null && !novaSenha.isEmpty();
        String sql = trocaSenha
                ? "UPDATE usuario SET nome = ?, email = ?, senha = ? WHERE id_usuario = ?"
                : "UPDATE usuario SET nome = ?, email = ? WHERE id_usuario = ?";
        try (Connection c = ConexaoBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            if (trocaSenha) {
                String hash = Seguranca.hash(novaSenha);
                ps.setString(3, hash);
                ps.setInt(4, u.getId());
                u.setSenha(hash);
            } else {
                ps.setInt(3, u.getId());
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ErroBanco("Não foi possível salvar as alterações do usuário.", e);
        }
    }

    /** Exclusão pelo administrador. O banco apaga as transações junto (ON DELETE CASCADE, RN09). */
    public void excluir(int idUsuario) {
        try (Connection c = ConexaoBD.abrir();
             PreparedStatement ps = c.prepareStatement("DELETE FROM usuario WHERE id_usuario = ?")) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ErroBanco("Não foi possível excluir o usuário.", e);
        }
    }

    /** Todos os usuários, para a tela do administrador. */
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        try (Connection c = ConexaoBD.abrir();
             PreparedStatement ps = c.prepareStatement("SELECT " + CAMPOS + " FROM usuario ORDER BY id_usuario");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
        } catch (SQLException e) {
            throw new ErroBanco("Não foi possível carregar os usuários.", e);
        }
        return lista;
    }

    /** Quantidade de transações de cada usuário (id do usuário → quantidade). */
    public Map<Integer, Integer> contarTransacoes() {
        Map<Integer, Integer> mapa = new HashMap<>();
        String sql = "SELECT id_usuario, COUNT(*) AS qtd FROM transacao GROUP BY id_usuario";
        try (Connection c = ConexaoBD.abrir(); PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                mapa.put(rs.getInt("id_usuario"), rs.getInt("qtd"));
            }
        } catch (SQLException e) {
            throw new ErroBanco("Não foi possível contar as transações.", e);
        }
        return mapa;
    }
}
