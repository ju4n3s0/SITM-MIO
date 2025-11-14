package db;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestConnection {
    public static void main(String[] args) {
        try (Connection conn = DatabaseManager.getConnection()) {
            System.out.println("Conectado a PostgreSQL");

            try (Statement st = conn.createStatement()) {
                var rs = st.executeQuery("SELECT version()");
                if (rs.next()) {
                    System.out.println("Versión: " + rs.getString(1));
                }
            }
        } catch (Exception e) {
            System.out.println("Error conectando");
            e.printStackTrace();
        }
    }
}