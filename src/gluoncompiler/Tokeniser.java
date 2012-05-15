package gluoncompiler;

import java.io.File;
import java.util.ArrayList;

/**
 * Parses a stream of characters into a stream of tokens.
 */
class Tokeniser {
	ArrayList<Token> tokens;
	int currentToken;
	GluonScanner scanner;

	public static void main(String[] args){
		GluonScanner scanner = new GluonScanner(new File("testProg.txt"));
		Tokeniser tk = new Tokeniser(scanner);
		tk.tokenise();
		tk.printTokens();
	}

	
	public Tokeniser(GluonScanner scanner){
		this.scanner = scanner;
		currentToken = 0;
		tokens = new ArrayList<>();
	}
	
	public void printTokens(){
		for (Token token: tokens){
			System.out.println(token);
		}
	}
	
	/**
	 * Create tokens
	 */
	public void tokenise(){
		while (!scanner.isEOF()){
			String word = scanner.getWord();
			if (!"".equals(word)){
				Token.buildTokens(tokens, word, scanner.lineNumber, scanner.position);
				if (scanner.current == '\n'){
					tokens.add(Token.createNewlineToken(scanner.lineNumber, scanner.position));
				}
			}
			scanner.skipWhitespace();
		}
		tokens.add(Token.createEOFToken(scanner.lineNumber, scanner.position));
	}

	public Token getPreviousToken(){
		if (tokens.size() <= currentToken - 1 || currentToken == 0)
			return null;
		return tokens.get(currentToken - 1);
	}
	
	/**
	 * Get the current token in the list of tokens
	 */
	public Token getCurrentToken(){
		if (tokens.size() <= currentToken){
			return null;
		}
		return tokens.get(currentToken);
	}

	public Operator getCurrentOperator(){
		return getCurrentToken().getOperator();
	}

	/**
	 * Advance in our list of tokens
	 */
	public void nextToken(){
		currentToken++;
	}

	/**
	 * Error if a token type doesn't match the expected type.
	 */
	public void matchTokenType(TokenType expected, String statementType){
		if (!getCurrentToken().getType().equals(expected))
			Error.expected(expected, getCurrentToken(), statementType);
	}

	public void matchOperator(Operator expected, String statementType){
		if (!expected.equals(getCurrentToken().getOperator()))
			Error.expected(expected, getCurrentToken(), statementType);
		nextToken();
	}

	public void matchNewline(String statementType){
		matchTokenType(TokenType.NEWLINE, statementType);
		nextToken();
	}

	public boolean testTokenType(TokenType expected){
		return getCurrentToken().getType().equals(expected);
	}

	public boolean testOperator(Operator expected){
		return expected.equals(getCurrentToken().getOperator());
	}

	public boolean testOperators(Operator[] expected){
		boolean result = false;
		for (Operator op: expected){
			result = result || testOperator(op);
		}
		return result;
	}
	
	boolean testKeyword(Keyword end) {
		return (end != null && end.equals(getCurrentToken().getKeyword()));
	}


	public boolean testEOF(){
		return testTokenType(TokenType.EOF);
	}	
}
