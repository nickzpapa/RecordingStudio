import java.util.Scanner;


public class ClientPromt {
	
	//Main Entry
	public static void main(String[] args) {
		ClientPromt.run();
	}

	public static void run() {
		System.out.println("---------------------------------");
		
		//input for user 
		Scanner cin = new Scanner(System.in);	
		
		//getting a mysql user and password
		String sql_user, sql_password;
		System.out.print("Username: ");
		sql_user = cin.next();
		System.out.print("Password: ");
		sql_password = cin.next();		
		
		//getting a new database name and creating database
		String db_name;
		System.out.print("Enter new name for database: ");
		db_name = cin.next();
		System.out.println("---------------------------------");
		DatabaseHandler db = new DatabaseHandler(sql_user, sql_password, db_name);	//logging into mysql
		System.out.println(db_name + " was successfully created.");
			
		//adding tables, constraints, rows
		System.out.println("---------------------------------");
		System.out.println("Creating enterprise from sql file...");
		db.createEnterprise("recording_studios.sql");
		System.out.println("Enterprise successfully created.");		
		
		
		//Printing out a prompt menu
		boolean requestExit = false;
		while(!requestExit) {
			
			//Printing menu and getting option from user.
			System.out.println("---------------------------------");			
			System.out.println("Enter an option (1-6)");
			System.out.println("1. Album List");
			System.out.println("2. Album Info ");
			System.out.println("3. New Album");
			System.out.println("4. Remove an Album");
			System.out.println("5. Insert New Studio");
			System.out.println("6. Exit Program");
			System.out.print(":> ");
			String userChoice = cin.next();			
			System.out.println("---------------------------------");
			

			switch(userChoice) {
			
				//Printing the albums
				case "1" :
					db.printAlbumList();
					break;
					
					
				//Getting more info on an album
				case "2" :	{			
					System.out.println("Which album would you like more info on?");
					System.out.print(":> ");
					cin.nextLine();
					String a_name = cin.nextLine();
					System.out.println();
					db.getAlbumInfo(a_name);
					break;
				}
					
					
				//Asking user to insert new album
				case "3" :	{									
					String album_name, group_name, studio_name, date_rec, length, num_songs;
					cin.nextLine();
					System.out.print("Album: ");
						album_name = cin.nextLine();
					System.out.print("Group Name: ");
						group_name = cin.nextLine();
					System.out.print("Studio: ");
						studio_name = cin.nextLine();
					System.out.print("Date Recorded: ");
						date_rec = cin.nextLine();
					System.out.print("Album Length: ");
						length = cin.nextLine();
					System.out.print("No. Songs: ");
						num_songs = cin.nextLine();
						
					System.out.println("Inserting Album...");
					if(db.insertAlbum(album_name, group_name, studio_name, date_rec, length, num_songs))
						System.out.println("Album inserted in to the table.");
					break;
				}
					
					
				//Removing an album	
				case "4" : {
					cin.nextLine();
					System.out.println("Enter the album name to remove.");
					System.out.print(":> ");
					String album_name = cin.nextLine();
					if(db.deleteAlbum(album_name))
						System.out.println("Album successfully deleted.");
					break;
				}
					
				
				//Inserting new studio and republishing albums of another studio to the new one
				case "5" : {
					String old_studio, studio_name, address, owner, phone;
					cin.nextLine();
					System.out.print("Old recording studio: ");
						old_studio = cin.nextLine();
					System.out.print("New studio name: ");
						studio_name = cin.nextLine();
					System.out.print("New address: ");
						address = cin.nextLine();
					System.out.print("New owner: ");
						owner = cin.nextLine();
					System.out.print("New phone: ");
						phone = cin.nextLine();
						
					if(db.createNewStudio(old_studio, studio_name, address, owner, phone))
						System.out.println("Record studio successfully added.");
					break;
				}
					
				//Exiting
				case "6" :
					System.out.println("Exiting...");
					requestExit = true;
					break;
					
					
				//Invalid Input
				default	:
					System.err.println("Whoops that was an invalid input!");
					break;
				
			
			}	// end switch case	
			
		}	// end program menu loop
		
		
		
		//Drop the database	and exit
		System.out.println("Deleting database...");
		if(db.deleteDatabase())
			System.out.println("Database successfully deleted. ");	
		System.out.println("Goodbye!");
		System.out.println("---------------------------------");


	}


}
