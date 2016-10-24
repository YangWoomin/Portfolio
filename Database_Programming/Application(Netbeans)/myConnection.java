
package mydbproject;
import java.sql.*;
import javax.swing.*;
public class myConnection {
    static Connection conn = null;
    
    public static Connection makeConn(){
       
        try {
         Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
 
        String url = "jdbc:sqlserver://Yang:1433;databaseName=Project";
        try{
   
            conn = DriverManager.getConnection(url, "NetbeansUser", "1234");
            JOptionPane.showMessageDialog(null, "getConnected");
            return conn;
        }
        catch(Exception e) {
          
            return null;
        }
        
    //return null;
    }
}
