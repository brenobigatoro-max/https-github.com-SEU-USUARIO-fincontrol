package br.com.fincontrol.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Usuário do FinControl. Guarda as próprias transações e calcula o saldo.
 */
public class Usuario {

    private int id;
    private String nome;
    private String email;
    private String senha;
    private List<Transacao> transacoes;

    public Usuario(int id, String nome, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.transacoes = new ArrayList<>(); // a lista começa vazia
    }

    /**
     * Adiciona uma transação à lista do usuário (RF003 e RF004).
     */
    public void adicionarTransacao(Transacao transacao) {
        if (transacao.getUsuario() != this) {
            throw new IllegalArgumentException("A transação pertence a outro usuário.");
        }
        transacoes.add(transacao);
    }

    /**
     * Calcula o saldo (RF005): soma as receitas e subtrai as despesas.
     */
    public double calcularSaldo() {
        double saldo = 0;
        for (Transacao t : transacoes) {
            if (t.getTipo().equalsIgnoreCase("Receita")) {
                saldo += t.getValor();
            } else if (t.getTipo().equalsIgnoreCase("Despesa")) {
                saldo -= t.getValor();
            }
        }
        return saldo;
    }

    public int getId() {
        return id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    /** Usado na tela do administrador para mostrar o perfil. */
    public String getPerfil() {
        return "Usuário";
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    /** Devolve a lista só para leitura: novas transações entram por adicionarTransacao(). */
    public List<Transacao> getTransacoes() {
        return Collections.unmodifiableList(transacoes);
    }
}
