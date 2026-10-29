# PARTE 2 EXAMEN. Aplicación Java + H2: gestor de películas

## 2.1. Qué hace el programa

La aplicación se ejecuta en una consola y presenta un menú para:

1. Consultar todas las películas.
2. Añadir una película.
3. Buscar películas por título.
4. Mostrar las películas junto a su género mediante `JOIN`.
5. Mostrar estadísticas por género mediante funciones de agregación (`COUNT` y `AVG`).
6. Consultar los géneros disponibles.
7. Eliminar una película por su identificador.
0. Salir.

La primera vez que se ejecuta, el programa crea las tablas e introduce los datos de ejemplo. En siguientes ejecuciones conserva los cambios en el archivo local de la base de datos.

## 2.2. Modelo relacional

El proyecto tiene **dos tablas**, dentro del rango solicitado de 2 a 5 tablas.

### Tabla `genero`

| Campo | Tipo | Restricciones |
|---|---|---|
| `id_genero` | `INT` | **PK**, clave primaria. |
| `nombre` | `VARCHAR(50)` | `NOT NULL`, `UNIQUE`. |

### Tabla `pelicula`

| Campo | Tipo | Restricciones |
|---|---|---|
| `id_pelicula` | `INT` | **PK**, clave primaria. |
| `titulo` | `VARCHAR(100)` | `NOT NULL`. |
| `anio` | `INT` | `NOT NULL`. |
| `nota` | `DECIMAL(3,1)` | `NOT NULL`. |
| `id_genero` | `INT` | **FK**, referencia a `genero(id_genero)`. |

**Relación:** un género puede tener muchas películas; cada película pertenece a un género. La clave ajena `pelicula.id_genero` evita asignar películas a géneros inexistentes.

El script de datos contiene **4 géneros** y **6 películas**, por lo que cada tabla tiene al menos los 3 registros mínimos.

## 2.3. Consultas SQL requeridas

Los ejemplos también están disponibles en `sql/03_consultas.sql`.

### Consulta simple

```sql
SELECT id_pelicula, titulo, anio, nota
FROM pelicula
ORDER BY titulo;
```

Consulta información de la tabla `pelicula`, ordenada por título.

### Consulta con JOIN

```sql
SELECT p.titulo, p.anio, g.nombre AS genero, p.nota
FROM pelicula p
INNER JOIN genero g ON p.id_genero = g.id_genero
ORDER BY g.nombre, p.titulo;
```

Une las tablas `pelicula` y `genero` para mostrar el nombre del género en vez de solo su identificador.

### Consulta con agregación

```sql
SELECT g.nombre,
       COUNT(p.id_pelicula) AS cantidad_peliculas,
       ROUND(AVG(p.nota), 2) AS nota_media
FROM genero g
LEFT JOIN pelicula p ON g.id_genero = p.id_genero
GROUP BY g.id_genero, g.nombre
ORDER BY g.nombre;
```

Agrupa las películas por género, cuenta cuántas hay y calcula su nota media. El `LEFT JOIN` permite que también aparezcan géneros que todavía no tengan películas.
