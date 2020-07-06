
/*
 * Kellan Delaney
 * Nand2Tetris Compiler Part 1: Syntax Analyzer
 * Driver Class, runs the syntax analyzer
 * 06/02/2020
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Driver {

	public static void main(String[] args) throws IOException {
		String current = System.getProperty("user.dir");
		String dest = current + "\\" + args[0];
		Path path = Paths.get(dest);

		// First checks if the input is a directory
		if (Files.exists(path)) {
			DirectoryStream<Path> stream = Files.newDirectoryStream(path);
			for (Path file : stream) {
				String filename = file.getFileName().toString();
				if (filename.contains(".jack")) {
					// creates output file, tokenizer and compilation engine for each .jack
					String output = filename.substring(0, filename.indexOf(".jack")) + ".xml";
					Tokenizer tokenizer = new Tokenizer(new File(filename));
					CompilationEngine engine = new CompilationEngine(tokenizer, new File(output));
				}
			}
		}
		// Otherwise it should be a single .jack file
		else {
			dest = dest + ".jack";
			path = Paths.get(dest);
			if (Files.exists(path)) {
				// creates an output file, tokenizer and compilation engine
				String filename = path.getFileName().toString();
				String output = filename.substring(0, filename.indexOf(".jack")) + ".xml";
				Tokenizer tokenizer = new Tokenizer(new File(filename));
				CompilationEngine engine = new CompilationEngine(tokenizer, new File(output));
			}
		}
	}

}
