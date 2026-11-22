package br.com.fincontrol.dados;

/**
 * Erro ao acessar o banco de dados, com uma mensagem que pode ser mostrada ao usuário.
 */
public class ErroBanco extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ErroBanco(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
