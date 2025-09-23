import util.DatabaseConnection;
import util.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args){
        try{
            Connection conn = DatabaseConnection.getConnection();
            if(conn != null && !conn.isClosed()){
                System.out.println("la connexion avec la base de donnée est tested successfully ");
            }else{
                System.out.println("probleme de connexion");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

}
