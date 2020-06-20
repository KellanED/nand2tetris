
/* 
 * Kellan Delaney
 * Nand2Tetris Assembler
 * Driver Class, runs the (2-pass) assembler
 * 06/20/2020
 * TODO: Include GUI, so it is not necessary to change source code or
 *       manipulate file names to work on multiple assembly files.
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Driver {

	public static void main(String[] args) throws IOException {

		File in = new File("test.asm"); // input file, in assembly
		File out = new File("test.hack"); // output file, in machine code

		SymbolTable st = new SymbolTable(); // creates symbol table
		st.addEntry("SP", 0); // adds all predefined symbols
		st.addEntry("LCL", 1);
		st.addEntry("ARG", 2);
		st.addEntry("THIS", 3);
		st.addEntry("THAT", 4);
		st.addEntry("R0", 0);
		st.addEntry("R1", 1);
		st.addEntry("R2", 2);
		st.addEntry("R3", 3);
		st.addEntry("R4", 4);
		st.addEntry("R5", 5);
		st.addEntry("R6", 6);
		st.addEntry("R7", 7);
		st.addEntry("R8", 8);
		st.addEntry("R9", 9);
		st.addEntry("R10", 10);
		st.addEntry("R11", 11);
		st.addEntry("R12", 12);
		st.addEntry("R13", 13);
		st.addEntry("R14", 14);
		st.addEntry("R15", 15);
		st.addEntry("SCREEN", 16384);
		st.addEntry("KBD", 24567);

		/*************************************************************************************************/
		// First run through the assembly file
		Parser p1 = new Parser(in); // allows for slicing and dicing of assembly instructions
		int lines = 0; // counts lines of code

		while (p1.hasMoreCommands()) {
			p1.advance();
			if (p1.commandType().equals("A_COMMAND") || p1.commandType().equals("C_COMMAND")) {
				lines++;
			} else if (p1.commandType().equals("L_COMMAND")) {
				st.addEntry(p1.symbol(), lines); // adds label to the symbol table, associated with its line #
			}
		}
		/*************************************************************************************************/

		/*************************************************************************************************/
		// Second run through assembly file
		Parser p2 = new Parser(in); // more slicing and dicing; need new Parser bc the old one can't restart
		FileWriter writer = new FileWriter(out, false); // to write the machine code to a new file
		int nextAddress = 16; // for new symbols

		while (p2.hasMoreCommands()) {
			p2.advance();
			if (p2.commandType().equals("A_COMMAND")) { // A command
				int x;
				try {
					x = Integer.parseInt(p2.symbol()); // if the symbol is a pure decimal, encode that
				} catch (NumberFormatException e) {
					if (st.contains(p2.symbol())) { // if not, add a new symbol or get a symbol from the
						x = st.getAddress(p2.symbol()); // symbol table, and use that associated address
					} else {
						x = nextAddress;
						st.addEntry(p2.symbol(), nextAddress++);
					}
				}
				writer.write("0" + Code.address(x) + "\n"); // writes machine code instruction to file
			} else if (p2.commandType().equals("C_COMMAND")) { // C command, much simpler: slice up instruction, encode,
																// write
				writer.write("111" + Code.comp(p2.comp()) + Code.dest(p2.dest()) + Code.jump(p2.jump()) + "\n");
			}
		}
		writer.close();
		/*************************************************************************************************/
	}
}
