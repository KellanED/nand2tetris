
/*
 * Kellan Delaney
 * Nand2Tetris Compiler Part 2: Code Generation
 * CompilationEngine Class, compiles all parts of the program using the tokens, writes code to VM file
 * 07/09/2020
 */

import java.io.File;
import java.io.IOException;

public class CompilationEngine {

	private Tokenizer token; // current token
	private File output; // output .vm file
	private VMWriter vmwriter; // writer to the .vm file
	private SymbolTable symbols; // symbol table, used for handling variables and identifiers
	private String className; // name of the class
	private int whileN = 0; // used to keep while labels unique
	private int ifN = 0; // used to keep if labels unique
	private boolean object = false; // indicates if the current class can be instantiated

	/**
	 * Constructor
	 * 
	 * @param input  - Tokenizer
	 * @param output - output file, .vm
	 * @throws IOException
	 */
	public CompilationEngine(Tokenizer input, File output) throws IOException {
		this.token = input;
		this.output = output;
		vmwriter = new VMWriter(this.output);
		symbols = new SymbolTable();
		token.advance();
		compileClass();
		vmwriter.close();
	}

	/**
	 * Compiles a complete class
	 * 
	 * @throws IOException
	 */
	private void compileClass() throws IOException {
		token.advance();
		className = token.identifier();
		token.advance();
		// repeat until there is nothing left in the class
		while (token.hasMoreTokens()) {
			token.advance();
			if (token.tokenType().equals("KEYWORD")) {
				// if there are class level variables
				if (token.keyWord().equals("static") | token.keyWord().equals("field")) {
					compileClassVarDec();
				}
				// else if there is a subroutine declaration
				else if (token.keyWord().equals("function") | token.keyWord().equals("method")
						| token.keyWord().equals("constructor")) {
					compileSubroutine();
				}
			}
		}
	}

	/**
	 * Compiles a static declaration or a field declaration
	 * 
	 * @throws IOException
	 */
	private void compileClassVarDec() throws IOException {
		String kind = token.keyWord().toUpperCase();
		token.advance();
		String type = token.identifier();
		token.advance();
		String name = token.identifier();
		symbols.define(name, type, kind); // defines the variable in the symbol table
		token.advance();
		// checks for multiple in line declaration: ex. int x, y;
		while (token.symbol() != ';') {
			token.advance();
			name = token.identifier();
			symbols.define(name, type, kind);
			token.advance();
		}
	}

	/**
	 * Compiles a complete method, function, or constructor
	 * 
	 * @throws IOException
	 */
	private void compileSubroutine() throws IOException {
		boolean declared = false;
		String subType = "function";
		symbols.startSubroutine();
		int nLocals = 0;
		// checks if the subroutine is a constructor or method, which require slightly
		// different setups
		if (token.keyWord().equals("constructor")) {
			object = true;
			subType = "constructor";
		} else if (token.keyWord().equals("method")) {
			subType = "method";
		}
		token.advance();
		token.advance();
		String functionName = token.identifier();
		token.advance();
		// in case there is an identifier that starts with a keyword
		// ex. double = do + uble
		if (token.tokenType().equals("IDENTIFIER")) {
			functionName = functionName + token.identifier();
			token.advance();
		}
		compileParameterList(subType); // compiles argument parameters
		token.advance();
		// repeat until the entire subroutine is finished
		while (token.symbol() != '}') {
			token.advance();
			if (token.tokenType().equals("KEYWORD")) {
				// if there are local level variables
				if (token.keyWord().equals("var")) {
					nLocals += compileVarDec();
				}
				// else if there are statements
				else if (token.keyWord().equals("let") | token.keyWord().equals("if") | token.keyWord().equals("while")
						| token.keyWord().equals("do") | token.keyWord().equals("return")) {
					// if the function has not been explicitly declared yet
					if (!declared) {
						vmwriter.writeFunction(className + "." + functionName, nLocals);
						declared = true;
						// constructor and method setups
						if (subType.equals("constructor")) {
							vmwriter.writePush("CONSTANT", symbols.varCount("FIELD"));
							vmwriter.writeCall("Memory.alloc", 1);
							vmwriter.writePop("POINTER", 0);
						} else if (subType.equals("method")) {
							vmwriter.writePush("ARGUMENT", 0);
							vmwriter.writePop("POINTER", 0);
						}
					}
					compileStatements(); // compiles let, if, while, do, return statements
				}
			}
		}
	}

