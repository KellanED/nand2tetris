
/* 
 * Kellan Delaney
 * Nand2Tetris Virtual Machine Translator
 * Translates parsed strings into assembly code
 * 06/27/2020
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {

	private File f; // output file containing assembly code
	private String VMFilename; // filename of input .vm file - for static symbols
	private String function; // current function being written
	private FileWriter writer; // used to write to the file
	private int n = 0; // incrementing value to ensure unique labels

	/**
	 * Constructor
	 * 
	 * @param f - Output file
	 * @throws IOException
	 */
	public Writer(File f) throws IOException {
		this.f = f;
		writer = new FileWriter(this.f, false);
		function = "";
		this.writeInit();
	}

	/**
	 * Informs the writer than the translation of a new VM file has started
	 * 
	 * @param filename - filename of the VM file
	 */
	public void setFileName(String filename) {
		int x = filename.indexOf('.');
		VMFilename = filename.substring(0, x);
	}

	/**
	 * Writes the assembly code that is the translate of the given arithmetic
	 * command
	 * 
	 * @param command - VM arithmetic command
	 * @throws IOException
	 */
	public void writeArithmetic(String command) throws IOException {
		if (command.equals("add")) {
			writer.write("@SP\n");
			writer.write("M=M-1\n"); // decrement SP
			writer.write("A=M\n");
			writer.write("D=M\n"); // store top of stack in D
			writer.write("@SP\n");
			writer.write("M=M-1\n"); // decrement SP
			writer.write("A=M\n");
			writer.write("M=M+D\n"); // add D to top of stack and store in top of stack
			writer.write("@SP\n");
			writer.write("M=M+1\n"); // increment SP
		} else if (command.equals("sub")) {
			writer.write("@SP\n");
			writer.write("M=M-1\n"); // decrement SP
			writer.write("A=M\n");
			writer.write("D=M\n"); // store top of stack in D
			writer.write("@SP\n");
			writer.write("M=M-1\n"); // decrement SP
			writer.write("A=M\n");
			writer.write("M=M-D\n"); // subtract D to top of stack and store in top of stack
			writer.write("@SP\n");
			writer.write("M=M+1\n"); // increment SP
		} else if (command.equals("neg")) {
			writer.write("@SP\n");
			writer.write("M=M-1\n"); // decrement SP
			writer.write("A=M\n");
			writer.write("D=M\n");
			writer.write("M=M-D\n");
			writer.write("M=M-D\n"); // negates the value at the top of the stack
			writer.write("@SP\n");
			writer.write("M=M+1\n"); // increment SP
		} else if (command.equals("eq")) {
			writer.write("@SP\n");
			writer.write("M=M-1\n"); // decrement SP
			writer.write("A=M\n");
			writer.write("D=M\n"); // store top of stack in D
			writer.write("@SP\n");
			writer.write("M=M-1\n"); // decrement SP
			writer.write("A=M\n");
			writer.write("D=M-D\n");
			writer.write("M=0\n"); // false by default
			writer.write("@TRUE" + n + "\n");
			writer.write("D;JEQ\n"); // jump to true block if equal
			writer.write("@END" + n + "\n");
			writer.write("0;JMP\n"); // otherwise jump to the end
			writer.write("(TRUE" + n + ")\n");
			writer.write("@SP\n");
			writer.write("A=M\n");
			writer.write("M=-1\n");
			writer.write("(END" + n + ")\n");
			n++;
			writer.write("@SP\n");
			writer.write("M=M+1\n"); // increment SP
		} else if (command.equals("gt")) {
			writer.write("@SP\n");
			writer.write("M=M-1\n"); // decrement SP
			writer.write("A=M\n");
			writer.write("D=M\n"); // store top of stack in D
			writer.write("@SP\n");
			writer.write("M=M-1\n"); // decrement SP
			writer.write("A=M\n");
			writer.write("D=M-D\n");
			writer.write("M=0\n"); // false by default
			writer.write("@TRUE" + n + "\n");
			writer.write("D;JGT\n"); // jump to true block if greater
			writer.write("@END" + n + "\n");
			writer.write("0;JMP\n"); // otherwise jump to the end
			writer.write("(TRUE" + n + ")\n");
			writer.write("@SP\n");
			writer.write("A=M\n");
			writer.write("M=-1\n");
			writer.write("(END" + n + ")\n");
			n++;
			writer.write("@SP\n");
			writer.write("M=M+1\n"); // increment SP
		} else if (command.equals("lt")) {
			writer.write("@SP\n");
			writer.write("M=M-1\n"); // decrement SP
			writer.write("A=M\n");
			writer.write("D=M\n"); // store top of stack in D
			writer.write("@SP\n");
			writer.write("M=M-1\n"); // decrement SP
			writer.write("A=M\n");
			writer.write("D=M-D\n");
			writer.write("M=0\n"); // false by default
			writer.write("@TRUE" + n + "\n");
			writer.write("D;JLT\n"); // jump to true block if less
			writer.write("@END" + n + "\n");
			writer.write("0;JMP\n"); // otherwise jump to the end
			writer.write("(TRUE" + n + ")\n");
			writer.write("@SP\n");
			writer.write("A=M\n");
			writer.write("M=-1\n");
			writer.write("(END" + n + ")\n");
			n++;
			writer.write("@SP\n");
			writer.write("M=M+1\n"); // increment SP
		} else if (command.equals("and")) {
			writer.write("@SP\n");
			writer.write("M=M-1\n"); // decrement SP
			writer.write("A=M\n");
			writer.write("D=M\n"); // store top of stack in D
			writer.write("@SP\n");
			writer.write("M=M-1\n"); // decrement SP
			writer.write("A=M\n");
			writer.write("M=M&D\n"); // and D with M to top of stack and store in top of stack
			writer.write("@SP\n");
			writer.write("M=M+1\n"); // increment SP
		} else if (command.equals("or")) {
			writer.write("@SP\n");
			writer.write("M=M-1\n"); // decrement SP
			writer.write("A=M\n");
			writer.write("D=M\n"); // store top of stack in D
			writer.write("@SP\n");
			writer.write("M=M-1\n"); // decrement SP
			writer.write("A=M\n");
			writer.write("M=M|D\n"); // or D with M to top of stack and store in top of stack
			writer.write("@SP\n");
			writer.write("M=M+1\n"); // increment SP
		} else if (command.equals("not")) {
			writer.write("@SP\n");
			writer.write("M=M-1\n"); // decrement SP
			writer.write("A=M\n");
			writer.write("M=-M\n"); // negates the value at the top of the stack
			writer.write("D=M\n");
			writer.write("M=D-1\n"); // adds one to essentially bitwise invert
			writer.write("@SP\n");
			writer.write("M=M+1\n"); // increment SP
		}
	}

	/**
	 * Writes the assembly code translation of the push or pop command
	 * 
	 * @param command - push or pop
	 * @param segment - segment name
	 * @param index   - index within the segment
	 * @throws IOException
	 */
	public void writePushPop(String command, String segment, int index) throws IOException {
		if (command.equals("C_PUSH")) {
			if (segment.equals("constant")) {
				writer.write("@" + index + "\n"); // constant
				writer.write("D=A\n");
				writer.write("@SP\n");
				writer.write("A=M\n");
				writer.write("M=D\n"); // place constant on stack
				writer.write("@SP\n");
				writer.write("M=M+1\n"); // increment SP
			} else if (segment.equals("local")) {
				writer.write("@LCL\n");
				writer.write("D=M\n");
				writer.write("@" + index + "\n");
				writer.write("D=D+A\n");
				writer.write("A=D\n");
				writer.write("D=M\n"); // get value from segment
				writer.write("@SP\n");
				writer.write("A=M\n"); // go to stack
				writer.write("M=D\n"); // place on stack
				writer.write("@SP\n");
				writer.write("M=M+1\n"); // increment SP
			} else if (segment.equals("argument")) {
				writer.write("@ARG\n");
				writer.write("D=M\n");
				writer.write("@" + index + "\n");
				writer.write("D=D+A\n");
				writer.write("A=D\n");
				writer.write("D=M\n"); // get value from segment
				writer.write("@SP\n");
				writer.write("A=M\n"); // go to stack
				writer.write("M=D\n"); // place on stack
				writer.write("@SP\n");
				writer.write("M=M+1\n"); // increment SP
			} else if (segment.equals("this")) {
				writer.write("@THIS\n");
				writer.write("D=M\n");
				writer.write("@" + index + "\n");
				writer.write("D=D+A\n");
				writer.write("A=D\n");
				writer.write("D=M\n"); // get value from segment
				writer.write("@SP\n");
				writer.write("A=M\n"); // go to stack
				writer.write("M=D\n"); // place on stack
				writer.write("@SP\n");
				writer.write("M=M+1\n"); // increment SP
			} else if (segment.equals("that")) {
				writer.write("@THAT\n");
				writer.write("D=M\n");
				writer.write("@" + index + "\n");
				writer.write("D=D+A\n");
				writer.write("A=D\n");
				writer.write("D=M\n"); // get value from segment
				writer.write("@SP\n");
				writer.write("A=M\n"); // go to stack
				writer.write("M=D\n"); // place on stack
				writer.write("@SP\n");
				writer.write("M=M+1\n"); // increment SP
			} else if (segment.equals("pointer")) {
				writer.write("@3\n");
				writer.write("D=A\n");
				writer.write("@" + index + "\n");
				writer.write("D=D+A\n");
				writer.write("A=D\n");
				writer.write("D=M\n"); // get value from segment
				writer.write("@SP\n");
				writer.write("A=M\n"); // go to stack
				writer.write("M=D\n"); // place on stack
				writer.write("@SP\n");
				writer.write("M=M+1\n"); // increment SP
			} else if (segment.equals("temp")) {
				writer.write("@5\n");
				writer.write("D=A\n");
				writer.write("@" + index + "\n");
				writer.write("D=D+A\n");
				writer.write("A=D\n");
				writer.write("D=M\n"); // get value from segment
				writer.write("@SP\n");
				writer.write("A=M\n"); // go to stack
				writer.write("M=D\n"); // place on stack
				writer.write("@SP\n");
				writer.write("M=M+1\n"); // increment SP
			} else if (segment.equals("static")) {
				writer.write("@" + VMFilename + "." + index + "\n");
				writer.write("D=M\n"); // get value at the symbol location
				writer.write("@SP\n");
				writer.write("A=M\n"); // go to stack
				writer.write("M=D\n"); // place on stack
				writer.write("@SP\n");
				writer.write("M=M+1\n"); // increment SP
			} else {
				writer.write("\n");
			}
		} else if (command.equals("C_POP")) {
			if (segment.equals("local")) {
				writer.write("@" + index + "\n");
				writer.write("D=A\n"); // D is index value
				writer.write("@LCL\n");
				writer.write("A=D+M\n");
				writer.write("D=A\n");
				writer.write("@R13\n");
				writer.write("M=D\n"); // R13 has address
				writer.write("@SP\n");
				writer.write("M=M-1\n"); // decrement SP
				writer.write("A=M\n"); // go to SP
				writer.write("D=M\n"); // store value in D
				writer.write("@R13\n");
				writer.write("A=M\n"); // get address from R13
				writer.write("M=D\n"); // store value
			} else if (segment.equals("argument")) {
				writer.write("@" + index + "\n");
				writer.write("D=A\n"); // D is index value
				writer.write("@ARG\n");
				writer.write("A=D+M\n");
				writer.write("D=A\n");
				writer.write("@R13\n");
				writer.write("M=D\n"); // R13 has address
				writer.write("@SP\n");
				writer.write("M=M-1\n"); // decrement SP
				writer.write("A=M\n"); // go to SP
				writer.write("D=M\n"); // store value in D
				writer.write("@R13\n");
				writer.write("A=M\n"); // get address from R13
				writer.write("M=D\n"); // store value
			} else if (segment.equals("this")) {
				writer.write("@" + index + "\n");
				writer.write("D=A\n"); // D is index value
				writer.write("@THIS\n");
				writer.write("A=D+M\n");
				writer.write("D=A\n");
				writer.write("@R13\n");
				writer.write("M=D\n"); // R13 has address
				writer.write("@SP\n");
				writer.write("M=M-1\n"); // decrement SP
				writer.write("A=M\n"); // go to SP
				writer.write("D=M\n"); // store value in D
				writer.write("@R13\n");
				writer.write("A=M\n"); // get address from R13
				writer.write("M=D\n"); // store value
			} else if (segment.equals("that")) {
				writer.write("@" + index + "\n");
				writer.write("D=A\n"); // D is index value
				writer.write("@THAT\n");
				writer.write("A=D+M\n");
				writer.write("D=A\n");
				writer.write("@R13\n");
				writer.write("M=D\n"); // R13 has address
				writer.write("@SP\n");
				writer.write("M=M-1\n"); // decrement SP
				writer.write("A=M\n"); // go to SP
				writer.write("D=M\n"); // store value in D
				writer.write("@R13\n");
				writer.write("A=M\n"); // get address from R13
				writer.write("M=D\n"); // store value
			} else if (segment.equals("pointer")) {
				writer.write("@" + index + "\n");
				writer.write("D=A\n"); // D is index value
				writer.write("@3\n");
				writer.write("A=D+A\n");
				writer.write("D=A\n");
				writer.write("@R13\n");
				writer.write("M=D\n"); // R13 has address
				writer.write("@SP\n");
				writer.write("M=M-1\n"); // decrement SP
				writer.write("A=M\n"); // go to SP
				writer.write("D=M\n"); // store value in D
				writer.write("@R13\n");
				writer.write("A=M\n"); // get address from R13
				writer.write("M=D\n"); // store value
			} else if (segment.equals("temp")) {
				writer.write("@" + index + "\n");
				writer.write("D=A\n"); // D is index value
				writer.write("@5\n");
				writer.write("A=D+A\n");
				writer.write("D=A\n");
				writer.write("@R13\n");
				writer.write("M=D\n"); // R13 has address
				writer.write("@SP\n");
				writer.write("M=M-1\n"); // decrement SP
				writer.write("A=M\n"); // go to SP
				writer.write("D=M\n"); // store value in D
				writer.write("@R13\n");
				writer.write("A=M\n"); // get address from R13
				writer.write("M=D\n"); // store value
			} else if (segment.equals("static")) {
				writer.write("@SP\n");
				writer.write("M=M-1\n"); // decrement SP
				writer.write("A=M\n"); // go to SP
				writer.write("D=M\n"); // get value at top of stack
				writer.write("@" + VMFilename + "." + index + "\n");
				writer.write("M=D\n"); // put value at the symbol location
			} else {
				writer.write("\n");
			}
		}
	}
	
	/**Writes the translation for init function
	 * @throws IOException
	 */
	public void writeInit() throws IOException {
		writer.write("@256\n");
		writer.write("D=A\n");
		writer.write("@SP\n");
		writer.write("M=D\n");
		writer.write("@1\n");
		writer.write("D=-A\n");
		writer.write("@LCL\n");
		writer.write("M=D\n");
		writer.write("@2\n");
		writer.write("D=-A\n");
		writer.write("@ARG\n");
		writer.write("M=D\n");
		writer.write("@3\n");
		writer.write("D=-A\n");
		writer.write("@THIS\n");
		writer.write("M=D\n");
		writer.write("@4\n");
		writer.write("D=-A\n");
		writer.write("@THAT\n");
		writer.write("M=D\n");
		this.writeCall("Sys.init", 0);
	}
	
	/**Writes the translation for creating a label
	 * @param label - name of the label
	 * @throws IOException
	 */
	public void writeLabel(String label) throws IOException {
		writer.write("("+function+"$"+label+")\n");
	}
	
	/**Writes the translation for an unconditional jump
	 * @param label - destination to jump
	 * @throws IOException
	 */
	public void writeGoto(String label) throws IOException {
		writer.write("@"+function+"$"+label+"\n");
		writer.write("0;JMP\n");
	}
	
	/**Writes the translation for a conditional jump
	 * @param label - destination to jump
	 * @throws IOException
	 */
	public void writeIf(String label) throws IOException {
		writer.write("@SP\n");
		writer.write("M=M-1\n"); // decrement SP
		writer.write("A=M\n");
		writer.write("D=M\n"); // gets value at top of stack
		writer.write("@"+function+"$"+label+"\n");
		writer.write("D;JNE\n"); // jump if not zero
	}
	
	/**Writes the translation for a function call
	 * @param functionName - name of the function
	 * @param numArgs - number of arguments already pushed for the function
	 * @throws IOException
	 */
	public void writeCall(String functionName, int numArgs) throws IOException {
		writer.write("@RET_ADDR$"+n+"\n");
		writer.write("D=A\n"); // get return address
		writer.write("@SP\n");
		writer.write("A=M\n");
		writer.write("M=D\n");  // pushes return address to top of stack
		writer.write("@SP\n");
		writer.write("M=M+1\n"); // increment SP
		writer.write("@LCL\n");
		writer.write("D=M\n"); // get LCL value
		writer.write("@SP\n");
		writer.write("A=M\n");
		writer.write("M=D\n"); // pushes LCL value to top of stack
		writer.write("@SP\n");
		writer.write("M=M+1\n"); // increment SP
		writer.write("@ARG\n");
		writer.write("D=M\n"); // get ARG value
		writer.write("@SP\n");
		writer.write("A=M\n");
		writer.write("M=D\n"); // pushes ARG value to top of stack
		writer.write("@SP\n");
		writer.write("M=M+1\n"); // increment SP
		writer.write("@THIS\n");
		writer.write("D=M\n"); // get THIS value
		writer.write("@SP\n");
		writer.write("A=M\n");
		writer.write("M=D\n"); // pushes THIS value to top of stack
		writer.write("@SP\n");
		writer.write("M=M+1\n"); // increment SP
		writer.write("@THAT\n");
		writer.write("D=M\n"); // get THAT value
		writer.write("@SP\n");
		writer.write("A=M\n");
		writer.write("M=D\n"); // pushes THAT value to top of stack
		writer.write("@SP\n");
		writer.write("M=M+1\n"); // increment SP
		writer.write("D=M\n");
		writer.write("@"+numArgs+"\n");
		writer.write("D=D-A\n");
		writer.write("@5\n");
		writer.write("D=D-A\n");
		writer.write("@ARG\n");
		writer.write("M=D\n"); // ARG = SP-n-5
		writer.write("@SP\n");
		writer.write("D=M\n");
		writer.write("@LCL\n");
		writer.write("M=D\n"); // LCL = SP
		writer.write("@"+functionName+"\n");
		writer.write("0;JMP\n");
		writer.write("(RET_ADDR$"+n+")\n");
		n++;
	}
	
	/**Writes the translation for a return call in a function
	 * @throws IOException
	 */
	public void writeReturn() throws IOException {
		writer.write("@LCL\n");
		writer.write("D=M\n");
		writer.write("@R13\n");
		writer.write("M=D\n"); // R13 = FRAME = LCL
		writer.write("@5\n");
		writer.write("D=D-A\n");
		writer.write("A=D\n");
		writer.write("D=M\n");
		writer.write("@R14\n");
		writer.write("M=D\n"); // R14 = RET = *(FRAME-5)
		writer.write("@SP\n");
		writer.write("M=M-1\n"); // decrement SP
		writer.write("A=M\n");
		writer.write("D=M\n"); // get value at top of stack
		writer.write("@ARG\n");
		writer.write("A=M\n");
		writer.write("M=D\n"); // *ARG = pop()
		writer.write("@ARG\n");
		writer.write("D=M\n");
		writer.write("D=D+1\n");
		writer.write("@SP\n");
		writer.write("M=D\n"); // SP = ARG + 1
		writer.write("@R13\n");
		writer.write("D=M\n");
		writer.write("@1\n");
		writer.write("D=D-A\n");
		writer.write("A=D\n");
		writer.write("D=M\n");
		writer.write("@THAT\n");
		writer.write("M=D\n"); // THAT = *(FRAME-1)
		writer.write("@R13\n");
		writer.write("D=M\n");
		writer.write("@2\n");
		writer.write("D=D-A\n");
		writer.write("A=D\n");
		writer.write("D=M\n");
		writer.write("@THIS\n");
		writer.write("M=D\n"); // THIS = *(FRAME-2)
		writer.write("@R13\n");
		writer.write("D=M\n");
		writer.write("@3\n");
		writer.write("D=D-A\n");
		writer.write("A=D\n");
		writer.write("D=M\n");
		writer.write("@ARG\n");
		writer.write("M=D\n"); // ARG = *(FRAME-3)
		writer.write("@R13\n");
		writer.write("D=M\n");
		writer.write("@4\n");
		writer.write("D=D-A\n");
		writer.write("A=D\n");
		writer.write("D=M\n");
		writer.write("@LCL\n");
		writer.write("M=D\n"); // LCL = *(FRAME-4)
		writer.write("@R14\n");
		writer.write("A=M\n");
		writer.write("0;JMP\n"); // goto return address
	}
	
	/**Writes the translation for a function declaration
	 * @param functionName - name of the function
	 * @param numLocals - number of local variables
	 * @throws IOException
	 */
	public void writeFunction(String functionName, int numLocals) throws IOException {
		writer.write("("+functionName+")\n");
		function = functionName;
		for (int i = 0; i < numLocals; i++) {
			writer.write("@SP\n");
			writer.write("A=M\n");
			writer.write("M=0\n"); // push 0
			writer.write("@SP\n");
			writer.write("M=M+1\n"); // increment SP
		}
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
