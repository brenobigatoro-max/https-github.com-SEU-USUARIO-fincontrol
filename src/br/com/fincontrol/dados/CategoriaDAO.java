package br.com.fincontrol.dados;

import br.com.fincontrol.model.Categoria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Acesso à tabela categoria. */
public class CategoriaDAO {

    /** Categorias do tipo escolhido (RN04), em ordem alfabética. */
    public List<Categoria> listarPorTipo(String tipo) {
        String sql = "SELECT id_categoria, nome, tipo FROM categoria WHERE tipo = ? ORDER BY nome";
        List<Categoria> lista = new ArrayList<>();
        try (Connection c = ConexaoBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Categoria(rs.getInt("id_categoria"), rs.getString("nome"), rs.getString("tipo")));
                }
            }
        } catch (SQLException e) {
            throw new ErroBanco("Não foi possível carregar as categorias.", e);
        }
        return lista;
    }
}
