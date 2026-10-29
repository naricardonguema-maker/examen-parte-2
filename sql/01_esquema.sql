-- Esquema de la base de datos del gestor de películas.
-- Se puede ejecutar en la consola SQL de H2; las tablas no se duplican si ya existen.

CREATE TABLE IF NOT EXISTS genero (
    id_genero INT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS pelicula (
    id_pelicula INT PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    anio INT NOT NULL,
    nota DECIMAL(3,1) NOT NULL,
    id_genero INT NOT NULL,
    CONSTRAINT fk_pelicula_genero FOREIGN KEY (id_genero)
        REFERENCES genero(id_genero)
);
