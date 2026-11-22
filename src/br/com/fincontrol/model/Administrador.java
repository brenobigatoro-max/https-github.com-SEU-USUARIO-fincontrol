package br.com.fincontrol.model;

/**
 * Administrador é um tipo de Usuario (herança): tem tudo o que o usuário tem
 * e, além disso, pode gerenciar os usuários do sistema.
 */
public class Administrador extends Usuario {

    public Administrador(int id, String nome, String email, String senha) {
        super(id, nome, email, senha); // chama o construtor de Usuario
    }

    /**
     * Gerenciamento de usuários (cadastrar, editar, excluir).
     * É feito pela tela "Gerenciar usuários" (ui/TelaUsuarios), que grava no MySQL
     * por meio da classe dados/UsuarioDAO.
     */
    public void gerenciarUsuarios() {
        System.out.println("Gerenciamento de usuários: use a tela Gerenciar usuários.");
    }

    @Override
    public String getPerfil() {
        return "Administrador";
    }
}
