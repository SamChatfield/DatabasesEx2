import java.util.Scanner;
import java.sql.*;

public class CreateDB {
	
	public CreateDB() {
		Scanner in = new Scanner(System.in);
		String username;
		String password;
		String database = "sxc678";
		String url = "jdbc:postgresql://mod-intro-databases.cs.bham.ac.uk/" + database;
		
		System.out.println("Username:");
		username = in.nextLine();
		System.out.println("Password:");
		password = in.nextLine();
		
		Connection conn = connectDB(url, username, password);
		
		try {
			makeTables(conn);
			populateTables(conn);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Incorrect SQL in make or populate");
			System.exit(1);
		}
	}
	
	private void makeTables(Connection conn) throws SQLException {
//		System.out.println("makeTables");
		String createTablesString = ""
				+ "CREATE TABLE Child ("
					+ "cid INT PRIMARY KEY,"
					+ "name CHAR(20) NOT NULL,"
					+ "address CHAR(100) NOT NULL"
				+ ");"
				+ ""
				+ "CREATE TABLE SantasLittleHelper ("
					+ "slhid INT PRIMARY KEY,"
					+ "name CHAR(20) NOT NULL"
				+ ");"
				+ ""
				+ "CREATE TABLE Gift ("
					+ "gid INT PRIMARY KEY,"
					+ "description CHAR(50)"
				+ ");"
				+ ""
				+ "CREATE TABLE Present ("
					+ "gid INT,"
					+ "cid INT,"
					+ "slhid INT,"
					+ "FOREIGN KEY (gid) REFERENCES Gift (gid),"
					+ "FOREIGN KEY (cid) REFERENCES Child (cid),"
					+ "FOREIGN KEY (slhid) REFERENCES SantasLittleHelper (slhid)"
				+ ");";
		
		PreparedStatement createTables = conn.prepareStatement(createTablesString);
		createTables.execute();
	}
	
	private void populateTables(Connection conn) throws SQLException {
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