	/**
	 * Compiles a (possibly empty) parameter list, not including the enclosing "()"
	 * 
	 * @throws IOException
	 */
	private void compileParameterList(String subType) throws IOException {
		// if its a method, put a holder symbol, only to increment index to allow for
		// argument 0 to be this
		if (subType.equals("method")) {
			symbols.define("", "", "ARG");
		}
		token.advance();
		// checks for individual parameters
		while (token.symbol() != ')') {
			String kind = "ARG";
			String type = token.identifier();
			token.advance();
			String name = token.identifier();
			symbols.define(name, type, kind); // defines the variable in the symbol table
			token.advance();
			// checks if there are more parameters
			if (token.symbol() == ',') {
				token.advance();
			}
		}
	}

	/**
	 * Compiles a var declaration
	 * 
	 * @throws IOException
	 */
	private int compileVarDec() throws IOException {
		int nLocals = 0;
		String kind = token.keyWord().toUpperCase();
		token.advance();
		String type = token.identifier();
		token.advance();
		String name = token.identifier();
		symbols.define(name, type, kind);
		nLocals++;
		token.advance();
		// checks if there are multiple inline declarations
		while (token.symbol() != ';') {
			token.advance();
			name = token.identifier();
			symbols.define(name, type, kind);
			nLocals++;
			token.advance();
		}
		return nLocals;
	}

	/**
	 * Compiles a sequence of statements, not including the enclosing "{}"
	 * 
	 * @throws IOException
	 */
	private void compileStatements() throws IOException {
		// repeat until there are no more statements to compile
		while (token.keyWord().equals("do") | token.keyWord().equals("let") | token.keyWord().equals("while")
				| token.keyWord().equals("return") | token.keyWord().equals("if")) {
			if (token.keyWord().equals("do")) { // do statement
				compileDo();
			} else if (token.keyWord().equals("let")) { // let statement
				compileLet();
			} else if (token.keyWord().equals("while")) { // while statement
				compileWhile();
			} else if (token.keyWord().equals("return")) { // return statement
				compileReturn();
			} else if (token.keyWord().equals("if")) { // if statement
				compileIf();
			}
		}
	}

	/**
	 * Compiles a do statement
	 * 
	 * @throws IOException
	 */
	private void compileDo() throws IOException {
		token.advance();
		String functionName = token.identifier();
		token.advance();
		int n = 0;
		// checks for object method calls, ex. ball.move();
		if (token.symbol() == '.') {
			token.advance();
			// if the first token is not in the symbol table, it must be an OS function, ex.
			// Math.multiply()
			if (symbols.indexOf(functionName) == -1) {
				functionName = functionName + "." + token.identifier();
			}
			// otherwise symbol must be translated to its object identity for the VM command
			// and push the object as its first argument
			else {
				if (symbols.kindOf(functionName).equals("VAR")) {
					vmwriter.writePush("LOCAL", symbols.indexOf(functionName));
				} else if (symbols.kindOf(functionName).equals("ARG")) {
					vmwriter.writePush("ARGUMENT", symbols.indexOf(functionName));
				} else if (symbols.kindOf(functionName).equals("STATIC")) {
					vmwriter.writePush("STATIC", symbols.indexOf(functionName));
				} else {
					vmwriter.writePush("THIS", symbols.indexOf(functionName));
				}
				functionName = symbols.typeOf(functionName) + "." + token.identifier();
				n++;
			}
			token.advance();
		}
		// otherwise must be a call to a function in the same class
		else {
			if (object) {
				vmwriter.writePush("POINTER", 0);
				n++;
			}
			functionName = className + "." + functionName;
		}
		token.advance();
		int nArgs = compileExpressionList() + n; // compiles the argument expressions
		vmwriter.writeCall(functionName, nArgs);
		token.advance();
		token.advance();
		vmwriter.writePop("TEMP", 0); // don't care about what is returned from a do statement
	}

	/**
	 * Compiles a let statement
	 * 
	 * @throws IOException
	 */
	private void compileLet() throws IOException {
		token.advance();
		String variable = token.identifier();
		token.advance();
		// checks for arrays
		if (token.symbol() == '[') {
			token.advance();
			compileExpression(); // compiles index expression
			if (symbols.kindOf(variable).equals("VAR")) {
				vmwriter.writePush("LOCAL", symbols.indexOf(variable));
			} else if (symbols.kindOf(variable).equals("ARG")) {
				vmwriter.writePush("ARGUMENT", symbols.indexOf(variable));
			} else if (symbols.kindOf(variable).equals("STATIC")) {
				vmwriter.writePush("STATIC", symbols.indexOf(variable));
			} else {
				vmwriter.writePush("THIS", symbols.indexOf(variable));
			}
			vmwriter.writeArithmetic("ADD"); // a[i] = 5; *(a+i) = 5
			token.advance();
			token.advance();
			compileExpression(); // compiles expression on the other side of =
			vmwriter.writePop("TEMP", 0); // puts value in temp
			vmwriter.writePop("POINTER", 1); // puts a+i as THAT pointer
			vmwriter.writePush("TEMP", 0); // pushes value
			vmwriter.writePop("THAT", 0); // stores in THAT
		} else {
			token.advance();
			compileExpression(); // compiles expression on the other side of = and pops to appropriate location
			if (symbols.kindOf(variable).equals("VAR")) {
				vmwriter.writePop("LOCAL", symbols.indexOf(variable));
			} else if (symbols.kindOf(variable).equals("ARG")) {
				vmwriter.writePop("ARGUMENT", symbols.indexOf(variable));
			} else if (symbols.kindOf(variable).equals("STATIC")) {
				vmwriter.writePop("STATIC", symbols.indexOf(variable));
			} else {
				vmwriter.writePop("THIS", symbols.indexOf(variable));
			}
		}
		token.advance();
	}

