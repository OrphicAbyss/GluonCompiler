package simplecompiler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Used to parse a source file into tokens.
 */
class Tokeniser {
	private ArrayList<Token> tokens;
	private InputStream source;

	/**
	 * Create a tokeniser instance to parse a source file into tokens.
	 *
	 * @param source data to tokenise
	 */
	public Tokeniser(InputStream source){
		this.source = source;
	}

	/**
	 * Loads in the source data and matches it to Tokens.
	 */
	public void tokenise() throws IOException{
		int nextChar;
		StringBuilder sb = new StringBuilder();
		String token;

		while ((nextChar = source.read()) != -1){
			sb.append(Character.toString((char)nextChar));
		}
	}

	/**
	 * Gets the next token in the list of tokens.
	 */
	public Token getCurrentToken(){
		return null;
	}

	/**
	 * Print out the list of tokens for debugging.
	 */
	public void printTokens(){

	}
}
