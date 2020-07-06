
/*
 * Kellan Delaney
 * Nand2Tetris Compiler Part 1: Syntax Analyzer
 * CompilationEngine Class, compiles all parts of the program using the tokens, writes syntax to XML file
 * 06/02/2020
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CompilationEngine {

	private Tokenizer token;
	private File output;
	private FileWriter writer;

	public CompilationEngine(Tokenizer input, File output) throws IOException {
		this.token = input;
		this.output = output;
		writer = new FileWriter(this.output, false);
		token.advance();
		compileClass();
		writer.close();
		System.out.println("xd");
	}

	/**
	 * Compiles a complete class
	 * 
	 * @throws IOException
	 */
	private void compileClass() throws IOException {
		writer.write("<class>\n");
		writer.write("<keyword> " + token.keyWord() + " </keyword>\n");
		token.advance();
		writer.write("<identifier> " + token.identifier() + " </identifier>\n");
		token.advance();
		writer.write("<symbol> " + token.symbol() + " </symbol>\n");
		while (token.hasMoreTokens()) {
			token.advance();
			if (token.tokenType().equals("KEYWORD")) {
				if (token.keyWord().equals("static") | token.keyWord().equals("field")) {
					compileClassVarDec();
				} else if (token.keyWord().equals("function") | token.keyWord().equals("method")
						| token.keyWord().equals("constructor")) {
					compileSubroutine();
				}
			}
		}
		writer.write("<symbol> " + token.symbol() + " </symbol>\n");
		writer.write("</class>\n");
	}

	/**
	 * Compiles a static declaration or a field declaration
	 * 
	 * @throws IOException
	 */
	private void compileClassVarDec() throws IOException {
		writer.write("<classVarDec>\n");
		writer.write("<keyword> " + token.keyWord() + " </keyword>\n");
		token.advance();
		if (token.tokenType().equals("KEYWORD")) {
			writer.write("<keyword> " + token.keyWord() + " </keyword>\n");
		} else {
			writer.write("<identifier> " + token.identifier() + " </identifier>\n");
		}
		token.advance();
		writer.write("<identifier> " + token.identifier() + " </identifier>\n");
		token.advance();
		while (token.symbol() != ';') {
			writer.write("<symbol> " + token.symbol() + " </symbol>\n");
			token.advance();
			writer.write("<identifier> " + token.identifier() + " </identifier>\n");
			token.advance();
			if (token.symbol() != ';') {
				writer.write("<symbol> " + token.symbol() + " </symbol>\n");
				token.advance();
			}
		}
		writer.write("<symbol> " + token.symbol() + " </symbol>\n");
		writer.write("</classVarDec>\n");
	}

	/**
	 * Compiles a complete method, function, or constructor
	 * 
	 * @throws IOException
	 */
	private void compileSubroutine() throws IOException {
		writer.write("<subroutineDec>\n");
		writer.write("<keyword> " + token.keyWord() + " </keyword>\n");
		token.advance();
		if (token.tokenType().equals("KEYWORD")) {
			writer.write("<keyword> " + token.keyWord() + " </keyword>\n");
		} else {
			writer.write("<identifier> " + token.identifier() + " </identifier>\n");
		}
		token.advance();
		writer.write("<identifier> " + token.identifier() + " </identifier>\n");
		token.advance();
		writer.write("<symbol> " + token.symbol() + " </symbol>\n");
		compileParameterList();
		writer.write("<symbol> " + token.symbol() + " </symbol>\n");
		writer.write("<subroutineBody>\n");
		token.advance();
		writer.write("<symbol> " + token.symbol() + " </symbol>\n");
		while (token.symbol() != '}') {
			token.advance();
			if (token.tokenType().equals("KEYWORD")) {
				if (token.keyWord().equals("var")) {
					compileVarDec();
				} else if (token.keyWord().equals("let") | token.keyWord().equals("if")
						| token.keyWord().equals("while") | token.keyWord().equals("do")
						| token.keyWord().equals("return")) {
					compileStatements();
				}
			}
		}
		writer.write("<symbol> " + token.symbol() + " </symbol>\n");
		writer.write("</subroutineBody>\n");
		writer.write("</subroutineDec>\n");
	}

	/**
	 * Compiles a (possibly empty) parameter list, not including the enclosing "()"
	 * 
	 * @throws IOException
	 */
	private void compileParameterList() throws IOException {
		writer.write("<parameterList>\n");
		token.advance();
		while (token.symbol() != ')') {
			if (!(token.tokenType().equals("KEYWORD"))) {
				System.out.println(token.tokenType());
				return;
			}
			writer.write("<keyword> " + token.keyWord() + " </keyword>\n");
			token.advance();
			writer.write("<identifier> " + token.identifier() + " </identifier>\n");
			token.advance();
			if (token.symbol() != ')') {
				writer.write("<symbol> " + token.symbol() + " </symbol>\n");
				token.advance();
			}
		}
		writer.write("</parameterList>\n");
	}

	/**
	 * Compiles a var declaration
	 * 
	 * @throws IOException
	 */
	private void compileVarDec() throws IOException {
		writer.write("<varDec>\n");
		writer.write("<keyword> " + token.keyWord() + " </keyword>\n");
		token.advance();
		if (token.tokenType().equals("KEYWORD")) {
			writer.write("<keyword> " + token.keyWord() + " </keyword>\n");
		} else {
			writer.write("<identifier> " + token.identifier() + " </identifier>\n");
		}
		token.advance();
		writer.write("<identifier> " + token.identifier() + " </identifier>\n");
		token.advance();
		while (token.symbol() != ';') {
			writer.write("<symbol> " + token.symbol() + " </symbol>\n");
			token.advance();
			writer.write("<identifier> " + token.identifier() + " </identifier>\n");
			token.advance();
			if (token.symbol() != ';') {
				writer.write("<symbol> " + token.symbol() + " </symbol>\n");
				token.advance();
			}
		}
		writer.write("<symbol> " + token.symbol() + " </symbol>\n");
		writer.write("</varDec>\n");
	}

	/**
	 * Compiles a sequence of statements, not including the enclosing "{}"
	 * 
	 * @throws IOException
	 */
	private void compileStatements() throws IOException {
		writer.write("<statements>\n");
		while (token.keyWord().equals("do") | token.keyWord().equals("let") | token.keyWord().equals("while")
				| token.keyWord().equals("return") | token.keyWord().equals("if")) {
			if (token.keyWord().equals("do")) {
				compileDo();
			} else if (token.keyWord().equals("let")) {
				compileLet();
			} else if (token.keyWord().equals("while")) {
				compileWhile();
			} else if (token.keyWord().equals("return")) {
				compileReturn();
			} else if (token.keyWord().equals("if")) {
				compileIf();
			}
		}
		writer.write("</statements>\n");
	}

	/**
	 * Compiles a do statement
	 * 
	 * @throws IOException
	 */
	private void compileDo() throws IOException {
		writer.write("<doStatement>\n");
		writer.write("<keyword> " + token.keyWord() + " </keyword>\n");
		token.advance();
		writer.write("<identifier> " + token.identifier() + " </identifier>\n");
		token.advance();
		if (token.symbol() == '.') {
			writer.write("<symbol> " + token.symbol() + " </symbol>\n");
			token.advance();
			writer.write("<identifier> " + token.identifier() + " </identifier>\n");
			token.advance();
		}
		writer.write("<symbol> " + token.symbol() + " </symbol>\n");
		token.advance();
		compileExpressionList();
		writer.write("<symbol> " + token.symbol() + " </symbol>\n");
		token.advance();
		writer.write("<symbol> " + token.symbol() + " </symbol>\n");
		writer.write("</doStatement>\n");
		token.advance();
	}

	/**
	 * Compiles a let statement
	 * 
	 * @throws IOException
	 */
	private void compileLet() throws IOException {
		writer.write("<letStatement>\n");
		writer.write("<keyword> " + token.keyWord() + " </keyword>\n");
		token.advance();
		writer.write("<identifier> " + token.identifier() + " </identifier>\n");
		token.advance();
		if (token.symbol() == '[') {
			writer.write("<symbol> " + token.symbol() + " </symbol>\n");
			token.advance();
			compileExpression();
			writer.write("<symbol> " + token.symbol() + " </symbol>\n");
			token.advance();
		}
		writer.write("<symbol> " + token.symbol() + " </symbol>\n");
		token.advance();
		compileExpression();
		writer.write("<symbol> " + token.symbol() + " </symbol>\n");
		writer.write("</letStatement>\n");
		token.advance();
	}

	/**
	 * Compiles a while statement
	 * 
	 * @throws IOException
	 */
	private void compileWhile() throws IOException {
		writer.write("<whileStatement>\n");
		writer.write("<keyword> " + token.keyWord() + " </keyword>\n");
		token.advance();
		writer.write("<symbol> " + token.symbol() + " </symbol>\n");
		token.advance();
		compileExpression();
		writer.write("<symbol> " + token.symbol() + " </symbol>\n");
		token.advance();
		writer.write("<symbol> " + token.symbol() + " </symbol>\n");
		token.advance();
		compileStatements();
		writer.write("<symbol> " + token.symbol() + " </symbol>\n");
		writer.write("</whileStatement>\n");
		token.advance();
	}

	/**
	 * Compiles a return statement
	 * 
	 * @throws IOException
	 */
	private void compileReturn() throws IOException {
		writer.write("<returnStatement>\n");
		writer.write("<keyword> " + token.keyWord() + " </keyword>\n");
		token.advance();
		if (!(token.symbol() == ';')) {
			compileExpression();
		}
		writer.write("<symbol> " + token.symbol() + " </symbol>\n");
		writer.write("</returnStatement>\n");
		token.advance();
	}

	/**
	 * Compiles an if statement, possibly with a trailing else clause
	 * 
	 * @throws IOException
	 */
	private void compileIf() throws IOException {
		writer.write("<ifStatement>\n");
		writer.write("<keyword> " + token.keyWord() + " </keyword>\n");
		token.advance();
		writer.write("<symbol> " + token.symbol() + " </symbol>\n");
		token.advance();
		compileExpression();
		writer.write("<symbol> " + token.symbol() + " </symbol>\n");
		token.advance();
		writer.write("<symbol> " + token.symbol() + " </symbol>\n");
		token.advance();
		compileStatements();
		writer.write("<symbol> " + token.symbol() + " </symbol>\n");
		token.advance();
		if (token.keyWord().equals("else")) {
			writer.write("<keyword> " + token.keyWord() + " </keyword>\n");
			token.advance();
			writer.write("<symbol> " + token.symbol() + " </symbol>\n");
			token.advance();
			compileStatements();
			writer.write("<symbol> " + token.symbol() + " </symbol>\n");
			token.advance();
		}
		writer.write("</ifStatement>\n");
	}

	/**
	 * Compiles a (possibly empty) comma separated list of expressions
	 * 
	 * @throws IOException
	 */
	private void compileExpressionList() throws IOException {
		writer.write("<expressionList>\n");
		while (token.symbol() != ')') {
			compileExpression();
			if (token.symbol() != ')') {
				writer.write("<symbol> " + token.symbol() + " </symbol>\n");
				token.advance();
			}
		}
		writer.write("</expressionList>\n");
	}

	/**
	 * Compiles an expression
	 * 
	 * @throws IOException
	 */
	private void compileExpression() throws IOException {
		writer.write("<expression>\n");
		compileTerm();
		while (token.symbol() == '+' | token.symbol() == '-' | token.symbol() == '*' | token.symbol() == '/'
				| token.symbol() == '&' | token.symbol() == '|' | token.symbol() == '<' | token.symbol() == '>'
				| token.symbol() == '=') {
			writer.write("<symbol> " + token.symbol() + " </symbol>\n");
			token.advance();
			compileTerm();
		}
		writer.write("</expression>\n");
	}

	/**
	 * Compiles a term
	 * 
	 * @throws IOException
	 */
	private void compileTerm() throws IOException {
		writer.write("<term>\n");
		if (token.tokenType().equals("SYMBOL")) {
			if (token.symbol() == '-' | token.symbol() == '~') {
				writer.write("<symbol> " + token.symbol() + " </symbol>\n");
				token.advance();
				compileTerm();
			} else if (token.symbol() == '(') {
				writer.write("<symbol> " + token.symbol() + " </symbol>\n");
				token.advance();
				compileExpression();
				writer.write("<symbol> " + token.symbol() + " </symbol>\n");
				token.advance();
			}
		} else if (token.tokenType().equals("INT_CONST")) {
			writer.write("<integerConstant> " + token.intVal() + " </integerConstant>\n");
			token.advance();
		} else if (token.tokenType().equals("STRING_CONST")) {
			writer.write("<stringConstant> " + token.stringVal() + " </stringConstant>\n");
			token.advance();
		} else if (token.tokenType().equals("KEYWORD")) {
			writer.write("<keyword> " + token.keyWord() + " </keyword>\n");
			token.advance();
		} else if (token.tokenType().equals("IDENTIFIER")) {
			String prevToken = token.identifier();
			token.advance();
			if (token.symbol() == '[') {
				writer.write("<identifier> " + prevToken + " </identifier>\n");
				writer.write("<symbol> " + token.symbol() + " </symbol>\n");
				token.advance();
				compileExpression();
				writer.write("<symbol> " + token.symbol() + " </symbol>\n");
				token.advance();
			} else if (token.symbol() == '.') {
				writer.write("<identifier> " + prevToken + " </identifier>\n");
				writer.write("<symbol> " + token.symbol() + " </symbol>\n");
				token.advance();
				writer.write("<identifier> " + token.identifier() + " </identifier>\n");
				token.advance();
				writer.write("<symbol> " + token.symbol() + " </symbol>\n");
				token.advance();
				compileExpressionList();
				writer.write("<symbol> " + token.symbol() + " </symbol>\n");
				token.advance();
			} else if (token.symbol() == '(') {
				writer.write("<identifier> " + prevToken + " </identifier>\n");
				writer.write("<symbol> " + token.symbol() + " </symbol>\n");
				token.advance();
				compileExpressionList();
				writer.write("<symbol> " + token.symbol() + " </symbol>\n");
				token.advance();
			} else {
				writer.write("<identifier> " + prevToken + " </identifier>\n");
			}
		}
		writer.write("</term>\n");
	}
}
