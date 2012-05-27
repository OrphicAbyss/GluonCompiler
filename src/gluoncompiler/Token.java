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
	public static ArrayList<Token> buildTokens(String word, int line, int position) {
		ArrayList<Token> tokensOut = new ArrayList<>();
		while (!"".equals(word)){
			Token token;
			int wordLen = word.length();

			char first = word.charAt(0);
			// Try building an identifier
			if (Character.isLetter(first))
				token = matchIdentifier(word, line, position);
			else if (Character.isDigit(first))
				token = matchLiteral(word, line, position);
			else
				token = matchOperator(word, line, position);

			if (token != null){
				int size = token.getText().length();
				word = word.substring(size);
				position += size;
			} else {
				// if we didn't find a token, create an unknown token out of it
				token = new Token(TokenType.UNKNOWN,  word.substring(0,1), line, position);
				word = word.substring(1);
				position++;
			}
			
			tokensOut.add(token);
		}
		return tokensOut;
	}

	public static Token createNewlineToken(int line, int position){
		return new Token(TokenType.NEWLINE,"", line, position);
	}

	public static Token createEOFToken(int line, int position){
		return new Token(TokenType.EOF,"", line, position);
	}

	private static Token matchIdentifier(String word, int line, int position){
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
				return new Token(key, line, position);
			else
				return new Token(TokenType.IDENTIFIER, token.toString(), line, position);
		}

		return null;
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

	private static Token matchLiteral(String word, int line, int position){
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
			return new Token(TokenType.LITERAL,token.toString(), line, position);

		return null;
	}

	private static Token matchOperator(String word, int line, int position){
		// Try building a symbol
		for (Operator testOp: Operator.values()) {
			String testOpStr = testOp.getValue();
			int testOpLen = testOpStr.length();
			if (testOpLen <= word.length()){
				String testWord = word.substring(0, testOpLen);
				if (testOpStr.equals(testWord)) {
					return new Token(testOp, line, position);
				}
			}
		}
		
		return null;
	}

	private TokenType type;
	private String text;
	private String value;
	private Keyword keyword;
	private Operator operator;
	private int line;
	private int position;
	private Token next;

	public Token(TokenType type, String value, int line, int position){
		switch (type){
			case IDENTIFIER:
			case KEYWORD:
			case LITERAL:
			case NEWLINE:
			case UNKNOWN:
			case EOF:
				this.type = type;
				this.text = value;
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
		this.text = keyword.name();
		this.value = keyword.name();
		this.keyword = keyword;
		this.operator = null;
		this.line = line;
		this.position = position;
	}

	public Token(Operator operator, int line, int position){
		this.type = TokenType.OPERATOR;
		this.text = operator.getValue();
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
	
	public boolean isOperator(Operator op){
		return type.equals(TokenType.OPERATOR) && operator.equals(op);
	}

	public boolean isLiteral(){
		return type.equals(TokenType.LITERAL);
	}
	
	public boolean isNewline(){
		return type.equals(TokenType.NEWLINE);
	}

	public boolean isEOF(){
		return type.equals(TokenType.EOF);
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

	/**
	 * @return the next
	 */
	public Token getNext() {
		return next;
	}

	/**
	 * @param next the next to set
	 */
	public void setNext(Token next) {
		this.next = next;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
}
