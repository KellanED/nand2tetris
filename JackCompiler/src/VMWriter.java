
/*
 * Kellan Delaney
 * Nand2Tetris Compiler Part 2: Code Generation
 * VMWriter Class, writes VM commands to a .vm file
 * 07/06/2020
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class VMWriter {

	private File out; // output .vm file
	private FileWriter writer; // writes to the file

	/**
	 * Constructor
	 * 
	 * @param f - output file
	 * @throws IOException
	 */
	public VMWriter(File f) throws IOException {
		out = f;
		writer = new FileWriter(out);
	}

	/**
	 * Writes a VM push command
	 * 
	 * @param segment - "CONSTANT" "ARGUMENT" "LOCAL" "STATIC" "THIS" "THAT"
	 *                "POINTER" "TEMP"
	 * @param index
	 * @throws IOException
	 */
	public void writePush(String segment, int index) throws IOException {
		writer.write("push " + segment.toLowerCase() + " " + index + "\n");
	}

	/**
	 * Writes a VM pop command
	 * 
	 * @param segment - "CONSTANT" "ARGUMENT" "LOCAL" "STATIC" "THIS" "THAT"
	 *                "POINTER" "TEMP"
	 * @param index
	 * @throws IOException
	 */
	public void writePop(String segment, int index) throws IOException {
		writer.write("pop " + segment.toLowerCase() + " " + index + "\n");
	}

	/**
	 * Writes a VM arithmetic command
	 * 
	 * @param command - "ADD" "SUB" "NEG" "EQ" "GT" "LT" "AND" "OR" "NOT"
	 * @throws IOException
	 */
	public void writeArithmetic(String command) throws IOException {
		writer.write(command.toLowerCase() + "\n");
	}

	/**
	 * Writes a VM label command
	 * 
	 * @param label
	 * @throws IOException
	 */
	public void writeLabel(String label) throws IOException {
		writer.write("label " + label + "\n");
	}

	/**
	 * Writes a VM goto command
	 * 
	 * @param label
	 * @throws IOException
	 */
	public void writeGoto(String label) throws IOException {
		writer.write("goto " + label + "\n");
	}

	/**
	 * Writes a VM if-goto command
	 * 
	 * @param label
	 * @throws IOException
	 */
	public void writeIf(String label) throws IOException {
		writer.write("if-goto " + label + "\n");
	}

	/**
	 * Writes a VM call command
	 * 
	 * @param name
	 * @param nArgs
	 * @throws IOException
	 */
	public void writeCall(String name, int nArgs) throws IOException {
		writer.write("call " + name + " " + nArgs + "\n");
	}

	/**
	 * Writes a VM function command
	 * 
	 * @param name
	 * @param nLocals
	 * @throws IOException
	 */
	public void writeFunction(String name, int nLocals) throws IOException {
		writer.write("function " + name + " " + nLocals + "\n");
	}

	/**
	 * Writes a VM return command
	 * 
	 * @throws IOException
	 */
	public void writeReturn() throws IOException {
		writer.write("return\n");
	}

	/**
	 * Closes the output file
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		writer.close();
	}
}
