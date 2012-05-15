package gluoncompiler;

import java.util.ArrayList;

/**
 * Represents a token in a source file.
 *
 * Tokens can be of type: Keyword, Identifier, Operator, Literal
 */
public class Token {

	/**
	 * Builds a set of tokens out of the word passed in.
	 *
	 * @param tokens ArrayList to add tokens to
	 * @param word The string to tokenise
	 */
	public static void buildTokens(ArrayList<Token> tokens, String word, int line, int position) {
		while (!"".equals(word)){
			int wordLen = word.length();

			char first = word.charAt(0);
			// Try building an identifier
			if (Character.isLetter(first))
				word = matchIdentifier(tokens, word, line, position);
			else if (Character.isDigit(first))
				word = matchLiteral(tokens, word, line, position);
			else
				word = matchOperator(tokens, word, line, position);

			// If the word length hasn't changed (remove a char as an invalid token)
			if (word.length() == wordLen){
				tokens.add(new Token(TokenType.UNKNOWN,word.substring(0,1), line, -1));
				word = word.substring(1);
				position++;
			} else {
				position += wordLen - word.length();
			}
		}
	}

	public static Token createNewlineToken(int line, int position){
		return new Token(TokenType.NEWLINE,"", line, position);
	}

	public static Token createEOFToken(int line, int position){
		return new Token(TokenType.EOF,"", line, position);
	}

	private static String matchIdentifier(ArrayList tokens, String word, int line, int position){
		StringBuilder token = new StringBuilder();
		int i=0;

		for (; i<word.length(); i++){
			char current = word.charAt(i);
			if (!Character.isLetterOrDigit(current)){
				break;
			}
			token.append(current);
		}

		if (i != 0) {
			Keyword key = testForKeyword(token.toString());
			if (key != null)
				tokens.add(new Token(key, line, position));
			else
				tokens.add(new Token(TokenType.IDENTIFIER, token.toString(), line, position));
		}

		return word.substring(i);
	}

	private static Keyword testForKeyword(String token){
		String upperValue = token.toUpperCase();

		for (Keyword key: Keyword.values()){
			if (key.name().equals(upperValue)){
				return key;
			}
		}

		return null;
	}

	private static String matchLiteral(ArrayList tokens, String word, int line, int position){
		StringBuilder token = new StringBuilder();
		int i = 0;

		for (; i<word.length(); i++){
			char current = word.charAt(i);
			if (!Character.isDigit(current)){
				break;
			}
			token.append(current);
		}

		if (i != 0)
			tokens.add(new Token(TokenType.LITERAL,token.toString(), line, position));

		return word.substring(i);
	}

	private static String matchOperator(ArrayList tokens, String word, int line, int position){
		// Try building a symbol
		for (Operator testOp: Operator.values()) {
			String testOpStr = testOp.getValue();
			int testOpLen = testOpStr.length();
			if (testOpLen <= word.length()){
				String testWord = word.substring(0, testOpLen);
				if (testOpStr.equals(testWord)) {
					tokens.add(new Token(testOp, line, position));
					word = word.substring(testOpLen);
					break;
				}
			}
		}

		return word;
	}

	private TokenType type;
	private String value;
	private Keyword keyword;
	private Operator operator;
	private int line;
	private int position;

	public Token(TokenType type, String value, int line, int position){
		switch (type){
			case IDENTIFIER:
			case KEYWORD:
			case LITERAL:
			case NEWLINE:
			case UNKNOWN:
			case EOF:
				this.type = type;
				this.value = value;
				this.keyword = null;
				this.operator = null;
				this.line = line;
				this.position = position;
				return;
			default:
				Error.abort("Unknown TokenType for creating token: " + type.name() + " With value: " + value);
				break;
		}
	}

	public Token(Keyword keyword, int line, int position){
		this.type = TokenType.KEYWORD;
		this.value = keyword.name();
		this.keyword = keyword;
		this.operator = null;
		this.line = line;
		this.position = position;
	}

	public Token(Operator operator, int line, int position){
		this.type = TokenType.OPERATOR;
		this.value = operator.name();
		this.operator = operator;
		this.keyword = null;
		this.line = line;
		this.position = position;
	}

	public boolean isIdentifier(){
		return type.equals(TokenType.IDENTIFIER);
	}

	public boolean isKeyword(){
		return type.equals(TokenType.KEYWORD);
	}

	public boolean isOperator(){
		return type.equals(TokenType.OPERATOR);
	}

	public boolean isLiteral(){
		return type.equals(TokenType.LITERAL);
	}

	@Override
	public String toString(){
		return getType().toString() + "(line: " + getLine() + ",pos: " + getPosition() + "): " + getValue();
	}

	/**
	 * @return the type
	 */
	public TokenType getType() {
		return type;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return the keyword
	 */
	public Keyword getKeyword() {
		return keyword;
	}

	/**
	 * @return the operator
	 */
	public Operator getOperator() {
		return operator;
	}

	/**
	 * @return the line
	 */
	public int getLine() {
		return line;
	}

	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}
}