	/**
	 * Compiles a while statement
	 * 
	 * @throws IOException
	 */
	private void compileWhile() throws IOException {
		int n = whileN++; // takes index locally, otherwise it would be overridden if there are inner
							// loops
		vmwriter.writeLabel("WHILE_EXP" + n);
		token.advance();
		token.advance();
		compileExpression(); // compiles conditional statement
		vmwriter.writeArithmetic("NOT"); // if conditional statement is not met
		vmwriter.writeIf("WHILE_END" + n); // go to the end of the loop
		token.advance();
		token.advance();
		compileStatements(); // compiles the statements inside of the loop
		token.advance();
		vmwriter.writeGoto("WHILE_EXP" + n); // go back to the beginning of the loop
		vmwriter.writeLabel("WHILE_END" + n);
	}

	/**
	 * Compiles a return statement
	 * 
	 * @throws IOException
	 */
	private void compileReturn() throws IOException {
		token.advance();
		// if there is a value to return, get that
		if (!(token.symbol() == ';')) {
			compileExpression();
		}
		// otherwise return 0
		else {
			vmwriter.writePush("CONSTANT", 0);
		}
		vmwriter.writeReturn();
		token.advance();
	}

	/**
	 * Compiles an if statement, possibly with a trailing else clause
	 * 
	 * @throws IOException
	 */
	private void compileIf() throws IOException {
		int n = ifN++; // takes index locally, otherwise it would be overridden if there are inner ifs
		token.advance();
		token.advance();
		compileExpression(); // compiles conditional statement
		vmwriter.writeIf("IF_TRUE" + n); // if that statement is true, go to IF_TRUE label
		vmwriter.writeGoto("IF_FALSE" + n); // otherwise go to IF_FALSE label
		token.advance();
		token.advance();
		vmwriter.writeLabel("IF_TRUE" + n);
		compileStatements(); // compiles statements in the true section
		vmwriter.writeGoto("IF_END" + n);
		token.advance();
		vmwriter.writeLabel("IF_FALSE" + n);
		// if there is an else statement
		if (token.keyWord().equals("else")) {
			token.advance();
			token.advance();
			compileStatements(); // compiles statements in the false/else section
			token.advance();
		}
		vmwriter.writeLabel("IF_END" + n);
	}

	/**
	 * Compiles a (possibly empty) comma separated list of expressions
	 * 
	 * @throws IOException
	 */
	private int compileExpressionList() throws IOException {
		int numExps = 0;
		// repeat until there are no more expressions
		while (token.symbol() != ')') {
			compileExpression();
			numExps++;
			if (token.symbol() != ')') {
				token.advance();
			}
		}
		return numExps;
	}

	/**
	 * Compiles an expression
	 * 
	 * @throws IOException
	 */
	private void compileExpression() throws IOException {
		compileTerm(); // compiles term in an expression
		// while there are arithmetic symbols, continue to combine into one expression
		while (token.symbol() == '+' | token.symbol() == '-' | token.symbol() == '*' | token.symbol() == '/'
				| token.symbol() == '&' | token.symbol() == '|' | token.symbol() == '<' | token.symbol() == '>'
				| token.symbol() == '=') {
			char symbol = token.symbol();
			token.advance();
			compileTerm(); // compiles the next term of the expression, then performs the operation
			if (symbol == '+') {
				vmwriter.writeArithmetic("ADD");
			} else if (symbol == '-') {
				vmwriter.writeArithmetic("SUB");
			} else if (symbol == '*') {
				vmwriter.writeCall("Math.multiply", 2);
			} else if (symbol == '/') {
				vmwriter.writeCall("Math.divide", 2);
			} else if (symbol == '&') {
				vmwriter.writeArithmetic("AND");
			} else if (symbol == '|') {
				vmwriter.writeArithmetic("OR");
			} else if (symbol == '<') {
				vmwriter.writeArithmetic("LT");
			} else if (symbol == '>') {
				vmwriter.writeArithmetic("GT");
			} else if (symbol == '=') {
				vmwriter.writeArithmetic("EQ");
			}
		}
	}

