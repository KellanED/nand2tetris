
/* 
 * Kellan Delaney
 * Nand2Tetris Assembler
 * SymbolTable class, container for predefined symbols, variables, and labels
 * 06/20/2020
 */

import java.util.HashMap;

public class SymbolTable {

	private HashMap<String, Integer> st; // symbol table is essentially just a hashmap

	/**
	 * Constructor
	 */
	public SymbolTable() {
		st = new HashMap<>();
	}

	/**
	 * Adds a symbol to the table
	 * 
	 * @param symbol  - symbol
	 * @param address - address/value to be associated with symbol
	 */
	public void addEntry(String symbol, int address) {
		st.put(symbol, address);
	}

	/**
	 * Determines if the symbol is already in the table
	 * 
	 * @param symbol - symbol to be checked
	 * @return - true if the table contains the symbol already
	 */
	public boolean contains(String symbol) {
		return st.containsKey(symbol);
	}

	/**
	 * Gets the address associated with a specific symbol
	 * 
	 * @param symbol - symbol to be checked
	 * @return - address associated with symbol, assuming symbol is in the table
	 */
	public int getAddress(String symbol) {
		return st.get(symbol);
	}

}
