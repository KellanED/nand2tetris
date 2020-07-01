
/* 
 * Kellan Delaney
 * Nand2Tetris Virtual Machine Translator
 * Driver Class, runs the VM translator
 * 06/27/2020
 * TODO: Utilize console commands to run through an entire directory of .vm files
 */

import java.io.File;
import java.io.IOException;

public class Driver {

	public static void main(String[] args) throws IOException {

		File in = new File("test.vm"); // input file, in vm code
		File out = new File("test.asm"); // output file, in assembly code

		Parser p = new Parser(in);
		Writer w = new Writer(out);
		w.setFileName("test.vm");

		while (p.hasMoreCommands()) {
			p.advance();
			if (p.commandType().equals("C_PUSH")) {
				w.writePushPop("C_PUSH", p.arg1(), Integer.parseInt(p.arg2()));
			} else if (p.commandType().equals("C_POP")) {
				w.writePushPop("C_POP", p.arg1(), Integer.parseInt(p.arg2()));
			} else if (p.commandType().equals("C_ARITHMETIC")) {
				w.writeArithmetic(p.arg1());
			} else if (p.commandType().equals("C_LABEL")) {
				w.writeLabel(p.arg1());
			} else if (p.commandType().equals("C_GOTO")) {
				w.writeGoto(p.arg1());
			} else if (p.commandType().equals("C_IF")) {
				w.writeIf(p.arg1());
			} else if (p.commandType().equals("C_CALL")) {
				w.writeCall(p.arg1(), Integer.parseInt(p.arg2()));
			} else if (p.commandType().equals("C_RETURN")) {
				w.writeReturn();
			} else if (p.commandType().equals("C_FUNCTION")) {
				w.writeFunction(p.arg1(), Integer.parseInt(p.arg2()));
			}
		}

		w.close();
	}

}
