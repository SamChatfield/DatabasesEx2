import java.util.Scanner;
import java.util.Scanner;
import java.sql.*;

public class DBInterface {
	
	private final String helpString = "help - returns a list of commands\n"
			+ "exit - terminates the program\n"
			+ "child <id> - report for a child with given id containing: id, name, address, set of presents they will receive\n"
			+ "helper <id> - report for a helper with given id containing: id, name, list of children's names, addresses and set of presents for each child";
	
	public DBInterface() {
		Scanner in = new Scanner(System.in);
		String username, password, url;
		
		boolean connSuccess = false;
		Connection conn = null;
		
		while (!connSuccess) {
			System.out.println("Username:");
			username = in.nextLine();
			System.out.println("Password:");
			password = in.nextLine();
			url = "jdbc:postgresql://mod-intro-databases.cs.bham.ac.uk/" + username;
			
			try {
				conn = DriverManager.getConnection(url, username, password);
				connSuccess = true;
			} catch (SQLException e) {
				System.out.println("Failed to connect to the database\n"
						+ "Check that the username and password are correct and try again\n");
			}
		}
		
		System.out.println("Connected to the database");
		
		run(conn);
	}
	
	private void run(Connection conn) {
		Scanner scan = new Scanner(System.in);
		boolean running = true;
		
		System.out.println("Type help for a list of commands");
		
		while (running) {
			String command[] = scan.nextLine().split(" ");
			
			switch (command[0]) {
			case "help":
				System.out.println(helpString);
				break;
			case "exit":
				exit(conn);
				break;
			case "child":
				if (command.length != 2) {
					System.out.println("Incorrect arguments - child <id>");
					break;
				}
				
				int cid;
				try {
					cid = Integer.parseInt(command[1]);
				} catch (NumberFormatException e) {
					System.out.println("Argument is not an id");
					break;
				}
				
				child(conn, cid);
				break;
			case "helper":
				if (command.length != 2) {
					System.out.println("Incorrect arguments - helper <id>");
					break;
				}
				
				int slhid;
				try {
					slhid = Integer.parseInt(command[1]);
				} catch (NumberFormatException e) {
					System.out.println("Argument is not an id");
					break;
				}
				
				helper(conn, slhid);
				break;
			default:
				System.out.println("Command not recognised");
				break;
			}
		}
	}
	
	private void child(Connection conn, int cid) {
		String output = "";
		
		String childInfoQuery = ""
				+ "SELECT * FROM Child WHERE Child.cid = ?;";
		String presentsQuery = ""
				+ "SELECT * FROM Gift WHERE Gift.gid IN (SELECT gid FROM Present WHERE cid = ?);";
		
		boolean emptyInfo = true;
		
		try {
			PreparedStatement childInfo = conn.prepareStatement(childInfoQuery);
			childInfo.setInt(1, cid);
			ResultSet childInfoResults = childInfo.executeQuery();
			
			while (childInfoResults.next()) {
				emptyInfo = false;
				int cidret = childInfoResults.getInt("cid");
				String name = childInfoResults.getString("name").trim();
				String address = childInfoResults.getString("address").trim();
				output += "Child Report\n"
						+ "ID: " + cidret + "\n"
						+ "Name: " + name + "\n"
						+ "Address: " + address + "\n"
						+ "Presents:\n";
			}
			
			if (emptyInfo) {
				output += "No child found with that ID\n";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Child info SQL error");
		}
		
		boolean emptyPresents = true;
		
		try {
			PreparedStatement presents = conn.prepareStatement(presentsQuery);
			presents.setInt(1, cid);
			ResultSet presentsResults = presents.executeQuery();
			
			while (presentsResults.next()) {
				emptyPresents = false;
				int gid = presentsResults.getInt("gid");
				String desc = presentsResults.getString("description").trim();
				output += gid + ", " + desc + "\n";
			}
			
			if (emptyPresents && !emptyInfo) {
				output += "No presents for this child\n";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Child presents SQL error");
		}
		
		System.out.println(output);
	}
	
	private void helper(Connection conn, int slhid) {
		
	}
	
	private void exit(Connection conn) {
		try {
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("SQL error closing conn");
        }
		System.exit(0);
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
