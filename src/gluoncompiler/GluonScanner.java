package gluoncompiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Scanner class which will buffer the source input and allow it to be
 * read a character at a time.
 *
 * Keeps track of where we are in the file for error messages.
 */
public class GluonScanner {
	static final char TAB = '\t';
	static final char SPACE = ' ';

	int lineNumber = 0;
	int position = 0;

	Scanner input;	// Source stream
	String line = "";		// Next source line
	char current;		// Lookahead character
	boolean eof;

	/**
	 * Create a scanner to read a file
	 *
	 * @param file
	 */
	public GluonScanner(File file){
		try {
			input = new Scanner(file);
			initVars();
		} catch (FileNotFoundException ex) {
			Error.abort("Error reading file: FileNotFound.");
		}
	}

	/**
	 * Create a scanner which reads chars off standard in
	 */
	public GluonScanner(){
		input = new Scanner(System.in);
		initVars();
	}

	/**
	 * Setup variables
	 */
	private void initVars(){
		eof = false;
		getChar();
		skipWhitespace();
	}

	/**
	 * Read character from InputStream
	 */
	public void getChar() {
		try {
			if (position >= line.length()){
				line = input.nextLine();
				line = line + "\n";
				position = 0;
				lineNumber++;

				// only a period on a line means eof
				if (".\n".equals(line))
					eof = true;
			}

			current = line.charAt(position);
			position++;
		} catch (NoSuchElementException nsee){
			eof = true;
		}
	}

	/**
	 * Get the next word in the line
	 */
	public String getWord(){
		StringBuilder sb = new StringBuilder();

		while (!isWhitespace()){
			sb.append(current);
			getChar();
		}

		return sb.toString();
	}

	/**
	 * Returns true when we reach the end of the file
	 */
	public boolean isEOF(){
		return eof;
	}

	/**
	 * Test if the character is a whitespace char.
	 *
	 * @param c Character to test
	 * @return true if the character is whitespace
	 */
	public boolean isWhitespace(char c){
		return c == SPACE || c == TAB || c == '\n';
	}

	/**
	 * Test if the current character is a whitespace char.
	 *
	 * @return true if the current character is whitespace
	 */
	public boolean isWhitespace(){
		return isWhitespace(current);
	}
	
	/**
	 * Move scanner to the next non-whitespace character. If we are
	 * already at a non-whitespace character, nothing is done.
	 */
	public void skipWhitespace(){
		while (isWhitespace(current)){
			getChar();
		}
	}
}
