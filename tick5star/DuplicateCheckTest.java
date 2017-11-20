package tick5star;

import java.sql.SQLException;

public class DuplicateCheckTest {

	public static void main(String[] args) {
		Database db;
		try {
			db = new Database("chat25");
			db.printDatabaseContents();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
