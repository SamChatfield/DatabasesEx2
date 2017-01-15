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
		
		String childInfoQuery = "SELECT * FROM Child WHERE cid = ?;";
		String presentsQuery = "SELECT * FROM Gift WHERE Gift.gid IN (SELECT gid FROM Present WHERE cid = ?);";
		PreparedStatement childInfo = null;
		PreparedStatement presents = null;
		
		try {
			childInfo = conn.prepareStatement(childInfoQuery);
			presents = conn.prepareStatement(presentsQuery);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error preparing the child queries");
		}
		
		String helperInfoQuery = "SELECT * FROM SantasLittleHelper WHERE slhid = ?;";
		String helperChildrenQuery = "SELECT * FROM Child WHERE Child.cid IN (SELECT cid FROM Present WHERE slhid = ?);";
		PreparedStatement helperInfo = null;
		PreparedStatement helperChildren = null;
		
		try {
			helperInfo = conn.prepareStatement(helperInfoQuery);
			helperChildren = conn.prepareStatement(helperChildrenQuery);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error preparing the helper queries");
		}
		
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
				
				try {
					childInfo.setInt(1, cid);
					presents.setInt(1, cid);
					child(childInfo, presents);
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("Error setting child query parameters");
				}
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
				
				try {
					helperInfo.setInt(1, slhid);
					helperChildren.setInt(1, slhid);
					helper(helperInfo, helperChildren, presents);
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("Error setting helper query parameters");
				}
				break;
			default:
				System.out.println("Command not recognised");
				break;
			}
		}
	}
	
	private void child(PreparedStatement childInfo, PreparedStatement presents) {
		String output = "";
		
		boolean emptyInfo = true;
		
		try {
			ResultSet childInfoResults = childInfo.executeQuery();
			
			while (childInfoResults.next()) {
				emptyInfo = false;
				int cid = childInfoResults.getInt("cid");
				String name = childInfoResults.getString("name").trim();
				String address = childInfoResults.getString("address").trim();
				output += "Child Report\n"
						+ "ID: " + cid + "\n"
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
	
	private void helper(PreparedStatement helperInfo, PreparedStatement helperChildren, PreparedStatement presents) {
		String output = "";
		
		boolean emptyInfo = true;
		
		try {
			ResultSet helperInfoResults = helperInfo.executeQuery();
			
			while (helperInfoResults.next()) {
				emptyInfo = false;
				int slhid = helperInfoResults.getInt("slhid");
				String name = helperInfoResults.getString("name").trim();
				output += "Helper Report\n"
						+ "ID: " + slhid + "\n"
						+ "Name: " + name + "\n"
						+ "Children assigned to:\n";
			}
			
			if (emptyInfo) {
				output += "No helper found with that ID\n";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Helper info SQL error");
		}
		
		boolean emptyChildren = true;
		
		try {
			ResultSet helperChildrenResults = helperChildren.executeQuery();
			
			while (helperChildrenResults.next()) {
				emptyChildren = false;
				int cid = helperChildrenResults.getInt("cid");
				String name = helperChildrenResults.getString("name").trim();
				String address = helperChildrenResults.getString("address").trim();
				output += "Child ID: " + cid + "\n"
						+ "Child name: " + name + "\n"
						+ "Child address: " + address + "\n"
						+ "Child presents:\n";
				
				try {
					presents.setInt(1, cid);
					output += presentsOutput(presents);
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("Helper child presents SQL error");
				}
			}
			
			if (emptyChildren && !emptyInfo) {
				output += "No children assigned to this helper\n";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Helper children SQL error");
		}
		
		System.out.println(output);
	}
	
	private String presentsOutput(PreparedStatement presents) throws SQLException {
		String output = "";
		ResultSet presentsResults = presents.executeQuery();
		boolean emptyPresents = true;
		
		while (presentsResults.next()) {
			emptyPresents = false;
			int gid = presentsResults.getInt("gid");
			String desc = presentsResults.getString("description").trim();
			output += gid + ", " + desc + "\n";
		}
		
		if (emptyPresents) {
			output += "No presents for this child\n";
		}
		
		return output;
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
