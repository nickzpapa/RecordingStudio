import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;


public class DatabaseHandler {
	
	//Objects used to interact with database
	private final String DATABASE_URL = "jdbc:mysql://127.0.0.1";	
	private Connection connection;
	private Statement statement;
	private	ResultSet resultSet;
	
	//Saves the database name
	String db_name;
	
	
	/**
	 * Constuctor
	 * @param user - User name to log into database
	 * @param pass - Password to log into database
	 * @param db_name -	The name of the databse to be created
	 */
	DatabaseHandler(String user, String pass, String db_name){
			
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(DATABASE_URL, user, pass);
		}
		catch(ClassNotFoundException e){
			System.err.println("Could not find mySQL driver.");
			e.printStackTrace();
			System.exit(-1);
		}
		catch (SQLException e) {
			System.err.println("\nCould not establish connection to mySQL server.");
			e.printStackTrace();
			System.exit(-1);
		}	
		createDatabase(db_name);
	}
	
	
	/**
	 * Creates a database using the name parameter
	 * @param db_name - The name of the new database
	 */
	void createDatabase(String db_name) {
		
		try {
			statement = connection.createStatement();
			statement.execute("CREATE DATABASE " + db_name );
			statement.execute("USE " + db_name );
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		this.db_name = db_name;
	}
	
	
	/**
	 * Creates a new enterprise within a database using a sql file or file with sql statements
	 * @param file_name - Name of the file containing sql statements
	 */
	void createEnterprise(String file_name){
		
		ArrayList<String> statements = parseSqlFile(file_name);
		for(String s : statements){
			try {
				statement.execute(s);
			} catch (SQLException e) {
				System.err.println("\nError parsing sql file.\n");
				System.err.println(e.getMessage());
				System.exit(-1);
			}
		}		
	}
	
	
	/**
	 * Parses an SQL file or file with SQL statements.
	 * @param file - The location of the file.
	 * @return An ArrayList<String> of all the statements to be used.
	 */
	public ArrayList<String> parseSqlFile(String file) {		
		ArrayList<String> statements = new ArrayList<String>();
		Scanner input = null;
		try {
			input = new Scanner(new File (file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String buffer = "";
		while(input.hasNext()) {
			buffer += input.nextLine();
			buffer.trim();
			if (buffer.length() == 0)
				continue;
			else if(buffer.charAt(buffer.length()-1) == ';') {
				statements.add(buffer);
				buffer = "";
			}			
		}		
		input.close();
		return statements;		
	}
	
	
	/**
	 * Prints the album list from the albums table in the database
	 * @return - True if successful, False if not
	 */
	boolean printAlbumList() {
		
		System.out.println("\tAlbum\n");
		try {
			resultSet = statement.executeQuery("SELECT album_name FROM albums ORDER BY album_name");
			
			ArrayList<String> album_names = new ArrayList<String>();
			
			while(resultSet.next()) {
				album_names.add(resultSet.getString("album_name"));	
				
				System.out.println( album_names.size() + ".\t" + resultSet.getString("album_name"));
			}			
			return true;				
		} 
		catch (SQLException e) {
			System.err.println("\nError getting list of albums from database.\n");
			return false;
		}
	}
	
	
	/**
	 * Prints info of a single album in the database
	 * @param album_name - The name of the album to obtain information on
	 */
	void getAlbumInfo(String album_name) {
		
		try {
			resultSet = statement.executeQuery("SELECT * FROM albums WHERE album_name='" + album_name + "'");
			resultSet.next();
			
			album_name = resultSet.getString("album_name");
			String group_name = resultSet.getString("group_name");
			String studio_name = resultSet.getString("studio_name");
			Date date_rec = resultSet.getDate("date_rec");
			Time length = resultSet.getTime("length");
			int num_songs = resultSet.getInt("num_songs");
			
			System.out.println("Album:\t\t" + album_name);
			System.out.println("Artist:\t\t" + group_name);
			System.out.println("Studio:\t\t" + studio_name);
			System.out.println("Date:\t\t"+ date_rec);
			System.out.println("Length:\t\t" + length);
			System.out.println("No. Tracks:\t" + num_songs);
			
			
		} catch (SQLException e) {
			System.err.println("\nCould not get info on album.\n");
		}
			
	}
	
	
	/**
	 * Issues an sql statement to insert a new album into the database with the provided information
	 * @param album_name
	 * @param group_name
	 * @param studio_name
	 * @param date_rec
	 * @param length
	 * @param num_songs
	 * @return - True if insert was successful, False if not
	 */
	boolean insertAlbum(String album_name, String group_name, String studio_name, String date_rec, String length, String num_songs) {
		
		try {
			statement.execute(
					"INSERT INTO albums VALUES ('" 
					 + album_name + "', '" + group_name + "', '" + studio_name + "', '" 
					 + date_rec.toString() +  "', '" + length.toString() + "', '" + num_songs + "')"
					 );
			return true;
		} catch (SQLException e) {
			System.err.println("\n" + e.getMessage());
			return false;
		}		
	}
	
	
	
	/**
	 * Creates new studio in studios table,
	 * then takes updates the albums of another studio to be published by the newly created one.
	 * 
	 * @param old_studio
	 * @param studio_name
	 * @param address
	 * @param owner
	 * @param phone
	 * @return - True if insert was successful, False if not
	 */
	boolean createNewStudio(String old_studio, String studio_name, String address, String owner, String phone) {
		
		//inserting the new studio
		try {
			statement.execute(	"INSERT INTO studios VALUES ('" + studio_name + "', '" + address + 
								"', '" + owner + "', '" + phone + "')"
			);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Error inserting new studio.");
			return false;
		}
		
		//Updating albums to the new studio from the old one
		try {
			int rowsUpdated = statement.executeUpdate(	"UPDATE albums SET studio_name='" + studio_name +
								"' WHERE studio_name='" + old_studio + "'");
			if(rowsUpdated>0)
				return true;
			else
				System.err.println("\nCould not find the old studio.");
			return false;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Error updating album studios.");
			return false;
		}
		
	}
	
	
	/**
	 * Issues an sql command to delete an album
	 * @param album_name - Name of the album to delete
	 * @return - True if delete was successful, False if not
	 */
	boolean deleteAlbum(String album_name) {
		
		try {
			if(statement.executeUpdate("DELETE FROM albums WHERE album_name='" + album_name + "'") > 0) {
				return true;
			}
			else {
				System.err.println("\nAlbum does not exist.");
				return false;
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			return false;
		}
		
	}
	
	
	/**
	 * Deletes the database that was originally created
	 * @return - True if successful database deletion, False if not
	 */
	boolean deleteDatabase(){
		if(!db_name.isEmpty())
			try {
				statement.execute( "DROP DATABASE " + db_name );
				return true;
			} catch (SQLException e) {
				System.err.println("\nError trying to delete database.\n");
				return false;
			}
		else
			System.err.println("\nThere is no current database to delete.\n");
			return false;			
	}
	
}
