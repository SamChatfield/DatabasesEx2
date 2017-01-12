import java.util.Scanner;
import java.util.Scanner;
import java.sql.*;

public class DBInterface {
	
	private final String helpString = "help - returns a list of commands\n"
			+ "exit - terminates the program\n"
			+ "child <id> - report for a child with given id containing: id, name, address, set of presents they will receive\n"
			+ "helper <id> - report for a helper with given id containing: id, name, list of children's names, addresses and set of presents for each child"
	
	public DBInterface() {
		Scanner in = new Scanner(System.in);
		String username;
		String password;
		
		System.out.println("Username:");
		username = in.nextLine();
		System.out.println("Password:");
		password = in.nextLine();
		
		String url = "jdbc:postgresql://mod-intro-databases.cs.bham.ac.uk/" + username;
		Connection conn = connectDB(url, username, password);
		
		run(conn);
	}
	
	private void run() {
		Scanner scan = new Scanner(System.in);
		boolean running = true;
		
		System.out.println("Type help for a list of commands");
		
		while (running) {
			String line[] = scan.nextLine().split(" ");
			
			switch (line[0]) {
			case "help":
				System.out.println(helpString);
				break;
			case "exit":
				System.exit(0);
			case "child":
				if (line.length != 2) {
					System.out.println("Incorrect arguments - child <id>");
					break;
				}
				
				int cid;
				try {
					cid = Integer.parseInt(line[1]);
				} catch (NumberFormatException e) {
					System.out.println("Argument is not an id");
					break;
				}
				
				child(conn, cid);
			case "helper":
				if (line.length != 2) {
					System.out.println("Incorrect arguments - helper <id>");
					break;
				}
				
				int slhid;
				try {
					slhid = Integer.parseInt(line[1]);
				} catch (NumberFormatException e) {
					System.out.println("Argument is not an id");
					break;
				}
				
				helper(conn, slhid);
			default:
				System.out.println("Command not recognised");
				break;
			}
		}
	}
	
	private void child(Connection conn, int cid) {
		String output = "";
		
		String childInfoQuery = ""
				+ "SELECT *"
				+ "FROM Child"
				+ "WHERE Child.cid = ?";
		String presentsQuery = ""
				+ "SELECT *"
				+ "FROM Gift"
				+ "WHERE Gift.gid IN ("
					+ "SELECT gid"
					+ "FROM Present"
					+ "WHERE cid = ?"
				+ ")";
		
		try {
			PreparedStatement childInfo = conn.prepareStatement(childInfoQuery);
			childInfo.setInt(1, cid);
			ResultSet childInfoResults = childInfo.executeQuery();
			while (childInfoResults.next()) {
				int cid = childInfoResults.getInt("cid");
				String name = childInfoResults.getString("name");
				String address = childInfoResults.getString("address");
				output += "Child Report\n";
						+ "ID: " + cid + "\n"
						+ "Name: " + name + "\n"
						+ "Address: " + address + "\n"
						+ "Presents:\n";
			}
			
			PreparedStatement presents = conn.prepareStatement(presentsQuery);
			presents.setInt(1, cid);
			ResultSet presentsResults = presents.executeQuery();
			while (presentsResults.next()) {
				int gid = presentsResults.getInt("gid");
				String desc = presentsResults.getString("description");
				output += gid + ", " + desc + "\n";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("child SQL error");
		}
	}
	
	private void helper(Connection conn, int slhid) throws SQLException {
		
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
		DBInterface dbInterface = new DBInterface();
	}
	
}