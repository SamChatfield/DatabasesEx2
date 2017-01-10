import java.util.Scanner;
import java.sql.*;

public class CreateDB {
	
	public CreateDB() {
		Scanner in = new Scanner(System.in);
		String username;
		String password;
		String database = "cslibrary";
		String url = "jdbc:postgresql://mod-intro-databases.cs.bham.ac.uk/" + database;
		
		username = in.nextLine();
		password = in.nextLine();
		
		Connection conn = connectDB(url, username, password);
		
		makeTables(conn);
		populateTables(conn);
	}
	
	private void makeTables(Connection conn) {
		System.out.println("makeTables");
	}
	
	private void populateTables(Connection conn) {
		System.out.println("populateTables");
	}
	
	private Connection connectDB(String url, String username, String password) {
		Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException ex) {
            System.out.println("Ooops, couldn't get a connection");
            System.out.println("Check that <username> & <password> are right");
            System.exit(1);
        }
        
        if (conn != null) {
            System.out.println("Database accessed!");
        } else {
            System.out.println("Failed to make connection");
            System.exit(1);
        }
        
        return conn;
	}
	
	public static void main(String[] args) {
		try {
            //Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException ex) {
        	ex.printStackTrace();
            System.out.println("Driver not found");
            System.exit(1);
        }
		
		System.out.println("PostgreSQL driver registered.");
		
        CreateDB createDB = new CreateDB();
	}
	
}
