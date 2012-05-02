package simplecompiler;

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

	static int lineNumber;
	static int position;

	static Scanner input;	// Source stream
	static String line;		// Next source line
	static char current;		// Lookahead character

	/**
	 * Read character from InputStream
	 *
	 * @throws Exception
	 */
	public static void getChar() {
		if (position >= line.length()){
			line = input.nextLine();
			line = line + "\n";
			position = 0;
			lineNumber++;
		}

		current = line.charAt(position);
		position++;
	}

	/**
	 * @return true if the current character is whitespace
	 */
	public static boolean isWhitespace(char c){
		return c == SPACE || c == TAB;
	}

	/**
	 * Test if the character is an alpha char.
	 *
	 * @param c Character to test
	 * @return true if the character is an alpha char
	 */
	public static boolean isAlpha(char c){
		return Character.isAlphabetic(c);
	}

	/**
	 * Test if the current character is an alpha char.
	 *
	 * @return true if the current character is an alpha char
	 */
	public static boolean isAlpha(){
		return isAlpha(current);
	}

	/**
	 * Test if the character is a digit.
	 *
	 * @param c Character to test
	 * @return true if the character is a digit
	 */
	public static boolean isDigit(char c){
		return Character.isDigit(c);
	}

	/**
	 * Test if the current character is a digit.
	 *
	 * @return true if the current character is a digit
	 */
	public static boolean isDigit(){
		return isDigit(current);
	}

	/**
	 * Move scanner to the next non-whitespace character. If we are
	 * already at a non-whitespace character, nothing is done.
	 */
	public static void skipWhitespace(){
		while (isWhitespace(current)){
			getChar();
		}
	}

	/**
	 * Match a specific character and move ahead in scanner. Error's out
	 * if the character doesn't match.
	 *
	 * @param c Character expected in the stream
	 */
	static void matchAndAccept(char c){
		GluonScanner.skipWhitespace();
		if (current == c) {
			getChar();
			skipWhitespace();
		} else
			Error.expected("'" + c + "'");
	}

	/**
	 * Match a specific character but don't move ahead in scanner.
	 *
	 * @param c Character to match
	 * @return true if the character matches the current char
	 */
	static boolean matchOnly(char c){
		skipWhitespace();
		return (current == c);
	}

	public static void init(){
		input = new Scanner(System.in);
		line = "";
		position = 0;
		lineNumber = 1;
		getChar();
	}
}
