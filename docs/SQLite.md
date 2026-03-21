# Módulo `persistence` – Gestión de Base de Datos SQLite

Este módulo contiene toda la lógica relacionada con la persistencia de datos del proyecto.  
Su objetivo es proporcionar una capa sencilla y reutilizable para:

- Conectarse a la base de datos SQLite.
- Inicializar las tablas necesarias.
- Insertar registros desde los módulos de scraping o API.

La base de datos utilizada es **SQLite**, almacenada en el archivo:




---

## 🔌 1. `DatabaseConnection`

Clase encargada de gestionar la conexión con la base de datos SQLite.

### Funciones principales

- Mantiene la URL de conexión (`jdbc:sqlite:data/housing.db`).
- Devuelve una conexión activa mediante `DriverManager`.
- Centraliza el acceso a la base de datos para el resto del proyecto.

### Código relevante

```java
public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:data/housing.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}

public static void init() {
    createPropertiesTable();
}

CREATE TABLE IF NOT EXISTS properties (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    price REAL NOT NULL,
    url TEXT NOT NULL,
    captured_at TEXT NOT NULL
);
```


---

## 🏗️ 2. `DatabaseInitializer`

Esta clase se encarga de crear las tablas necesarias en la base de datos cuando el proyecto se inicia.  
Su función principal es garantizar que la estructura de la base de datos exista antes de insertar datos.

### Funciones principales

- Crear la tabla `properties` si no existe.
- Centralizar la inicialización de la base de datos.
- Evitar errores por tablas inexistentes.

### Código relevante

```java
public class DatabaseInitializer {

    public static void init() {
        createPropertiesTable();
    }

    private static void createPropertiesTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS properties (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                price REAL NOT NULL,
                url TEXT NOT NULL,
                captured_at TEXT NOT NULL
            );
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("Tabla properties creada o ya existente");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

```


---

## 🗂️ 3. `PropertyRepository`

Esta clase actúa como el repositorio encargado de insertar datos en la tabla `properties`.  
Es utilizada por los módulos de scraping o APIs para almacenar cada propiedad obtenida.

### Funciones principales

- Insertar propiedades en la base de datos SQLite.
- Encapsular la lógica SQL de inserción.
- Facilitar la reutilización desde distintos módulos del proyecto.

### Código relevante

```java
public class PropertyRepository {

    public void save(String title, double price, String url, String capturedAt) {

        String sql = "INSERT INTO properties(title, price, url, captured_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setDouble(2, price);
            stmt.setString(3, url);
            stmt.setString(4, capturedAt);

            stmt.executeUpdate();

            System.out.println("Propiedad guardada");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
