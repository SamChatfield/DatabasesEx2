import java.sql.*;

public class dbtest {

    public dbtest() {
    }


    public void connectToDB(String url, String username, String pass) {

        System.out.println("Example showing connection to mod-intro-databases server");

        try {

            //Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException ex) {
            System.out.println("Driver not found");
            System.exit(1);
        }

        System.out.println("PostgreSQL driver registered.");

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, username, pass);
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
        
        // As a test, let's print out all the books' titles
        
        System.out.println("Here's a list of the titles of all the books in the library");
        System.out.println();
        try {
        	PreparedStatement studentQuery = conn.prepareStatement(
        		"SELECT * FROM Books ORDER By title;");
        
        
        	ResultSet rs = studentQuery.executeQuery();
        
        	String title = null;
        
        	// Now interate through the books just picking up the title
        	while (rs.next()) {
        		title= rs.getString("title");
        		System.out.println(title);
        	}
        } catch (SQLException sqlE) {
        	System.out.println("SQL code is broken");
        }
        
        //Now, just tidy up by closing connection
        try {
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Main method to connect to database on the module server.
    public static void main(String[] args) {

        String username = "sxc678";
        String password = "Hyperion32-64";
        String database = "cslibrary";
        String URL = "jdbc:postgresql://mod-intro-databases.cs.bham.ac.uk/" + database;
        // Need it fully qualified if connecting via vpn

        dbtest test = new dbtest();
        
        test.connectToDB(URL, username, password);
        
    }
}