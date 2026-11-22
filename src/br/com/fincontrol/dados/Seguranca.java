package br.com.fincontrol.dados;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Criptografia da senha (RNF001): o banco guarda só o hash SHA-256, nunca a senha.
 * É o mesmo resultado da função SHA2(senha, 256) do MySQL, usada no script.
 */
public final class Seguranca {

    private Seguranca() {
    }

    public static String hash(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(senha.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : bytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível", e);
        }
    }
}
