-- CONSULTA 1: simple. Lee películas desde una única tabla.
SELECT id_pelicula, titulo, anio, nota
FROM pelicula
ORDER BY titulo;

-- CONSULTA 2: JOIN. Combina cada película con el nombre de su género.
SELECT p.titulo, p.anio, g.nombre AS genero, p.nota
FROM pelicula p
INNER JOIN genero g ON p.id_genero = g.id_genero
ORDER BY g.nombre, p.titulo;

-- CONSULTA 3: agregación. Cuenta las películas y calcula la nota media por género.
SELECT g.nombre,
       COUNT(p.id_pelicula) AS cantidad_peliculas,
       ROUND(AVG(p.nota), 2) AS nota_media
FROM genero g
LEFT JOIN pelicula p ON g.id_genero = p.id_genero
GROUP BY g.id_genero, g.nombre
ORDER BY g.nombre;
