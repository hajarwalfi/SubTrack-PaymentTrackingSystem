package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;


public class DatabaseConnection {
    private static Connection connection;
        private DatabaseConnection(){
            throw new UnsupportedOperationException("impossible d'instancier une classe utilitaiere!");
        }
        static{
            try{
                Properties props = new Properties();
                props.load(new FileInputStream("db.properties"));

                String url = props.getProperty("db.url");
                String user = props.getProperty("db.user");
                String password = props.getProperty("db.password");

                connection = DriverManager.getConnection(url,user,password);
                System.out.println("database connectée");

            } catch (IOException e) {
                System.err.println("la connexion a la base de donnée est echouée "+ e.getMessage());
            }catch (SQLException e){
                System.err.println("la connexion a la base de donnée est echouée" + e.getMessage());
            }
        }

        public static Connection getConnection(){
            return connection;
        }
}
