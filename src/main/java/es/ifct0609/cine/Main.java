package es.ifct0609.cine;

// Importamos las herramientas de Java que necesitamos para trabajar con H2,
// leer lo que escribe el usuario y crear la carpeta de la base de datos.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    // Dirección de la base de datos H2. Se guardará en la carpeta datos.
    // H2 crea automáticamente el archivo cine.mv.db cuando se conecta por primera vez.
    private static final String URL = "jdbc:h2:file:./datos/cine";
    private static final String USUARIO = "sa";
    private static final String CLAVE = "";

    // Un único Scanner nos permite leer las respuestas por consola.
    private static final Scanner teclado = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            // Creamos la carpeta si todavía no existe.
            Files.createDirectories(Path.of("datos"));

            // Cargamos el controlador JDBC de H2.
            Class.forName("org.h2.Driver");

            // Abrimos la conexión. Se cerrará automáticamente al terminar el bloque.
            try (Connection conexion = DriverManager.getConnection(URL, USUARIO, CLAVE)) {
                crearTablas(conexion);
                insertarDatosIniciales(conexion);
                mostrarMenu(conexion);
            }
        } catch (Exception error) {
            // Si ocurre algún problema, mostramos un mensaje comprensible.
            System.out.println("No se ha podido iniciar la aplicación.");
            System.out.println("Detalle del error: " + error.getMessage());
            System.out.println("Comprueba que tienes Java y que Maven puede descargar H2.");
        }

        teclado.close();
    }

    // Crea las tablas solo cuando aún no existen.
    private static void crearTablas(Connection conexion) throws SQLException {
        try (Statement sentencia = conexion.createStatement()) {
            // Una fila de GENERO puede estar relacionada con muchas películas.
            sentencia.execute("CREATE TABLE IF NOT EXISTS genero ("
                    + "id_genero INT PRIMARY KEY, "
                    + "nombre VARCHAR(50) NOT NULL UNIQUE"
                    + ")");

            // Cada película tiene una clave primaria y una clave ajena hacia GENERO.
            sentencia.execute("CREATE TABLE IF NOT EXISTS pelicula ("
                    + "id_pelicula INT PRIMARY KEY, "
                    + "titulo VARCHAR(100) NOT NULL, "
                    + "anio INT NOT NULL, "
                    + "nota DECIMAL(3,1) NOT NULL, "
                    + "id_genero INT NOT NULL, "
                    + "CONSTRAINT fk_pelicula_genero FOREIGN KEY (id_genero) "
                    + "REFERENCES genero(id_genero)"
                    + ")");
        }
    }

    // Añade ejemplos únicamente si las tablas están vacías; no duplica los datos al reiniciar.
    private static void insertarDatosIniciales(Connection conexion) throws SQLException {
        if (contarRegistros(conexion, "genero") == 0) {
            String sqlGenero = "INSERT INTO genero (id_genero, nombre) VALUES (?, ?)";

            try (PreparedStatement sentencia = conexion.prepareStatement(sqlGenero)) {
                sentencia.setInt(1, 1);
                sentencia.setString(2, "Ciencia ficción");
                sentencia.executeUpdate();

                sentencia.setInt(1, 2);
                sentencia.setString(2, "Comedia");
                sentencia.executeUpdate();

                sentencia.setInt(1, 3);
                sentencia.setString(2, "Aventura");
                sentencia.executeUpdate();

                sentencia.setInt(1, 4);
                sentencia.setString(2, "Animación");
                sentencia.executeUpdate();
            }
        }

        if (contarRegistros(conexion, "pelicula") == 0) {
            String sqlPelicula = "INSERT INTO pelicula "
                    + "(id_pelicula, titulo, anio, nota, id_genero) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement sentencia = conexion.prepareStatement(sqlPelicula)) {
                // Película 1: ciencia ficción.
                sentencia.setInt(1, 1);
                sentencia.setString(2, "Regreso al futuro");
                sentencia.setInt(3, 1985);
                sentencia.setDouble(4, 8.5);
                sentencia.setInt(5, 1);
                sentencia.executeUpdate();

                // Película 2: comedia.
                sentencia.setInt(1, 2);
                sentencia.setString(2, "Aterriza como puedas");
                sentencia.setInt(3, 1980);
                sentencia.setDouble(4, 7.8);
                sentencia.setInt(5, 2);
                sentencia.executeUpdate();

                // Película 3: aventura.
                sentencia.setInt(1, 3);
                sentencia.setString(2, "Indiana Jones y la última cruzada");
                sentencia.setInt(3, 1989);
                sentencia.setDouble(4, 8.2);
                sentencia.setInt(5, 3);
                sentencia.executeUpdate();

                // Película 4: animación.
                sentencia.setInt(1, 4);
                sentencia.setString(2, "Shrek");
                sentencia.setInt(3, 2001);
                sentencia.setDouble(4, 7.9);
                sentencia.setInt(5, 4);
                sentencia.executeUpdate();

                // Película 5: ciencia ficción.
                sentencia.setInt(1, 5);
                sentencia.setString(2, "Matrix");
                sentencia.setInt(3, 1999);
                sentencia.setDouble(4, 8.7);
                sentencia.setInt(5, 1);
                sentencia.executeUpdate();

                // Película 6: comedia.
                sentencia.setInt(1, 6);
                sentencia.setString(2, "El gran Lebowski");
                sentencia.setInt(3, 1998);
                sentencia.setDouble(4, 8.0);
                sentencia.setInt(5, 2);
                sentencia.executeUpdate();
            }
        }
    }

    // Cuenta filas en una tabla conocida. El nombre de la tabla no procede del usuario.
    private static int contarRegistros(Connection conexion, String tabla) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tabla;

        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {
            resultado.next();
            return resultado.getInt(1);
        }
    }

    // Muestra el menú principal y repite hasta que el usuario elija salir.
    private static void mostrarMenu(Connection conexion) throws SQLException {
        boolean salir = false;

        while (!salir) {
            System.out.println();
            System.out.println("========================================");
            System.out.println("       GESTOR DE PELÍCULAS DE CINE");
            System.out.println("========================================");
            System.out.println("1. Ver todas las películas (consulta simple)");
            System.out.println("2. Añadir una película");
            System.out.println("3. Buscar película por título");
            System.out.println("4. Ver películas y su género (JOIN)");
            System.out.println("5. Ver estadísticas por género (agregación)");
            System.out.println("6. Ver géneros disponibles");
            System.out.println("7. Eliminar una película por ID");
            System.out.println("0. Salir");

            int opcion = leerEntero("Elige una opción: ");

            // Ejecutamos la operación correspondiente a la opción seleccionada.
            switch (opcion) {
                case 1:
                    listarPeliculas(conexion);
                    break;
                case 2:
                    anadirPelicula(conexion);
                    break;
                case 3:
                    buscarPelicula(conexion);
                    break;
                case 4:
                    mostrarPeliculasConGenero(conexion);
                    break;
                case 5:
                    mostrarEstadisticas(conexion);
                    break;
                case 6:
                    listarGeneros(conexion);
                    break;
                case 7:
                    eliminarPelicula(conexion);
                    break;
                case 0:
                    salir = true;
                    System.out.println("¡Hasta pronto! Los cambios quedan guardados.");
                    break;
                default:
                    System.out.println("Opción no válida. Elige un número del menú.");
                    break;
            }
        }
    }

    // OPCIÓN 1: consulta SQL simple sobre una única tabla.
    private static void listarPeliculas(Connection conexion) throws SQLException {
        String sql = "SELECT id_pelicula, titulo, anio, nota FROM pelicula ORDER BY titulo";

        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {
            System.out.println("\n--- LISTA DE PELÍCULAS ---");
            System.out.printf("%-4s %-38s %-8s %-6s%n", "ID", "TÍTULO", "AÑO", "NOTA");

            while (resultado.next()) {
                System.out.printf("%-4d %-38s %-8d %-6.1f%n",
                        resultado.getInt("id_pelicula"),
                        resultado.getString("titulo"),
                        resultado.getInt("anio"),
                        resultado.getDouble("nota"));
            }
        }
    }

    // OPCIÓN 2: solicita los datos y guarda una película nueva.
    private static void anadirPelicula(Connection conexion) throws SQLException {
        System.out.println("\n--- AÑADIR PELÍCULA ---");
        String titulo = leerTexto("Título: ");
        int anio = leerEntero("Año de estreno: ");

        double nota;
        do {
            nota = leerDecimal("Nota (de 0 a 10): ");
            if (nota < 0 || nota > 10) {
                System.out.println("La nota debe estar entre 0 y 10.");
            }
        } while (nota < 0 || nota > 10);

        // Mostramos los géneros para que el usuario pueda seleccionar uno existente.
        listarGeneros(conexion);
        int idGenero = leerGeneroExistente(conexion);

        // Calculamos un nuevo ID tomando el mayor ID actual y sumándole uno.
        int nuevoId = obtenerSiguienteId(conexion);

        String sql = "INSERT INTO pelicula (id_pelicula, titulo, anio, nota, id_genero) "
                + "VALUES (?, ?, ?, ?, ?)";

        // PreparedStatement inserta los valores por separado y evita concatenarlos en SQL.
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, nuevoId);
            sentencia.setString(2, titulo);
            sentencia.setInt(3, anio);
            sentencia.setDouble(4, nota);
            sentencia.setInt(5, idGenero);
            sentencia.executeUpdate();
        }

        System.out.println("Película añadida correctamente con ID " + nuevoId + ".");
    }

    // OPCIÓN 3: busca coincidencias parciales en el título.
    private static void buscarPelicula(Connection conexion) throws SQLException {
        String texto = leerTexto("Escribe todo o parte del título: ");
        String sql = "SELECT id_pelicula, titulo, anio, nota FROM pelicula "
                + "WHERE LOWER(titulo) LIKE LOWER(?) ORDER BY titulo";

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, "%" + texto + "%");

            try (ResultSet resultado = sentencia.executeQuery()) {
                System.out.println("\n--- RESULTADOS DE LA BÚSQUEDA ---");
                boolean encontrado = false;

                while (resultado.next()) {
                    encontrado = true;
                    System.out.printf("ID: %d | %s (%d) | Nota: %.1f%n",
                            resultado.getInt("id_pelicula"),
                            resultado.getString("titulo"),
                            resultado.getInt("anio"),
                            resultado.getDouble("nota"));
                }

                if (!encontrado) {
                    System.out.println("No se han encontrado películas con ese título.");
                }
            }
        }
    }

    // OPCIÓN 4: JOIN combina la información de PELICULA y GENERO usando la clave ajena.
    private static void mostrarPeliculasConGenero(Connection conexion) throws SQLException {
        String sql = "SELECT p.id_pelicula, p.titulo, p.anio, p.nota, g.nombre AS genero "
                + "FROM pelicula p INNER JOIN genero g ON p.id_genero = g.id_genero "
                + "ORDER BY g.nombre, p.titulo";

        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {
            System.out.println("\n--- PELÍCULAS CON SU GÉNERO (JOIN) ---");

            while (resultado.next()) {
                System.out.printf("ID: %d | %s (%d) | Género: %s | Nota: %.1f%n",
                        resultado.getInt("id_pelicula"),
                        resultado.getString("titulo"),
                        resultado.getInt("anio"),
                        resultado.getString("genero"),
                        resultado.getDouble("nota"));
            }
        }
    }

    // OPCIÓN 5: GROUP BY agrupa por género y AVG/COUNT calculan estadísticas.
    private static void mostrarEstadisticas(Connection conexion) throws SQLException {
        String sql = "SELECT g.nombre, COUNT(p.id_pelicula) AS cantidad, "
                + "ROUND(AVG(p.nota), 2) AS nota_media "
                + "FROM genero g LEFT JOIN pelicula p ON g.id_genero = p.id_genero "
                + "GROUP BY g.id_genero, g.nombre ORDER BY g.nombre";

        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {
            System.out.println("\n--- ESTADÍSTICAS POR GÉNERO ---");

            while (resultado.next()) {
                System.out.printf("%-20s | Películas: %d | Nota media: %.2f%n",
                        resultado.getString("nombre"),
                        resultado.getInt("cantidad"),
                        resultado.getDouble("nota_media"));
            }
        }
    }

    // OPCIÓN 6: muestra los géneros que se pueden asignar a las películas.
    private static void listarGeneros(Connection conexion) throws SQLException {
        String sql = "SELECT id_genero, nombre FROM genero ORDER BY id_genero";

        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {
            System.out.println("\n--- GÉNEROS DISPONIBLES ---");

            while (resultado.next()) {
                System.out.println(resultado.getInt("id_genero") + ". "
                        + resultado.getString("nombre"));
            }
        }
    }

    // OPCIÓN 7: elimina una película a partir de su ID.
    private static void eliminarPelicula(Connection conexion) throws SQLException {
        int id = leerEntero("ID de la película que quieres eliminar: ");
        String sql = "DELETE FROM pelicula WHERE id_pelicula = ?";

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, id);
            int filasEliminadas = sentencia.executeUpdate();

            if (filasEliminadas > 0) {
                System.out.println("Película eliminada correctamente.");
            } else {
                System.out.println("No existe ninguna película con ese ID.");
            }
        }
    }

    // Pide un género hasta que se introduzca un ID que exista en la base de datos.
    private static int leerGeneroExistente(Connection conexion) throws SQLException {
        int idGenero;
        boolean existe;

        do {
            idGenero = leerEntero("Escribe el ID del género elegido: ");
            String sql = "SELECT COUNT(*) FROM genero WHERE id_genero = ?";

            try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                sentencia.setInt(1, idGenero);

                try (ResultSet resultado = sentencia.executeQuery()) {
                    resultado.next();
                    existe = resultado.getInt(1) > 0;
                }
            }

            if (!existe) {
                System.out.println("Ese género no existe. Elige uno de la lista.");
            }
        } while (!existe);

        return idGenero;
    }

    // Devuelve el siguiente ID libre para las nuevas películas.
    private static int obtenerSiguienteId(Connection conexion) throws SQLException {
        String sql = "SELECT COALESCE(MAX(id_pelicula), 0) + 1 FROM pelicula";

        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {
            resultado.next();
            return resultado.getInt(1);
        }
    }

    // Lee un entero y vuelve a preguntar si el usuario escribe algo que no es un número entero.
    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = teclado.nextLine().trim();

            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException error) {
                System.out.println("Introduce un número entero, por favor.");
            }
        }
    }

    // Lee un número decimal. Acepta tanto punto como coma decimal.
    private static double leerDecimal(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = teclado.nextLine().trim().replace(',', '.');

            try {
                return Double.parseDouble(texto);
            } catch (NumberFormatException error) {
                System.out.println("Introduce un número válido, por ejemplo 8,5.");
            }
        }
    }

    // Lee texto no vacío; así evitamos guardar una película sin título.
    private static String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = teclado.nextLine().trim();

            if (!texto.isEmpty()) {
                return texto;
            }

            System.out.println("Este campo no puede estar vacío.");
        }
    }
}
