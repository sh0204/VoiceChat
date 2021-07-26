
import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class DataBase {

    public static Connection connect() {

        Connection conn = null;

        try {
             Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "bandal2305");
            Component rootPane = null;
            System.out.println("연결되었습니다.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }

        return conn;

    }

}
