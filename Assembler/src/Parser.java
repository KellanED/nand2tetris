
/* 
 * Kellan Delaney
 * Nand2Tetris Assembler
 * Parser Class, divides up assembly instruction into informational chunks
 * 06/20/2020
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Parser {

	private File f; // File which will be read from
	private Scanner in; // Scanner to read file
	private String cmd; // current command

	/**
	 * Constructor
	 * 
	 * @param f - Assembly file to be parsed
	 */
	public Parser(File f) {
		this.f = f; // initializes the file
		try {
			in = new Scanner(this.f); // initializes the scanner
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks if there is more to be read in the file
	 * 
	 * @return - true/false
	 */
	public boolean hasMoreCommands() {
		if (in.hasNext()) {
			return true;
		}
		in.close();
		return false;
	}

	/**
	 * Moves forward to the next command
	 */
	public void advance() {
		String x;
		do {
			x = in.nextLine();
		} while (x.equals("") || x.indexOf("//") == 0); // makes sure it advances to a real command
		if (x.contains("//")) {
			x = x.substring(0, x.indexOf("//")); // removes comment from command line
		}
		cmd = x.trim(); // removes excess whitespace from command line
	}

	/**
	 * Determines the command type of the current command
	 * 
	 * @return - "A_COMMAND", "C_COMMAND", "L_COMMAND"
	 */
	public String commandType() {
		if (cmd.contains("@")) { // A indicator
			return "A_COMMAND";
		} else if (cmd.contains("(")) { // L indicator
			return "L_COMMAND";
		} // otherwise its C
		return "C_COMMAND";
	}

	/**
	 * Determines the symbol for A and L commands
	 * 
	 * @return - symbol of A or L command
	 */
	public String symbol() {
		if (this.commandType().equals("A_COMMAND")) {
			int x = cmd.indexOf('@');
			return cmd.substring(x + 1); // returns Xxx from @Xxx
		}
		int x = cmd.indexOf('(');
		int y = cmd.indexOf(')', x);
		return cmd.substring(x + 1, y); // returns Xxx from (Xxx)
	}

	/**
	 * Finds destination information from a C command
	 * 
	 * @return - destination
	 */
	public String dest() {
		if (cmd.contains("=")) {
			int x = cmd.indexOf('=');
			return cmd.substring(0, x);
		}
		return "";
	}

	/**
	 * Finds computation information from a C command
	 * 
	 * @return - computation
	 */
	public String comp() {
		int x = cmd.indexOf('=');
		if (cmd.indexOf(';') != -1) {
			int y = cmd.indexOf(';');
			return cmd.substring(x + 1, y);
		}
		return cmd.substring(x + 1);
	}

	/**
	 * Finds jump information from a C command
	 * 
	 * @return - jump
	 */
	public String jump() {
		if (cmd.contains(";")) {
			int x = cmd.indexOf(';');
			return cmd.substring(x + 1);
		}
		return "";
	}

}
