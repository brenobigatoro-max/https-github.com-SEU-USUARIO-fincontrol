package br.com.fincontrol.dados;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Abre conexões com o MySQL usando os dados do arquivo db.properties,
 * que fica na pasta do projeto (ao lado do build.xml).
 */
public final class ConexaoBD {

    private static Properties config;

    private ConexaoBD() {
    }

    private static synchronized Properties config() {
        if (config == null) {
            Properties p = new Properties();
            Path arquivo = Paths.get("db.properties");
            try (InputStream in = Files.exists(arquivo)
                    ? Files.newInputStream(arquivo)
                    : ConexaoBD.class.getResourceAsStream("/db.properties")) {
                if (in == null) {
                    throw new ErroBanco("Arquivo db.properties não encontrado. Copie o db.properties.exemplo, "
                            + "renomeie para db.properties e coloque a senha do usuário do MySQL.", null);
                }
                p.load(in);
            } catch (IOException e) {
                throw new ErroBanco("Não foi possível ler o arquivo db.properties.", e);
            }
            config = p;
        }
        return config;
    }

    /** Abre uma nova conexão. Use sempre dentro de try-with-resources, para ela ser fechada. */
    public static Connection abrir() throws SQLException {
        Properties p = config();
        return DriverManager.getConnection(p.getProperty("url"), p.getProperty("usuario"), p.getProperty("senha"));
    }

    /** Testa a conexão. Devolve null se deu certo, ou a mensagem do problema. */
    public static String testar() {
        try (Connection c = abrir()) {
            return null;
        } catch (SQLException e) {
            return e.getMessage();
        } catch (ErroBanco e) {
            return e.getMessage();
        }
    }
}
