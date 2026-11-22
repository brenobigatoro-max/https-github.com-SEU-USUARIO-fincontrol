package br.com.fincontrol.model;

import java.time.LocalDate;

/**
 * Uma movimentação financeira: uma receita (entrada) ou uma despesa (saída).
 * Cada transação pertence a um usuário e a uma categoria.
 */
public class Transacao {

    private int id;
    private String descricao;
    private double valor;
    private LocalDate data;
    private String tipo; // "Receita" ou "Despesa"
    private Usuario usuario;
    private Categoria categoria;

    public Transacao(int id, String descricao, double valor, LocalDate data,
                     String tipo, Usuario usuario, Categoria categoria) {
        this.id = id;
        this.descricao = descricao;
        if (!Double.isFinite(valor) || valor <= 0) { // RN03: segunda barreira, além da tela
            throw new IllegalArgumentException("O valor da transação deve ser um número maior que zero.");
        }
        this.valor = valor;
        this.data = data;
        this.tipo = tipo;
        this.usuario = usuario;
        this.categoria = categoria;
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getValor() {
        return valor;
    }

    public LocalDate getData() {
        return data;
    }

    public String getTipo() {
        return tipo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Categoria getCategoria() {
        return categoria;
    }
}
