import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Utility functions for reading files
 */
public class FileUtil {
	public static String[] readLines(File file) throws FileNotFoundException {
		// We partake in a considerable amount of tomfoolery
		return read(file).split("\n");
	}

	public static String read(File file) throws FileNotFoundException {
		// Set the delimiter to basically the end of the file:
		Scanner fileScanner = new Scanner(file).useDelimiter("\\Z");
		// Now, `next` will return the rest of the contents of `file`!
		String contents = fileScanner.next();

		fileScanner.close();

		return contents;
	}
}