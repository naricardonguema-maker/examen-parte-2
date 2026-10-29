-- Datos de ejemplo de H2.
-- MERGE permite ejecutar este archivo varias veces sin crear filas duplicadas por ID.

MERGE INTO genero (id_genero, nombre) KEY (id_genero) VALUES (1, 'Ciencia ficción');
MERGE INTO genero (id_genero, nombre) KEY (id_genero) VALUES (2, 'Comedia');
MERGE INTO genero (id_genero, nombre) KEY (id_genero) VALUES (3, 'Aventura');
MERGE INTO genero (id_genero, nombre) KEY (id_genero) VALUES (4, 'Animación');

MERGE INTO pelicula (id_pelicula, titulo, anio, nota, id_genero) KEY (id_pelicula)
VALUES (1, 'Regreso al futuro', 1985, 8.5, 1);
MERGE INTO pelicula (id_pelicula, titulo, anio, nota, id_genero) KEY (id_pelicula)
VALUES (2, 'Aterriza como puedas', 1980, 7.8, 2);
MERGE INTO pelicula (id_pelicula, titulo, anio, nota, id_genero) KEY (id_pelicula)
VALUES (3, 'Indiana Jones y la última cruzada', 1989, 8.2, 3);
MERGE INTO pelicula (id_pelicula, titulo, anio, nota, id_genero) KEY (id_pelicula)
VALUES (4, 'Shrek', 2001, 7.9, 4);
MERGE INTO pelicula (id_pelicula, titulo, anio, nota, id_genero) KEY (id_pelicula)
VALUES (5, 'Matrix', 1999, 8.7, 1);
MERGE INTO pelicula (id_pelicula, titulo, anio, nota, id_genero) KEY (id_pelicula)
VALUES (6, 'El gran Lebowski', 1998, 8.0, 2);
