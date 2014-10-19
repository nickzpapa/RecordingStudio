import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class SQLParser {
	
	static ArrayList<String> parseSqlFile(String file) {		
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
}
