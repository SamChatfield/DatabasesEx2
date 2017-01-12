import java.util.Random;
import java.util.Scanner;
import java.sql.*;

public class CreateDB {
	
	public CreateDB() {
		Scanner in = new Scanner(System.in);
		String username;
		String password;
		
		System.out.println("Username:");
		username = in.nextLine();
		System.out.println("Password:");
		password = in.nextLine();
		
		String url = "jdbc:postgresql://mod-intro-databases.cs.bham.ac.uk/" + username;
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
		// POPULATE CHILD:
		// Data to choose from randomly when generating children
		String[] names = {"Timmy", "Alex", "Ben", "Toby", "Lucy", "Alice", "Paul", "Sophie", "Lily", "Peter", "Joe", "Sandra", "John", "Jane"};
		int houseNoBound = 99;
		String[] roads = {"High Street", "Station Road", "Main Street", "Park Road", "Church Road", "Church Street", "London Road", "Victoria Road", "Green Lane", "Manor Road", "Park Avenue", "The Crescent", "Grange Road", "Mill Lane"};
		String[] cities = {"London", "Birmingham", "Bristol", "Bath", "Coventry", "York", "Leeds", "Manchester", "Liverpool", "Sheffield", "Glasgow", "Edinburgh", "Cardiff", "Belfast", "Leicester", "Nottingham", "Newcastle", "Brighton"};
		
		// Query and Statement to insert a child
		String insertChildQuery = "INSERT INTO Child VALUES (?, ?, ?)";
		PreparedStatement insertChild = conn.prepareStatement(insertChildQuery);
		
		for (int i = 0; i < 1000; i++) {
			String name = names[rand.nextInt(names.length)];
			int houseNo = rand.nextInt(houseNoBound) + 1;
			String road = roads[rand.nextInt(roads.length)];
			String city = cities[rand.nextInt(cities.length)];
			String address = "" + houseNo + " " + road + ", " + city;
			insertChild.setInt(1, i);
			insertChild.setString(2, name);
			insertChild.setString(3, address);
			insertChild.execute();
		}
		
		// POPULATE SANTASLITTLEHELPER
		String insertHelperQuery = "INSERT INTO SantasLittleHelper VALUES (?, ?)";
		PreparedStatement insertHelper = conn.prepareStatement(insertHelperQuery);
		
		for (int i = 0; i < 10; i++) {
			insertHelper.setInt(1, i);
			insertHelper.setString(2, names[i]);
			insertHelper.execute();
		}
		
		// POPULATE GIFT
		String[] gifts = {"Lego Set", "Xbox One", "Playstation 4", "Doll", "Toy oven", "Train set", "Action figure", "Toy car", "Stuffed animal", "Board game"};
		String insertGiftQuery = "INSERT INTO Gift VALUES (?, ?)";
		PreparedStatement insertGift = conn.prepareStatement(insertGiftQuery);
		
		for (int i = 0; i < 10; i++) {
			insertGift.setInt(1, i);
			insertGift.setString(2, gifts[i]);
			insertGift.execute();
		}
		
		// POPULATE PRESENTS
		String insertPresentQuery = "INSERT INTO Present VALUES (?, ?, ?)";
		PreparedStatement insertPresent = conn.prepareStatement(insertPresentQuery);
		
		for (int i = 0; i < 100; i++) {
			int gid = rand.nextInt(10);
//			int cid = i;
			int cid = rand.nextInt(50);
			int slhid = rand.nextInt(10);
			insertPresent.setInt(1, gid);
			insertPresent.setInt(2, cid);
			insertPresent.setInt(3, slhid);
			insertPresent.execute();
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