	/**
	 * Compiles a term
	 * 
	 * @throws IOException
	 */
	private void compileTerm() throws IOException {
		if (token.tokenType().equals("SYMBOL")) {
			// if performing a unary operation
			if (token.symbol() == '-' | token.symbol() == '~') {
				char symbol = token.symbol();
				token.advance();
				compileTerm(); // compile the term, then perform operation
				if (symbol == '-') {
					vmwriter.writeArithmetic("NEG");
				} else if (symbol == '~') {
					vmwriter.writeArithmetic("NOT");
				}
			}
			// or there are expressions within expressions
			else if (token.symbol() == '(') {
				token.advance();
				compileExpression();
				token.advance();
			}
		}
		// if the term is an integer constant
		else if (token.tokenType().equals("INT_CONST")) {
			int value = token.intVal();
			vmwriter.writePush("CONSTANT", value); // push the integer constant
			token.advance();
		}
		// if the term is a string constant
		else if (token.tokenType().equals("STRING_CONST")) {
			String value = token.stringVal();
			vmwriter.writePush("CONSTANT", value.length());
			vmwriter.writeCall("String.new", 1); // allocate space for the string
			for (int i = 0; i < value.length(); i++) { // adds characters to the string
				vmwriter.writePush("CONSTANT", value.charAt(i));
				vmwriter.writeCall("String.appendChar", 2);
			}
			token.advance();
		}
		// if the term is a keyword
		else if (token.tokenType().equals("KEYWORD")) {
			if (token.keyWord().equals("this")) { // this translates to pointer 0
				vmwriter.writePush("POINTER", 0);
			} else if (token.keyWord().equals("true")) { // true translates to 1111...
				vmwriter.writePush("CONSTANT", 0);
				vmwriter.writeArithmetic("NOT");
			} else if (token.keyWord().equals("false")) { // false translates to 0000...
				vmwriter.writePush("CONSTANT", 0);
			} else if (token.keyWord().equals("null")) { // null translates to 0
				vmwriter.writePush("CONSTANT", 0);
			}
			token.advance();
		}
		// otherwise it is an identifier
		else if (token.tokenType().equals("IDENTIFIER")) {
			String prevToken = token.identifier();
			token.advance();
			// checks for arrays
			if (token.symbol() == '[') {
				if (symbols.kindOf(prevToken).equals("VAR")) {
					vmwriter.writePush("LOCAL", symbols.indexOf(prevToken));
				} else if (symbols.kindOf(prevToken).equals("ARG")) {
					vmwriter.writePush("ARGUMENT", symbols.indexOf(prevToken));
				}
				token.advance();
				compileExpression();
				vmwriter.writeArithmetic("ADD");
				vmwriter.writePop("POINTER", 1);
				vmwriter.writePush("THAT", 0);
				token.advance();
			}
			// checks for object function calls
			else if (token.symbol() == '.') {
				token.advance();
				String functionName;
				int n = 0;
				// if the identifier is not in the symbol table, it must be an OS command
				if (symbols.typeOf(prevToken).equals("NONE")) {
					functionName = prevToken + "." + token.identifier();
				}
				// otherwise it must be a function called on a previously declared object
				else {
					vmwriter.writePush("THIS", symbols.indexOf(prevToken));
					functionName = symbols.typeOf(prevToken) + "." + token.identifier();
					n++;
				}
				token.advance();
				// checks for cases of keywords located at the beginning of identifiers
				if (token.tokenType().equals("IDENTIFIER")) {
					functionName = functionName + token.identifier();
				}
				token.advance();
				int nArgs = compileExpressionList(); // compiles the argument expression list
				vmwriter.writeCall(functionName, nArgs + n);
				token.advance();
			}
			// checks for functions called in the same class
			else if (token.symbol() == '(') {
				token.advance();
				int nArgs = compileExpressionList();
				vmwriter.writeCall(className + "." + prevToken, nArgs);
				token.advance();
			}
			// otherwise must just be a variable call
			else {
				if (symbols.kindOf(prevToken).equals("VAR")) {
					vmwriter.writePush("LOCAL", symbols.indexOf(prevToken));
				} else if (symbols.kindOf(prevToken).equals("ARG")) {
					vmwriter.writePush("ARGUMENT", symbols.indexOf(prevToken));
				} else if (symbols.kindOf(prevToken).equals("STATIC")) {
					vmwriter.writePush("STATIC", symbols.indexOf(prevToken));
				} else {
					vmwriter.writePush("THIS", symbols.indexOf(prevToken));
				}
			}
		}
		// writer.write("</term>\n");
	}
}
