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

	public GluonScanner(File file){
		try {
			input = new Scanner(file);
			initVars();
		} catch (FileNotFoundException ex) {
			Error.abort("Error reading file: FileNotFound.");
		}
	}

	public GluonScanner(){
		input = new Scanner(System.in);
		initVars();
	}

	private void initVars(){
		eof = false;
		getChar();
		skipWhitespace();
	}

	/**
	 * Returns true when we reach the end of the file
	 */
	public boolean isEOF(){
		return eof;
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
	 * @return true if the current character is whitespace
	 */
	public boolean isWhitespace(char c){
		return c == SPACE || c == TAB;
	}

	/**
	 * Test if the character is an alpha char.
	 *
	 * @param c Character to test
	 * @return true if the character is an alpha char
	 */
	public boolean isAlpha(char c){
		return Character.isAlphabetic(c);
	}

	/**
	 * Test if the current character is an alpha char.
	 *
	 * @return true if the current character is an alpha char
	 */
	public boolean isAlpha(){
		return isAlpha(current);
	}

	/**
	 * Test if the next token is an identifier. If it is, build the
	 * identifier out of the stream.
	 *
	 * @return The identifier
	 */
	public String getIdentifier(){
		if (!isAlpha(current))
			Error.expected("Identifier", null);

		StringBuilder ident = new StringBuilder();

		while (isAlpha(current) || isDigit(current)){
			ident.append(current);
			getChar();
		}

		skipWhitespace();
		return ident.toString();
	}

	/**
	 * Test if the character is a digit.
	 *
	 * @param c Character to test
	 * @return true if the character is a digit
	 */
	public boolean isDigit(char c){
		return Character.isDigit(c);
	}

	/**
	 * Test if the current character is a digit.
	 *
	 * @return true if the current character is a digit
	 */
	public boolean isDigit(){
		return isDigit(current);
	}

	/**
	 * Test if the next token is an integer. If it is, build the integer
	 * out of the stream.
	 *
	 * @return The integer found
	 */
	String getInteger(){
		if (!isDigit(current))
			Error.expected("Integer", null);

		StringBuilder integer = new StringBuilder();
		while (isDigit(current)){
			integer.append(current);
			getChar();
		}

		skipWhitespace();
		return integer.toString();
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

	/**
	 * Match a specific character and move ahead in scanner. Error's out
	 * if the character doesn't match.
	 *
	 * @param expectedChar Character expected in the stream
	 */
	void matchAndAccept(char expectedChar, String matching){
		skipWhitespace();
		if (current == expectedChar) {
			getChar();
			skipWhitespace();
		} else {
			if (expectedChar == '\n')
				Error.expected("new line", matching);
			else
				Error.expected("'" + expectedChar + "'", matching);
		}
	}

	/**
	 * Match a specific character but don't move ahead in scanner.
	 *
	 * @param expectedChar Character to match
	 * @return true if the character matches the current char
	 */
	boolean matchOnly(char expectedChar){
		skipWhitespace();
		return (current == expectedChar);
	}
}
