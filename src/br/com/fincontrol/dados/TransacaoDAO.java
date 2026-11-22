package br.com.fincontrol.dados;

import br.com.fincontrol.model.Categoria;
import br.com.fincontrol.model.Transacao;
import br.com.fincontrol.model.Usuario;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

/** Acesso à tabela transacao. */
public class TransacaoDAO {

    /** Carrega do banco todas as transações do usuário para dentro da lista dele (RF006). */
    public void carregar(Usuario u) {
        String sql = "SELECT t.id_transacao, t.descricao, t.valor, t.data_transacao, t.tipo, "
                + "c.id_categoria, c.nome AS nome_categoria, c.tipo AS tipo_categoria "
                + "FROM transacao t JOIN categoria c ON c.id_categoria = t.id_categoria "
                + "WHERE t.id_usuario = ? ORDER BY t.data_transacao, t.id_transacao";
        try (Connection c = ConexaoBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, u.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Categoria cat = new Categoria(rs.getInt("id_categoria"),
                            rs.getString("nome_categoria"), rs.getString("tipo_categoria"));
                    u.adicionarTransacao(new Transacao(rs.getInt("id_transacao"), rs.getString("descricao"),
                            rs.getBigDecimal("valor").doubleValue(), rs.getDate("data_transacao").toLocalDate(),
                            rs.getString("tipo"), u, cat));
                }
            }
        } catch (SQLException e) {
            throw new ErroBanco("Não foi possível carregar as transações.", e);
        }
    }

    /** Grava uma receita ou despesa (RF003, RF004) e a coloca na lista do usuário. */
    public Transacao inserir(Usuario u, String descricao, double valor, LocalDate data, String tipo, Categoria cat) {
        String sql = "INSERT INTO transacao (descricao, valor, data_transacao, tipo, id_usuario, id_categoria) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = ConexaoBD.abrir();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, descricao.trim());
            ps.setBigDecimal(2, BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP));
            ps.setDate(3, Date.valueOf(data));
            ps.setString(4, tipo);
            ps.setInt(5, u.getId());
            ps.setInt(6, cat.getId());
            ps.executeUpdate();
            try (ResultSet chave = ps.getGeneratedKeys()) {
                chave.next();
                Transacao t = new Transacao(chave.getInt(1), descricao.trim(), valor, data, tipo, u, cat);
                u.adicionarTransacao(t);
                return t;
            }
        } catch (SQLException e) {
            throw new ErroBanco("Não foi possível salvar a transação.", e);
        }
    }
}
