import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Loads the .env file from the project root
    private static final Dotenv dotenv = Dotenv.load();

    private static final String URL = dotenv.get("SUPABASE_URL");
    private static final String USER = dotenv.get("SUPABASE_USER");
    private static final String PASSWORD = dotenv.get("SUPABASE_PASSWORD");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}