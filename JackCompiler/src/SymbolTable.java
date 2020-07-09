
/*
 * Kellan Delaney
 * Nand2Tetris Compiler Part 2: Code Generation
 * SymbolTable Class, provides a symbol table abstraction that associates identifier names found in the program with properties needed for compilation
 * 07/06/2020
 */

import java.util.HashMap;
import java.util.Map.Entry;

public class SymbolTable {

	private HashMap<String, String> classScope; // scope for class level variables
	private HashMap<String, String> subScope; // scope for local level variables
	private int staticIndex = 0; // index for static variables
	private int fieldIndex = 0; // index for field variables
	private int argIndex = 0; // index for argument variables
	private int varIndex = 0; // index for local variables

	/**
	 * Constructor
	 */
	public SymbolTable() {
		classScope = new HashMap<>();
		subScope = new HashMap<>();
	}

	/**
	 * Starts a new subroutine scope (resets the subroutine symbol table)
	 */
	public void startSubroutine() {
		subScope.clear();
		argIndex = 0;
		varIndex = 0;
	}

	/**
	 * Defines a new identifier of a given name, type, and kind and assigns it to a
	 * running index
	 * 
	 * @param name
	 * @param type
	 * @param kind - "STATIC" "FIELD" "ARG" "VAR"
	 */
	public void define(String name, String type, String kind) {
		if (kind.equals("STATIC")) {
			classScope.put(name, staticIndex++ + "$" + type + "$" + kind);
		} else if (kind.equals("FIELD")) {
			classScope.put(name, fieldIndex++ + "$" + type + "$" + kind);
		} else if (kind.equals("ARG")) {
			subScope.put(name, argIndex++ + "$" + type + "$" + kind);
		} else if (kind.equals("VAR")) {
			subScope.put(name, varIndex++ + "$" + type + "$" + kind);
		} else {
			System.out.println("Tried to define a symbol with an unkown kind");
			return;
		}
	}

	/**
	 * Returns the number of variables of the given kind already defined in the
	 * current scope
	 * 
	 * @param kind - "STATIC" "FIELD" "ARG" "VAR"
	 * @return
	 */
	public int varCount(String kind) {
		int count = 0;
		for (Entry<String, String> entry : classScope.entrySet()) {
			String value = entry.getValue();
			int x = value.lastIndexOf('$');
			value = value.substring(x + 1);
			if (kind.equals(value)) {
				count++;
			}
		}
		for (Entry<String, String> entry : subScope.entrySet()) {
			String value = entry.getValue();
			int x = value.lastIndexOf('$');
			value = value.substring(x + 1);
			if (kind.equals(value)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Returns the kind of the named identifier in the current scope. If the
	 * identifier is unkown in the current scope, returns "NONE"
	 * 
	 * @return - "STATIC" "FIELD" "ARG" "VAR" "NONE"
	 */
	public String kindOf(String name) {
		String kind = "NONE";
		if (subScope.containsKey(name)) {
			kind = subScope.get(name).substring(subScope.get(name).lastIndexOf('$') + 1);
		} else if (classScope.containsKey(name)) {
			kind = classScope.get(name).substring(classScope.get(name).lastIndexOf('$') + 1);
		}
		return kind;
	}

	/**
	 * Returns the type of the named identifier in the current scope
	 * 
	 * @param name
	 * @return
	 */
	public String typeOf(String name) {
		String type = "NONE";
		if (subScope.containsKey(name)) {
			type = subScope.get(name).substring(subScope.get(name).indexOf('$') + 1,
					subScope.get(name).lastIndexOf('$'));
		} else if (classScope.containsKey(name)) {
			type = classScope.get(name).substring(classScope.get(name).indexOf('$') + 1,
					classScope.get(name).lastIndexOf('$'));
		}
		return type;
	}

	/**
	 * Returns the index assigned to the named identifier
	 * 
	 * @param name
	 * @return
	 */
	public int indexOf(String name) {
		int index = -1;
		if (subScope.containsKey(name)) {
			index = Integer.parseInt(subScope.get(name).substring(0, subScope.get(name).indexOf('$')));
		} else if (classScope.containsKey(name)) {
			index = Integer.parseInt(classScope.get(name).substring(0, classScope.get(name).indexOf('$')));
		}
		return index;
	}

}
