
/* 
 * Kellan Delaney
 * Nand2Tetris Assembler
 * Code class, encodes assembly information into machine code bits
 * 06/20/2020
 */

public class Code {

	/**
	 * Translates destination information into machine code bits
	 * 
	 * @param x - assembly destination information
	 * @return - "d2,d1,d0"
	 */
	public static String dest(String x) {
		if (x.equals("")) {
			return "000";
		} else if (x.equals("M")) {
			return "001";
		} else if (x.equals("D")) {
			return "010";
		} else if (x.equals("MD")) {
			return "011";
		} else if (x.equals("A")) {
			return "100";
		} else if (x.equals("AM")) {
			return "101";
		} else if (x.equals("AD")) {
			return "110";
		} else if (x.equals("AMD")) {
			return "111";
		} else {
			return null;
		}
	}

	/**
	 * Translates computation information into machine code bits
	 * 
	 * @param x - assembly computation information
	 * @return - "a,c5,c4,c3,c2,c1,c0"
	 */
	public static String comp(String x) {
		if (x.equals("0")) {
			return "0101010";
		} else if (x.equals("1")) {
			return "0111111";
		} else if (x.equals("-1")) {
			return "0111010";
		} else if (x.equals("D")) {
			return "0001100";
		} else if (x.equals("A")) {
			return "0110000";
		} else if (x.equals("!D")) {
			return "0001101";
		} else if (x.equals("!A")) {
			return "0110001";
		} else if (x.equals("-D")) {
			return "0001111";
		} else if (x.equals("-A")) {
			return "0110011";
		} else if (x.equals("D+1")) {
			return "0011111";
		} else if (x.equals("A+1")) {
			return "0110111";
		} else if (x.equals("D-1")) {
			return "0001110";
		} else if (x.equals("A-1")) {
			return "0110010";
		} else if (x.equals("D+A")) {
			return "0000010";
		} else if (x.equals("D-A")) {
			return "0010011";
		} else if (x.equals("A-D")) {
			return "0000111";
		} else if (x.equals("D&A")) {
			return "0000000";
		} else if (x.equals("D|A")) {
			return "0010101";
		} else if (x.equals("M")) {
			return "1110000";
		} else if (x.equals("!M")) {
			return "1110001";
		} else if (x.equals("-M")) {
			return "1110011";
		} else if (x.equals("M+1")) {
			return "1110111";
		} else if (x.equals("M-1")) {
			return "1110010";
		} else if (x.equals("D+M")) {
			return "1000010";
		} else if (x.equals("D-M")) {
			return "1010011";
		} else if (x.equals("M-D")) {
			return "1000111";
		} else if (x.equals("D&M")) {
			return "1000000";
		} else if (x.equals("D|M")) {
			return "1010101";
		} else {
			return null;
		}
	}

	/**
	 * Translates jump information into machine code bits
	 * 
	 * @param x - assembly jump information
	 * @return - "j2,j1,j0"
	 */
	public static String jump(String x) {
		if (x.equals("")) {
			return "000";
		} else if (x.equals("JGT")) {
			return "001";
		} else if (x.equals("JEQ")) {
			return "010";
		} else if (x.equals("JGE")) {
			return "011";
		} else if (x.equals("JLT")) {
			return "100";
		} else if (x.equals("JNE")) {
			return "101";
		} else if (x.equals("JLE")) {
			return "110";
		} else if (x.equals("JMP")) {
			return "111";
		} else {
			return null;
		}
	}

	/**
	 * Converts a decimal integer into a 15 bit binary string
	 * 
	 * @param x - decimal integer
	 * @return - 15 bit binary string
	 */
	public static String address(int x) {
		String z = Integer.toBinaryString(x); // converts to binary
		while (z.length() != 15) { // adds zeros until 15 bits
			z = "0" + z;
		}
		return z;
	}

}
