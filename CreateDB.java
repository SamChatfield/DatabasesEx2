import java.util.Random;
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
		String createTablesString = ""
				+ "CREATE TABLE Child ("
					+ "cid INT PRIMARY KEY,"
					+ "name CHAR(20) NOT NULL,"
					+ "address CHAR(50) NOT NULL"
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
//		System.out.println("populateTables");
		Random rand = new Random();
		// Populate Children:
		// Data to choose from randomly when generating children
		String[] cNames = {"Timmy", "Alex", "Ben", "Toby", "Lucy", "Alice", "Paul", "Sophie", "Lily", "Peter", "Joe", "Sandra", "John", "Jane"};
		int houseNoBound = 100;
		String[] roads = {"High Street", "Station Road", "Main Street", "Park Road", "Church Road", "Church Street", "London Road", "Victoria Road", "Green Lane", "Manor Road", "Park Avenue", "The Crescent", "Grange Road", "Mill Lane"};
		String[] cities = {"London", "Birmingham", "Bristol", "Bath", "Coventry", "York", "Leeds", "Manchester", "Liverpool", "Sheffield"};
		
		// Query and Statement to insert a child
		String insertChildQuery = "INSERT INTO Child VALUES (?, ?, ?)";
		PreparedStatement insertChild = conn.prepareStatement(insertChildQuery);
		
		for (int i = 0; i < 1000; i++) {
			String name = cNames[rand.nextInt(cNames.length)];
			int houseNo = rand.nextInt(houseNoBound);
			String road = roads[rand.nextInt(roads.length)];
			String city = cities[rand.nextInt(cities.length)];
			String address = "" + houseNo + " " + road + ", " + city;
			insertChild.setInt(1, i);
			insertChild.setString(2, name);
			insertChild.setString(3, address);
			insertChild.execute();
		}
		System.out.println("Population complete");
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
