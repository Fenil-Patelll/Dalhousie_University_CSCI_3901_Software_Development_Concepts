import java.sql.*;

//JDBC Class which will used ton make one time connection to Database
//the class object will return the object of Connection class which then will be used to make db connection in
//PowerService Constructor;
public class Jdbc {
    public Connection makeConnection() throws Exception{
        String url = "jdbc:mysql://db.cs.dal.ca:3306/fenilp";
        String uname = "fenilp";
        String pass = "B00917151";
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection c = DriverManager.getConnection(url,uname,pass);
        return c;

    }
}
