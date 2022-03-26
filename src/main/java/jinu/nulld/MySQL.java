package jinu.nulld;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {
    FileConfiguration config = YamlConfiguration.loadConfiguration(new File(Main.getPlugin(Main.class).getDataFolder(), "dbconfig.yml"));

    private Connection connection;

    public boolean isConnected() {
        return (connection != null);
    }

    public void connect() throws ClassNotFoundException, SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://skyisle.life:3606/pixel?useUnicode=true&characterEncoding=utf8&allowPublicKeyRetrieval=true&useSSL=false", "pixel", "pixelsql22!");
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
