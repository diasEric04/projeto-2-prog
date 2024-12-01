-- Criação da tabela 'marcas'
CREATE TABLE marcas (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    pais_origem VARCHAR(100) NOT NULL
);

-- Criação da tabela 'veiculos'
CREATE TABLE veiculos (
    id SERIAL PRIMARY KEY,
    modelo VARCHAR(100) NOT NULL,
    ano_fabricacao INT NOT NULL,
    quilometragem INT NOT NULL,
    valor DECIMAL(10, 2) NOT NULL,
    marca_id INT NOT NULL,
    FOREIGN KEY (marca_id) REFERENCES marcas (id) ON DELETE CASCADE
);