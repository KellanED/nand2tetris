
/*
 * Kellan Delaney
 * Nand2Tetris Compiler Part 2: Code Generation
 * Tokenizer Class, parses the .jack file into "tokens"
 * 07/09/2020
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Tokenizer {

	private File input; // input jack file
	private Scanner scan; // scanner to read file
	private String jackCode = ""; // will contain all of the input jack code
	private String current; // the current token
	private int start = 0; // index of first character of current command
	private int end = 1; // index of last character of current command
	private ArrayList<String> keywords; // list of keywords
	private ArrayList<String> symbols; // list of symbols

	/**
	 * Constructor
	 * 
	 * @param input - .jack File to be read and tokenized
	 * @throws FileNotFoundException
	 */
	public Tokenizer(File input) throws FileNotFoundException {
		// Creates jackCode - the input program with no whitespace or comments
		this.input = input;
		scan = new Scanner(this.input);
		while (scan.hasNextLine()) {
			String nextLine = scan.nextLine();
			// removes inline commands "// ..."
			if (nextLine.contains("//")) {
				nextLine = nextLine.substring(0, nextLine.indexOf("//"));
				jackCode = jackCode + nextLine;
				continue;
			}
			// removes block and API comments "/*...*/" "/**...*/"
			if (nextLine.contains("/*") || nextLine.contains("/**")) {
				while (!(nextLine.contains("*/"))) {
					nextLine = scan.nextLine();
				}
				continue;
			}
			jackCode = jackCode + nextLine;
		}
		scan.close();
		jackCode = jackCode.trim(); // removes trailing and leading whitespace

		// creates important token lists
		keywords = new ArrayList<String>();
		List<String> kwList = Arrays.asList("class", "constructor", "function", "method", "field", "static", "var",
				"int", "char", "boolean", "void", "true", "false", "null", "this", "let", "do", "if", "else", "while",
				"return");
		keywords.addAll(kwList);
		symbols = new ArrayList<String>();
		List<String> sList = Arrays.asList("{", "}", "(", ")", "[", "]", ".", ",", ";", "+", "-", "*", "/", "&", "|",
				"<", ">", "=", "~");
		symbols.addAll(sList);
	}

	/**
	 * Returns if there are more tokens to be read in the file
	 * 
	 * @return - true or false
	 */
	public boolean hasMoreTokens() {
		if (end != jackCode.length() - 1) {
			return true;
		}
		return false;
	}

	/**
	 * Advances to the next token and makes it the current
	 */
	public void advance() {
		boolean found = false;
		while (!found) {
			// if at the end of the code, just take the last part
			if (end == jackCode.length()) {
				current = jackCode.substring(start);
				found = true;
			}
			// if the substring is a keyword or symbol, thats the token
			else if (keywords.contains(jackCode.substring(start, end).trim())
					| symbols.contains(jackCode.substring(start, end).trim())) {
				current = jackCode.substring(start, end).trim();
				found = true;
			}
			// if the substring is string literal enclosed by ""
			else if (jackCode.substring(start, end).contains("\"")) {
				start = jackCode.indexOf('"', start);
				end = jackCode.indexOf('"', start + 1) + 1;
				current = jackCode.substring(start, end).trim();
				found = true;
			}
			// if the substring contains a keyword, an identifer token lies before it
			else {
				for (String kw : keywords) {
					if (jackCode.substring(start, end).trim().contains(kw)
							& !(jackCode.substring(start, end).trim().contains("print"))) {
						end = jackCode.indexOf(kw, start);
						current = jackCode.substring(start, end).trim();
						found = true;
					}
				}
				for (String s : symbols) {
					if (jackCode.substring(start, end).trim().contains(s)) {
						end = jackCode.indexOf(s, start);
						current = jackCode.substring(start, end).trim();
						found = true;
					}
				}
			}
			if (found) {
				if (current.equals("")) {
					// System.out.println("empty token, going back");
					start++;
					end++;
					found = false;
				}
			}
			end++;
		}
		// checks for identifier followed by identifier
		if (current.contains(" ") & !(current.contains("\""))) {
			current = current.substring(0, current.indexOf(' '));
			start = jackCode.indexOf(current, start) + current.length();
		} else {
			start = --end;
		}
	}

	/**
	 * Returns the token type of the current token
	 * 
	 * @return - "KEYWORD" "SYMBOL" "IDENTIFIER" "INT_CONST" "STRING_CONST"
	 */
	public String tokenType() {
		if (keywords.contains(current)) {
			return "KEYWORD";
		} else if (symbols.contains(current)) {
			return "SYMBOL";
		} else if (current.contains("\"")) {
			return "STRING_CONST";
		} else {
			try {
				Integer.parseInt(current);
				return "INT_CONST";
			} catch (NumberFormatException e) {
				return "IDENTIFIER";
			}
		}
	}

	/**
	 * Returns the keyword which is the current token
	 * 
	 * @return
	 */
	public String keyWord() {
		return current;
	}

	/**
	 * Returns the character symbol which is the current token
	 * 
	 * @return
	 */
	public char symbol() {
		return current.charAt(0);
	}

	/**
	 * Returns the string identifier which is the current token
	 * 
	 * @return
	 */
	public String identifier() {
		return current;
	}

	/**
	 * Returns the integer value of the current token
	 * 
	 * @return
	 */
	public int intVal() {
		return Integer.parseInt(current);
	}

	/**
	 * Returns the string value of the current token without double quotes
	 * 
	 * @return
	 */
	public String stringVal() {
		return current.substring(1, current.length() - 1);
	}

}
