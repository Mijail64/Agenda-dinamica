package com.mijail64;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import java.sql.ResultSetMetaData;

/**
 * Clase utilitaria para guardar/leer la matriz de la agenda en SQLite.
 * Requiere el driver JDBC de SQLite en el classpath (org.xerial:sqlite-jdbc).
 *
 * Uso básico:
 * AgendaStorage db = new AgendaStorage("agenda.db");
 * db.saveMatrix(1, contenido);
 * String[][][] contenido = db.loadMatrix(1);
 */
public class AgendaStorage {
    private final String dbUrl;

    public AgendaStorage(String dbPath) throws SQLException {
        // jdbc:sqlite: crea el archivo si no existe
        this.dbUrl = "jdbc:sqlite:" + dbPath;
        init();
    }

    private void init() throws SQLException {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS agenda_cells ("
                    + "agenda_id INTEGER NOT NULL,"
                    + "day INTEGER NOT NULL,"
                    + "hour INTEGER NOT NULL,"
                    + "name TEXT,"
                    + "description TEXT,"
                    + "tag TEXT,"
                    + "priority TEXT,"
                    + "PRIMARY KEY(agenda_id, day, hour)"
                    + ");";
            stmt.execute(sql);
            // Tabla de metadatos de agendas
            String sql2 = "CREATE TABLE IF NOT EXISTS agendas ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name TEXT NOT NULL UNIQUE,"
                    + "description TEXT,"
                    + "created_at TEXT"
                    + ");";
            stmt.execute(sql2);
        }
    }

    public static class AgendaRecord {
        public final int id;
        public final String name;
        public final String description;
        public final String createdAt;

        public AgendaRecord(int id, String name, String description, String createdAt) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.createdAt = createdAt;
        }
    }

    public int createAgenda(String name, String description) throws SQLException {
        String sql = "INSERT INTO agendas(name, description, created_at) VALUES(?, ?, datetime('now'))";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, description);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("No se pudo crear agenda");
    }

    public List<AgendaRecord> listAgendas() throws SQLException {
        List<AgendaRecord> out = new ArrayList<>();
        String sql = "SELECT id, name, description, created_at FROM agendas ORDER BY id";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new AgendaRecord(rs.getInt("id"), rs.getString("name"), rs.getString("description"), rs.getString("created_at")));
            }
        }
        return out;
    }

    public void deleteAgenda(int agendaId) throws SQLException {
        String sql = "DELETE FROM agendas WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, agendaId);
            ps.executeUpdate();
        }
        // opcional: borrar celdas asociadas
        String sql2 = "DELETE FROM agenda_cells WHERE agenda_id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql2)) {
            ps.setInt(1, agendaId);
            ps.executeUpdate();
        }
    }

    public void renameAgenda(int agendaId, String newName) throws SQLException {
        String sql = "UPDATE agendas SET name = ? WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setInt(2, agendaId);
            ps.executeUpdate();
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    /**
     * Guarda toda la matriz 7 x 24 x 4 para un agenda_id.
     * El arreglo debe tener dimensiones [7][24][4], pero el método tolera nulls.
     */
    public void saveMatrix(int agendaId, String[][][] matrix) throws SQLException {
        if (matrix == null) throw new IllegalArgumentException("matrix == null");

        String sql = "INSERT INTO agenda_cells(agenda_id, day, hour, name, description, tag, priority) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?) "
                + "ON CONFLICT(agenda_id, day, hour) DO UPDATE SET "
                + "name=excluded.name, description=excluded.description, tag=excluded.tag, priority=excluded.priority;";

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (int d = 0; d < Math.min(7, matrix.length); d++) {
                if (matrix[d] == null) continue;
                for (int h = 0; h < Math.min(24, matrix[d].length); h++) {
                    String[] cell = matrix[d][h];
                    String name = "", desc = "", tag = "", pr = "";
                    if (cell != null) {
                        if (cell.length > 0 && cell[0] != null) name = cell[0];
                        if (cell.length > 1 && cell[1] != null) desc = cell[1];
                        if (cell.length > 2 && cell[2] != null) tag = cell[2];
                        if (cell.length > 3 && cell[3] != null) pr = cell[3];
                    }
                    ps.setInt(1, agendaId);
                    ps.setInt(2, d);
                    ps.setInt(3, h);
                    ps.setString(4, name);
                    ps.setString(5, desc);
                    ps.setString(6, tag);
                    ps.setString(7, pr);
                    ps.addBatch();
                }
            }
            ps.executeBatch();
            conn.commit();
        }
    }

    /**
     * Carga la matriz para un agenda_id. Siempre devuelve un arreglo [7][24][4] no-nulo.
     */
    public String[][][] loadMatrix(int agendaId) throws SQLException {
        String[][][] matrix = new String[7][24][4];
        // inicializar con cadenas vacías
        for (int d = 0; d < 7; d++) for (int h = 0; h < 24; h++) Arrays.fill(matrix[d][h], "");

        String sql = "SELECT day, hour, name, description, tag, priority FROM agenda_cells WHERE agenda_id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, agendaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int d = rs.getInt("day");
                    int h = rs.getInt("hour");
                    if (d < 0 || d >= 7 || h < 0 || h >= 24) continue;
                    String name = rs.getString("name");
                    String desc = rs.getString("description");
                    String tag = rs.getString("tag");
                    String pr = rs.getString("priority");
                    matrix[d][h][0] = name != null ? name : "";
                    matrix[d][h][1] = desc != null ? desc : "";
                    matrix[d][h][2] = tag != null ? tag : "";
                    matrix[d][h][3] = pr != null ? pr : "";
                }
            }
        }
        return matrix;
    }

    /**
     * Helper: borra todas las celdas asociadas a un agenda_id (útil para pruebas).
     */
    public void clearAgenda(int agendaId) throws SQLException {
        String sql = "DELETE FROM agenda_cells WHERE agenda_id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, agendaId);
            ps.executeUpdate();
        }
    }
}
