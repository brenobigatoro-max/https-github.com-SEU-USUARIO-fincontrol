-- =====================================================================
-- FinControl — Banco de dados (Etapa 4)
-- Execute este script inteiro no MySQL Workbench, conectado como root.
-- Ele recria o banco do zero, com as tabelas e os dados iniciais de teste.
-- =====================================================================

SET NAMES utf8mb4;  -- garante que os acentos (Alimentação, Salário) sejam gravados certo

DROP DATABASE IF EXISTS FinControlDB;
CREATE DATABASE FinControlDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE FinControlDB;

-- ---------------------------------------------------------------------
-- Usuário do MySQL usado pelo programa Java (arquivo db.properties).
-- Tem permissão só neste banco, e só para ler e gravar dados.
-- ---------------------------------------------------------------------
CREATE USER IF NOT EXISTS 'fincontrol'@'localhost' IDENTIFIED BY 'FinControl@2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON FinControlDB.* TO 'fincontrol'@'localhost';
FLUSH PRIVILEGES;

-- ---------------------------------------------------------------------
-- Tabelas
-- ---------------------------------------------------------------------
CREATE TABLE usuario (
    id_usuario  INT AUTO_INCREMENT PRIMARY KEY,
    nome        VARCHAR(100) NOT NULL,
    email       VARCHAR(100) NOT NULL,
    senha       CHAR(64)     NOT NULL,                    -- hash SHA-256 (RNF001)
    perfil      ENUM('Usuario', 'Administrador') NOT NULL DEFAULT 'Usuario',
    CONSTRAINT uq_usuario_email UNIQUE (email)            -- RN06
);

CREATE TABLE categoria (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nome         VARCHAR(50) NOT NULL,
    tipo         ENUM('Receita', 'Despesa') NOT NULL
);

CREATE TABLE transacao (
    id_transacao   INT AUTO_INCREMENT PRIMARY KEY,
    descricao      VARCHAR(150)  NOT NULL,
    valor          DECIMAL(10,2) NOT NULL,
    data_transacao DATE          NOT NULL,
    tipo           ENUM('Receita', 'Despesa') NOT NULL,
    id_usuario     INT NOT NULL,
    id_categoria   INT NOT NULL,
    CONSTRAINT ck_transacao_valor CHECK (valor > 0),      -- RN03
    CONSTRAINT fk_transacao_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
        ON DELETE CASCADE,                                -- RN09
    CONSTRAINT fk_transacao_categoria
        FOREIGN KEY (id_categoria) REFERENCES categoria (id_categoria)
);

CREATE INDEX ix_transacao_usuario_data ON transacao (id_usuario, data_transacao);

-- ---------------------------------------------------------------------
-- Dados iniciais para teste
-- Senhas: breno 123456 · ana ana123 · carlos carlos123 · mariana mariana123 · joao joao123
-- ---------------------------------------------------------------------
INSERT INTO usuario (nome, email, senha, perfil) VALUES
('Breno Bigatoro', 'breno@gmail.com',   SHA2('123456', 256),     'Administrador'),
('Ana Souza',      'ana@gmail.com',     SHA2('ana123', 256),     'Usuario'),
('Carlos Lima',    'carlos@gmail.com',  SHA2('carlos123', 256),  'Usuario'),
('Mariana Rocha',  'mariana@gmail.com', SHA2('mariana123', 256), 'Usuario'),
('Joao Pedro',     'joao@gmail.com',    SHA2('joao123', 256),    'Usuario');

INSERT INTO categoria (nome, tipo) VALUES          -- RN08
('Salário',     'Receita'),
('Freelance',   'Receita'),
('Alimentação', 'Despesa'),
('Transporte',  'Despesa'),
('Lazer',       'Despesa');

INSERT INTO transacao (descricao, valor, data_transacao, tipo, id_usuario, id_categoria) VALUES
('Recebimento de salário', 2500.00, '2026-04-01', 'Receita', 1, 1),
('Freelance de design',     600.00, '2026-04-03', 'Receita', 1, 2),
('Compra no mercado',       180.50, '2026-04-04', 'Despesa', 1, 3),
('Passagem de ônibus',      120.00, '2026-04-05', 'Despesa', 1, 4),
('Freelance de design',     600.00, '2026-04-03', 'Receita', 2, 2),
('Compra no mercado',       180.50, '2026-04-04', 'Despesa', 3, 3);

-- Conferência rápida: saldo de cada usuário
SELECT u.nome,
       COALESCE(SUM(CASE WHEN t.tipo = 'Receita' THEN t.valor ELSE -t.valor END), 0) AS saldo
FROM usuario u
LEFT JOIN transacao t ON t.id_usuario = u.id_usuario
GROUP BY u.id_usuario, u.nome
ORDER BY u.id_usuario;
