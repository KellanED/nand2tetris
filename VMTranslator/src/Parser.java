
/* 
 * Kellan Delaney
 * Nand2Tetris Virtual Machine Translator
 * Parser Class, divides up virtual machine code into informational chunks
 * 06/27/2020
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser {

	private File f; // file containing VM code
	private Scanner in; // Scanner to read file
	private String cmd; // current command

	private final ArrayList<String> ARITHCMD = new ArrayList<String>(); // to contain the 9 arithmetic commands

	/**
	 * Constructor
	 * 
	 * @param f - VM file to be parsed
	 * @throws FileNotFoundException 
	 */
	public Parser(File f) throws FileNotFoundException {
		this.f = f; // initializes the file
		in = new Scanner(this.f); // initializes the scanner
		ARITHCMD.add("add"); // adds arithmetic commands to array list
		ARITHCMD.add("sub");
		ARITHCMD.add("neg");
		ARITHCMD.add("eq");
		ARITHCMD.add("gt");
		ARITHCMD.add("lt");
		ARITHCMD.add("and");
		ARITHCMD.add("or");
		ARITHCMD.add("not");
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
	 * @return - "C_ARITHMETIC," "C_PUSH," "C_POP," "C_LABEL," "C_GOTO," "C_IF,"
	 *         "C_FUNCTION," "C_RETURN," "C_CALL"
	 */
	public String commandType() {
		if (ARITHCMD.contains(cmd)) {
			return "C_ARITHMETIC";
		}
		if (cmd.contains("push")) {
			return "C_PUSH";
		}
		if (cmd.contains("pop")) {
			return "C_POP";
		}
		if (cmd.contains("label")) {
			return "C_LABEL";
		}
		if (cmd.contains("goto")) {
			if (cmd.contains("if")) {
				return "C_IF";
			}
			return "C_GOTO";
		}
		if (cmd.contains("function")) {
			return "C_FUNCTION";
		}
		if (cmd.contains("return")) {
			return "C_RETURN";
		}
		if (cmd.contains("call")) {
			return "C_CALL";
		}
		return "ERROR";	
	}

	/**
	 * Returns the first argument of the current command
	 * 
	 * @return - first argument
	 */
	public String arg1() {
		if (commandType().equals("C_ARITHMETIC")) {
			return cmd; // simply returns the command if an arithmetic command
		}
		int x = cmd.indexOf(' ');
		String arg1 = cmd.substring(x + 1);
		if (arg1.contains(" ")) {
			x = arg1.indexOf(' ');
			arg1 = arg1.substring(0, x);
		}
		return arg1;
	}
	
	/**
	 * Returns the second argument of the current command
	 * 
	 * @return - second argument
	 */
	public String arg2() {
		int x = cmd.lastIndexOf(' ');
		String arg2 = cmd.substring(x+1);
		return arg2;
	}

}
